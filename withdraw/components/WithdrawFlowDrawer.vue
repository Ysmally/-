<template>
  <Transition name="drawer-layer">
    <div v-if="visible" class="drawer-layer">
      <div class="drawer-mask" @click="emit('close')"></div>
      <Transition name="drawer-shell">
        <div v-if="visible" class="drawer-shell" :class="shellClass" :style="shellStyle">
          <div ref="measureRef" class="drawer-inner">
            <div ref="headerRef" class="drawer-header">
              <button
                class="header-btn back-btn"
                :class="{ hidden: !showBack }"
                :disabled="!showBack"
                @click="emit('back')"
              >
                &lt;
              </button>
              <button class="header-btn close-btn" @click="emit('close')">×</button>
            </div>

            <div class="drawer-content">
              <Transition
                :name="contentTransitionName"
                mode="out-in"
                @after-enter="handlePaneAfterEnter"
                @after-leave="handlePaneAfterLeave"
              >
                <div :key="mode" class="drawer-pane">
                  <div v-if="mode === 'accountSelect'" class="drawer-body drawer-body-account">
                    <div class="account-list">
                      <WithdrawAccountCard
                        v-for="account in accounts"
                        :key="account.id"
                        :account="account"
                        :selected="account.id === selectedId"
                        :was-selected="account.id === previousSelectedId"
                        @select="emit('selectAccount', account.id)"
                      />
                    </div>
                    <div class="drawer-footer">
                      <button class="primary-btn" @click="emit('bindBankCard')">绑定银行卡</button>
                    </div>
                  </div>

                  <div v-else-if="mode === 'identityVerify'" class="drawer-body drawer-body-verify">
                    <div class="identity-row">
                      <input
                        :value="realName"
                        placeholder="请输入姓名"
                        @input="emit('update:realName', ($event.target as HTMLInputElement).value)"
                      />
                    </div>
                    <div class="identity-row">
                      <input
                        :value="identityNo"
                        maxlength="18"
                        placeholder="请输入身份证"
                        @input="emit('update:identityNo', ($event.target as HTMLInputElement).value)"
                      />
                    </div>
                    <button class="primary-btn verify-btn" @click="emit('verifyIdentity')">验证身份</button>
                  </div>

                  <div v-else-if="mode === 'bindBankCard'" class="drawer-body drawer-body-bind">
                    <div class="form-field readonly-field">
                      <input :value="form.verifiedRealName" placeholder="姓名" readonly />
                    </div>
                    <div class="form-field readonly-field">
                      <input :value="form.verifiedIdentityNo" placeholder="身份证" readonly />
                    </div>
                    <div class="form-field with-action">
                      <input
                        :value="form.bankCardNo"
                        placeholder="请输入银行卡账号"
                        @input="updateField('bankCardNo', ($event.target as HTMLInputElement).value)"
                        @blur="emit('validateBankCard')"
                      />
                      <button class="scan-btn" aria-label="scan qr code" @click="emit('scanBankCard')"></button>
                    </div>
                    <div class="form-field">
                      <input
                        :value="form.bankName"
                        placeholder="请输入所属银行"
                        @input="updateField('bankName', ($event.target as HTMLInputElement).value)"
                        @blur="emit('validateBankName')"
                      />
                    </div>
                    <div class="form-field">
                      <input
                        :value="form.phone"
                        placeholder="请输入绑定手机号"
                        @input="updateField('phone', ($event.target as HTMLInputElement).value)"
                        @blur="emit('validatePhone')"
                      />
                    </div>
                    <div class="form-field with-action">
                      <input
                        :value="form.verificationCode"
                        maxlength="6"
                        placeholder="请输入手机验证码"
                        @input="updateField('verificationCode', ($event.target as HTMLInputElement).value)"
                      />
                      <button class="send-code-btn" @click="emit('sendCode')">获取验证码</button>
                    </div>
                    <button class="primary-btn bind-btn" @click="emit('submitBindBankCard')">绑定账号</button>
                  </div>
                </div>
              </Transition>
            </div>
          </div>
        </div>
      </Transition>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import type { BindBankForm, WithdrawAccount, WithdrawDrawer } from '../types';
import { normalizeBankCard, normalizePhone, sanitizeName } from '../security';
import WithdrawAccountCard from './WithdrawAccountCard.vue';

const props = defineProps<{
  // 抽屉是否可见
  visible: boolean;
  mode: WithdrawDrawer;
  // 已绑定账户列表
  accounts: WithdrawAccount[];
  // 当前选中的账户 ID
  selectedId: string;
  // 选中迁移动画中的上一次账户 ID
  previousSelectedId?: string;
  // 身份验证 - 姓名
  realName: string;
  // 身份验证 - 身份证号
  identityNo: string;
  //绑定银行卡表单数据
  form: BindBankForm;
}>();

const emit = defineEmits<{
  (event: 'close'): void;
  (event: 'back'): void;
  (event: 'selectAccount', id: string): void;
  (event: 'bindBankCard'): void;
  (event: 'update:realName', value: string): void;
  (event: 'update:identityNo', value: string): void;
  (event: 'verifyIdentity'): void;
  (event: 'update:form', value: BindBankForm): void;
  (event: 'scanBankCard'): void;
  (event: 'validateBankCard'): void;
  (event: 'validateBankName'): void;
  (event: 'validatePhone'): void;
  (event: 'sendCode'): void;
  (event: 'submitBindBankCard'): void;
}>();

const shellClass = computed(() => ({
  'shell-account': props.mode === 'accountSelect',
  'shell-verify': props.mode === 'identityVerify',
  'shell-bind': props.mode === 'bindBankCard',
}));

const showBack = computed(() => props.mode !== 'accountSelect');
const shellHeight = ref<number | null>(null);
const measureRef = ref<HTMLElement | null>(null);
const headerRef = ref<HTMLElement | null>(null);
const contentTransitionName = ref('content-slide-left');

let resizeObserver: ResizeObserver | null = null;

const modeOrder: Record<Exclude<WithdrawDrawer, ''>, number> = {
  accountSelect: 0,
  identityVerify: 1,
  bindBankCard: 2,
};

const shellStyle = computed(() => {
  if (!shellHeight.value) {
    return undefined;
  }
  return {
    height: `${shellHeight.value}px`,
  };
});

function handlePaneAfterEnter() {
  syncShellHeight();
}

function handlePaneAfterLeave() {
  syncShellHeight();
}

function updateContentTransition(newMode: WithdrawDrawer, oldMode: WithdrawDrawer) {
  if (!newMode || !oldMode) {
    contentTransitionName.value = 'content-slide-left';
    return;
  }
  const newOrder = modeOrder[newMode as Exclude<WithdrawDrawer, ''>];
  const oldOrder = modeOrder[oldMode as Exclude<WithdrawDrawer, ''>];
  contentTransitionName.value = newOrder >= oldOrder ? 'content-slide-left' : 'content-slide-right';
}

async function syncShellHeight() {
  if (!props.visible) {
    return;
  }
  await nextTick();
  refreshResizeObserverTargets();
  await new Promise<void>((resolve) => {
    requestAnimationFrame(() => resolve());
  });
  const firstHeight = getTargetShellHeight();
  if (firstHeight > 0) {
    shellHeight.value = firstHeight;
  }

  window.setTimeout(() => {
    const secondHeight = getTargetShellHeight();
    if (secondHeight > 0 && secondHeight !== shellHeight.value) {
      shellHeight.value = secondHeight;
    }
  }, 40);
}

function getTargetShellHeight() {
  const root = measureRef.value;
  if (!root) {
    return 0;
  }
  const headerHeight = headerRef.value?.offsetHeight || 0;
  const activePane = root.querySelector('.drawer-pane') as HTMLElement | null;
  const paneHeight = activePane?.scrollHeight || 0;
  return headerHeight + paneHeight;
}

function refreshResizeObserverTargets() {
  if (!resizeObserver) {
    return;
  }
  resizeObserver.disconnect();
  if (headerRef.value) {
    resizeObserver.observe(headerRef.value);
  }
  const activePane = measureRef.value?.querySelector('.drawer-pane') as HTMLElement | null;
  if (activePane) {
    resizeObserver.observe(activePane);
  }
}

function updateField(field: keyof BindBankForm, value: string) {
  emit('update:form', {
    ...props.form,
    [field]: sanitizeField(field, value),
  });
}


function sanitizeField(field: keyof BindBankForm, value: string) {
  if (field === 'bankCardNo') {
    return normalizeBankCard(value);
  }
  if (field === 'phone') {
    return normalizePhone(value);
  }
  if (field === 'verificationCode') {
    return String(value || '').replace(/\D/g, '').slice(0, 6);
  }
  return sanitizeName(value);
}

watch(
  () => props.mode,
  (newMode, oldMode) => {
    updateContentTransition(newMode, oldMode);
    syncShellHeight();
  },
);

watch(
  () => props.visible,
  (nextVisible) => {
    if (nextVisible) {
      syncShellHeight();
    } else {
      shellHeight.value = null;
    }
  },
);

watch(
  () => [props.accounts.length, props.selectedId, props.realName, props.identityNo, props.form],
  () => {
    syncShellHeight();
  },
  { deep: true },
);

onMounted(() => {
  if (typeof ResizeObserver !== 'undefined') {
    resizeObserver = new ResizeObserver(() => {
      syncShellHeight();
    });
    refreshResizeObserverTargets();
  }
});

onBeforeUnmount(() => {
  resizeObserver?.disconnect();
  resizeObserver = null;
});
</script>


<style scoped>
.drawer-layer {
  position: fixed;
  inset: 0;
  z-index: 34;
}

.drawer-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  transition: opacity 0.26s ease;
}

.drawer-layer-enter-active,
.drawer-layer-leave-active {
  transition: opacity 0.26s ease;
}

.drawer-layer-enter-from,
.drawer-layer-leave-to {
  opacity: 0;
}

.drawer-shell {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  background: #fff;
  border-radius: 4px 4px 0 0;
  box-sizing: border-box;
  transition: height 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.drawer-shell-enter-active,
.drawer-shell-leave-active {
  transition: transform 0.32s ease, opacity 0.32s ease;
}

.drawer-shell-enter-from {
  opacity: 0;
  transform: translateY(100%) scaleY(0.92);
  transform-origin: bottom center;
}

.drawer-shell-enter-to {
  opacity: 1;
  transform: translateY(0) scaleY(1);
  transform-origin: bottom center;
}

.drawer-shell-leave-from {
  opacity: 1;
  transform: translateY(0) scaleY(1);
  transform-origin: top center;
}

.drawer-shell-leave-to {
  opacity: 0.9;
  transform: translateY(100%) scaleY(0);
  transform-origin: top center;
}

.drawer-inner {
  width: 100%;
}

.drawer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 14px;
  border-bottom: 1px solid #f0f1f4;
}

.header-btn {
  width: 36px;
  height: 36px;
  border: 0;
  border-radius: 18px;
  color: #5a6474;
  background: #f2f4f8;
  font-size: 24px;
  line-height: 36px;
  text-align: center;
}

.back-btn.hidden {
  visibility: hidden;
}

.drawer-content {
  overflow: hidden;
}

.drawer-pane {
  width: 100%;
}

.content-slide-left-enter-active,
.content-slide-left-leave-active,
.content-slide-right-enter-active,
.content-slide-right-leave-active {
  transition: transform 0.28s ease, opacity 0.28s ease;
}

.content-slide-left-enter-from {
  opacity: 0.01;
  transform: translateX(40px);
}

.content-slide-left-leave-to {
  opacity: 0.01;
  transform: translateX(-40px);
}

.content-slide-right-enter-from {
  opacity: 0.01;
  transform: translateX(-40px);
}

.content-slide-right-leave-to {
  opacity: 0.01;
  transform: translateX(40px);
}

.shell-account {
  max-height: 82vh;
}

.shell-verify {
  max-height: 82vh;
}

.shell-bind {
  max-height: 88vh;
}

.drawer-body {
  box-sizing: border-box;
}

.drawer-body-account {
  max-height: calc(82vh - 56px);
  padding: 20px 16px 96px;
  overflow-y: auto;
  background: #f6f6f6;
}

.drawer-body-verify {
  padding: 24px 28px 32px;
}

.drawer-body-bind {
  padding: 24px 38px 32px;
}

.account-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.drawer-footer {
  position: sticky;
  left: 0;
  right: 0;
  bottom: -96px;
  padding: 24px 0 18px;
  background: #fff;
  box-shadow: 0 -8px 18px rgba(255, 255, 255, 0.9);
}

.identity-row,
.form-field {
  display: flex;
  align-items: center;
  height: 64px;
  border-radius: 6px;
  background: #f5f6f8;
}

.identity-row + .identity-row {
  margin-top: 16px;
}

.form-field {
  margin-bottom: 12px;
  border-radius: 5px;
}

.identity-row input,
.form-field input {
  flex: 1;
  min-width: 0;
  height: 100%;
  padding: 0 20px;
  border: 0;
  outline: none;
  color: #141922;
  background: transparent;
  font-size: 20px;
  box-sizing: border-box;
}

.identity-row input::placeholder,
.form-field input::placeholder {
  color: #b7bdc9;
}

.readonly-field input {
  color: #81889a;
}

.primary-btn {
  display: block;
  width: 100%;
  height: 64px;
  border: 0;
  border-radius: 6px;
  color: #fff;
  background: #2f55f5;
  font-size: 23px;
  font-weight: 800;
}

.verify-btn {
  margin-top: 28px;
}

.bind-btn {
  margin-top: 16px;
}

.shell-account .primary-btn {
  width: 340px;
  height: 58px;
  margin: 0 auto;
  border-radius: 4px;
  font-weight: 600;
}

.scan-btn,
.send-code-btn {
  position: relative;
  flex: 0 0 auto;
  margin-right: 10px;
  border: 0;
  background: transparent;
}

.scan-btn {
  position: relative;
  width: 54px;
  height: 54px;
  background: transparent;
}

/* 四角 L 形标记：微圆角处理，线条宽度2px */
.scan-btn::before {
  content: '';
  position: absolute;
  inset: 5px;
  background:
    /* 左上角 L 形 */
    linear-gradient(#5a6a7e, #5a6a7e) left top / 7px 2px no-repeat,
    linear-gradient(#5a6a7e, #5a6a7e) left top / 2px 7px no-repeat,
    /* 右上角 L 形 */
    linear-gradient(#5a6a7e, #5a6a7e) right top / 7px 2px no-repeat,
    linear-gradient(#5a6a7e, #5a6a7e) right top / 2px 7px no-repeat,
    /* 左下角 L 形 */
    linear-gradient(#5a6a7e, #5a6a7e) left bottom / 7px 2px no-repeat,
    linear-gradient(#5a6a7e, #5a6a7e) left bottom / 2px 7px no-repeat,
    /* 右下角 L 形 */
    linear-gradient(#5a6a7e, #5a6a7e) right bottom / 7px 2px no-repeat,
    linear-gradient(#5a6a7e, #5a6a7e) right bottom / 2px 7px no-repeat;
  border-radius: 1.5px;
  pointer-events: none;
}

/* 中间扫描线：圆角矩形，略微超出四角内框 */
.scan-btn::after {
  content: '';
  position: absolute;
  left: 3px;
  right: 3px;
  top: 50%;
  transform: translateY(-50%);
  height: 2px;
  background: #5a6a7e;
  border-radius: 4px;
  pointer-events: none;
}

.send-code-btn {
  width: 126px;
  height: 44px;
  border-radius: 4px;
  color: #2f55f5;
  background: #fff;
  font-size: 18px;
  font-weight: 700;
}

@media (max-width: 430px) {
  .drawer-body-bind {
    padding-left: 22px;
    padding-right: 22px;
  }
}
</style>
