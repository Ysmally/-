<!--
  - 老用户/新用户判断必须以后端返回为准
  - 绑定银行卡后必须重新查询账户列表
  - 提现按钮防重复提交，使用submitting锁
-->
<template>
  <div class="withdraw-page">
    <!-- 金额输入区 -->
    <section class="amount-section">
      <h2><span></span>提现金额</h2>
      <div class="amount-input">
        <span>¥</span>
        <input
          :value="amount"
          type="number"
          inputmode="decimal"
          placeholder="单笔最大限额20000，最小5元"
          @input="handleAmountInput"
        />
      </div>
      <div class="balance-line">
        当前可提现金额{{ walletDetail.withdrawable_balance }}元        <button @click="runConfiguredAction(entryConfig.guideEntry)">缴税问题指引 ></button>
      </div>
    </section>

    <!-- 提现方式区：微信 / 银行卡 -->
    <section class="method-section">
      <h2><span></span>提现方式</h2>
      <button
        v-for="method in withdrawMethods"
        :key="method.type"
        class="method-item"
        :class="{ active: selectedChannel === method.type }"
        @click="runConfiguredAction({ type: 'selectMethod', channel: method.type })"
      >
        <span class="method-icon" :class="method.iconClass">
          <img
            v-if="iconAssetMap[method.iconAssetKey]"
            :src="iconAssetMap[method.iconAssetKey]!"
            :alt="method.label"
          />
          <span v-else>{{ method.iconText }}</span>
        </span>
        <span class="method-name">{{ method.label }}</span>
        <span v-if="method.recommended" class="recommend-tag">推荐</span>
        <span class="method-radio"></span>
      </button>
    </section>

    <!-- 老用户(hasBoundAccounts=true)：展示"收款账号"+账号卡片+"切换账号" -->
    <!-- 新用户(hasBoundAccounts=false)：展示"绑定账号"按钮 -->
    <section v-if="showAccountSection" class="account-section">
      <div class="account-title">
        <h3>{{ hasBoundAccounts ? '收款账号' : '绑定账号' }}</h3>
        <button v-if="hasBoundAccounts" @click="runConfiguredAction(entryConfig.accountSwitchEntry)">切换账号</button>
      </div>
      <WithdrawAccountCard
        v-if="hasBoundAccounts && selectedAccount"
        :account="selectedAccount"
        :selected="true"
        :show-check="selectedChannel === 'bank'"
      />
      <button
        v-else
        class="empty-account"
        @click="runConfiguredAction(entryConfig.noAccountBindEntry)"
      >
        绑定账号
      </button>
    </section>

    <footer class="withdraw-footer">
      <button class="submit-btn" :disabled="!canSubmit || submitting" @click="runConfiguredAction(entryConfig.confirmSubmitEntry)">
        {{ submitting ? '提交中...' : '确认提现' }}
      </button>
      <!-- 提现协议勾选 -->
      <label class="withdraw-agreement">
        <span
          class="agreement-radio"
          :class="{ checked: agreementChecked }"
          @click="agreementChecked = !agreementChecked"
        ></span>
        <span>我已阅读并同意</span>
        <a href="javascript:void(0)" @click.stop="runConfiguredAction(entryConfig.agreementEntry)">《蓝鲸纪提现服务协议》</a>
      </label>
    </footer>

    <!-- 账号抽屉里的“绑定银行卡”始终进入绑卡流程，不复用提现提交逻辑 -->
    <WithdrawFlowDrawer
      :visible="Boolean(activeDrawer)"
      :mode="activeDrawer"
      :accounts="channelAccounts"
      :selected-id="selectedAccountId"
      :previous-selected-id="previousSelectedAccountId"
      :real-name="identityVerifyForm.realName"
      :identity-no="identityVerifyForm.identityNo"
      :form="bindBankForm"
      @close="runConfiguredAction({ type: 'closeDrawer' })"
      @back="goDrawerStepBack"
      @select-account="runConfiguredAction({ type: 'selectAccount' }, $event)"
      @bind-bank-card="openBindBankCardFlow"
      @update:real-name="identityVerifyForm.realName = $event"
      @update:identity-no="identityVerifyForm.identityNo = $event"
      @verify-identity="runConfiguredAction(entryConfig.identityVerifySubmitEntry)"
      @update:form="bindBankForm = $event"
      @submit-bind-bank-card="runConfiguredAction(entryConfig.bindBankCardSubmitEntry)"
      @scan-bank-card="runConfiguredAction(entryConfig.scanBankCardEntry)"
      @validate-bank-card="runConfiguredAction(entryConfig.validateBankCardEntry)"
      @validate-bank-name="runConfiguredAction(entryConfig.validateBankNameEntry)"
      @validate-phone="runConfiguredAction(entryConfig.validatePhoneEntry)"
      @send-code="runConfiguredAction(entryConfig.sendBindCodeEntry)"
    />
  </div>
</template>


<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import {
  applyWithdraw,
  buildApplyPayload,
  fetchBoundAccounts,
  fetchWalletDetail,
  sendBindVerificationCode,
  submitBindBankCardRequest,
  validateBankCardInput,
  validateBankNameInput,
  requestWechatWithdraw,
  verifyBindIdentity,
} from './api';
import { withdrawEntryConfig, withdrawMethods } from './config';
import { initialWithdrawAccounts } from './mock-data';
import {
  isValidBankCard,
  normalizeMoney,
  sanitizeBindBankForm,
  sanitizeIdentityVerifyForm,
  toMoney,
} from './security';
import type {
  BindBankForm,
  IdentityVerifyForm,
  WalletDetail,
  WithdrawActionConfig,
  WithdrawChannel,
  WithdrawDrawer,
} from './types';
import WithdrawFlowDrawer from './components/WithdrawFlowDrawer.vue';
import WithdrawAccountCard from './components/WithdrawAccountCard.vue';

const emptyWalletDetail: WalletDetail = {
  withdrawable_balance: '0.00',
  frozen_balance: '0.00',
  total_income: '0.00',
  pending_withdraw_amount: '0.00',
  today_income: '0.00',
  total_withdrawn: '0.00',
  next_release_at: null,
};

const router = useRouter();
const walletDetail = ref<WalletDetail>({ ...emptyWalletDetail });
const accounts = ref([...initialWithdrawAccounts]);
// 老用户/新用户判断：true=有已绑定账户，false=无账户需绑定

const hasBoundAccounts = ref(false);
const selectedChannel = ref<WithdrawChannel>('wechat');
const selectedAccountId = ref('');
const previousSelectedAccountId = ref('');

// 当前打开的抽屉模式，空字符串表示关闭
const activeDrawer = ref<WithdrawDrawer>('');
const amount = ref('');
const agreementChecked = ref(false);
const submitting = ref(false);
const identityVerifyForm = ref<IdentityVerifyForm>({
  realName: '',
  identityNo: '',
});


const entryConfig = withdrawEntryConfig;
//渠道图标资源映射，预留给后续替换为真实图片
const iconAssetMap = {
  wechat: '',
  bank: '',
};

const bindBankForm = ref<BindBankForm>({
  verifiedRealName: '',
  verifiedIdentityNo: '',
  bankCardNo: '',
  bankName: '',
  phone: '',
  verificationCode: '',
});
const bindCaptchaRef = ref('');

/* 计算属性 */
//当前渠道下的账户列表
const channelAccounts = computed(() => accounts.value.filter((item) => item.type === selectedChannel.value));
//当前选中的账户对象
const selectedAccount = computed(() => accounts.value.find((item) => item.id === selectedAccountId.value));
//可提现金额
const withdrawableAmount = computed(() => Number(walletDetail.value.withdrawable_balance || 0));
//输入金额
const inputAmount = computed(() => Number(amount.value || 0));
// 是否显示收款账号区，仅银行卡渠道显示
const showAccountSection = computed(() => selectedChannel.value === 'bank');
const canSubmit = computed(() => {
  // 微信渠道
  if (selectedChannel.value === 'wechat') {
    return Boolean(
      agreementChecked.value &&
        inputAmount.value > 0 &&
        inputAmount.value <= withdrawableAmount.value,
    );
  }

  return Boolean(
    hasBoundAccounts.value &&
      selectedAccount.value &&
      agreementChecked.value &&
      inputAmount.value > 0 &&
      inputAmount.value <= withdrawableAmount.value,
  );
});

/* 生命周期 */

onMounted(() => {
  runConfiguredAction(entryConfig.loadAccountsEntry);
  loadWalletDetail();
});

/* 数据加载 */

function loadWalletDetail() {
  fetchWalletDetail(
    (data) => {
      walletDetail.value = data || { ...emptyWalletDetail };
    },
    (message) => {
      ElMessage.error(message || '钱包详情加载失败');
    },
  );
}

//加载已绑定账户列表

function loadBoundAccounts() {
  fetchBoundAccounts(
    (data) => {
      const safeData = data || { hasBoundAccounts: false, accounts: [] };
      const safeAccounts = Array.isArray(safeData.accounts) ? safeData.accounts : [];

      hasBoundAccounts.value = Boolean(safeData.hasBoundAccounts);
      accounts.value = safeAccounts.length ? safeAccounts : initialWithdrawAccounts;

      // 新用户无账户时，清空列表
      if (!safeData.hasBoundAccounts && !safeAccounts.length) {
        accounts.value = [];
      }

      // 自动选中当前渠道下的第一个账户
      const firstAccount = accounts.value.find((item) => item.type === selectedChannel.value) || accounts.value[0];
      selectedAccountId.value = firstAccount?.id || '';
    },
    (message) => {
      ElMessage.error(message || '收款账号加载失败');
    },
  );
}

/* 交互处理 */
//切换提现渠道
function selectChannel(channel: WithdrawChannel) {
  selectedChannel.value = channel;
  const firstAccount = accounts.value.find((item) => item.type === channel);
  selectedAccountId.value = firstAccount?.id || '';
}

function closeDrawer() {
  activeDrawer.value = '';
}

// 选择账户并关闭抽屉
function chooseAccount(id: string) {
  if (selectedAccountId.value === id) {
    closeDrawer();
    return;
  }

  previousSelectedAccountId.value = selectedAccountId.value;
  selectedAccountId.value = id;
  ElMessage.success('已切换收款账号');
  window.setTimeout(() => {
    closeDrawer();
    previousSelectedAccountId.value = '';
  }, 420);
}

// 提交提现申请
function submitWithdraw() {
  if (!agreementChecked.value) {
    ElMessage.error('请先阅读并同意提现服务协议');
    return;
  }

  if (inputAmount.value <= 0) {
    ElMessage.error('请输入提现金额');
    return;
  }

  if (inputAmount.value > withdrawableAmount.value) {
    ElMessage.error('提现金额不能超过当前可提现金额');
    return;
  }

  if (selectedChannel.value === 'wechat') {
    submitting.value = true;
    requestWechatWithdraw(
      { amount: toMoney(amount.value) },
      () => {
        ElMessage.success('微信提现接口已预留');
        submitting.value = false;
      },
      (message) => {
        ElMessage.error(message || '微信提现接口调用失败');
        submitting.value = false;
      },
    );
    return;
  }

  // 银行卡渠道需选中账户
  if (!selectedAccount.value) {
    ElMessage.error('请选择收款账号');
    return;
  }

  submitting.value = true;
  applyWithdraw(
    buildApplyPayload(toMoney(amount.value), selectedAccount.value, bindBankForm.value.verifiedIdentityNo),
    () => {
      ElMessage.success('提现申请已提交');
      amount.value = '';
      loadWalletDetail();
      closeDrawer();
      submitting.value = false;
    },
    (message) => {
      ElMessage.error(message || '提现申请提交失败');
      submitting.value = false;
    },
  );
}

// 账户抽屉内绑定银行卡入口：
// 新老用户统一先走身份验证再绑卡
function openBindBankCardFlow() {
  activeDrawer.value = 'identityVerify';
}

function goDrawerStepBack() {
  if (activeDrawer.value === 'bindBankCard') {
    activeDrawer.value = 'identityVerify';
    return;
  }
  if (activeDrawer.value === 'identityVerify') {
    activeDrawer.value = 'accountSelect';
    return;
  }
  closeDrawer();
}

// 当前确认提现按钮按后端预留
function handleConfirmSubmit() {
  submitWithdraw();
}


function submitBindBankCard() {
  const safeForm = sanitizeBindBankForm(bindBankForm.value);

  // 表单校验
  if (!safeForm.verifiedRealName || !safeForm.verifiedIdentityNo) {
    ElMessage.error('请先完成身份验证');
    return;
  }
  if (!isValidBankCard(safeForm.bankCardNo)) {
    ElMessage.error('请输入正确的银行卡账号');
    return;
  }
  if (!safeForm.bankName) {
    ElMessage.error('请输入所属银行');
    return;
  }
  if (!safeForm.phone) {
    ElMessage.error('请输入绑定手机号');
    return;
  }
  if (!safeForm.verificationCode) {
    ElMessage.error('请输入手机验证码');
    return;
  }
  if (!bindCaptchaRef.value) {
    ElMessage.error('请先获取验证码');
    return;
  }

  submitBindBankCardRequest(
    {
      realName: safeForm.verifiedRealName,
      idCard: safeForm.verifiedIdentityNo,
      bankCardNo: safeForm.bankCardNo,
      bankName: safeForm.bankName,
      bankPhone: safeForm.phone,
      captcha: safeForm.verificationCode,
      ref: bindCaptchaRef.value,
    },
    (data) => {
      if (Number(data?.isVerified) !== 1) {
        ElMessage.error('银行卡绑定未通过，请核对信息后重试');
        return;
      }

      ElMessage.success('银行卡绑定成功');
      bindCaptchaRef.value = '';
      loadBoundAccounts();
      selectedChannel.value = 'bank';
      closeDrawer();
    },
    (message) => {
      ElMessage.error(message || '绑定银行卡失败');
    },
  );
}

function handleScanBankCard() {
  ElMessage.success('银行卡识别功能已预留');
}

function validateBankCard() {
  validateBankCardInput(
    { bankCardNo: bindBankForm.value.bankCardNo },
    () => {},
    (message) => {
      ElMessage.error(message || '银行卡校验失败');
    },
  );
}

function validateBankName() {
  validateBankNameInput(
    { bankName: bindBankForm.value.bankName, phone: bindBankForm.value.phone },
    () => {},
    (message) => {
      ElMessage.error(message || '所属银行校验失败');
    },
  );
}

function validatePhone() {
  if (!bindBankForm.value.phone) {
    ElMessage.error('请输入绑定手机号');
    return;
  }
}

/** 发送短信验证码 */
function sendBindCode() {
  const safeForm = sanitizeBindBankForm(bindBankForm.value);
  if (!safeForm.verifiedRealName || !safeForm.verifiedIdentityNo) {
    ElMessage.error('请先完成身份验证');
    return;
  }
  if (!isValidBankCard(safeForm.bankCardNo)) {
    ElMessage.error('请输入正确的银行卡账号');
    return;
  }
  if (!safeForm.bankName) {
    ElMessage.error('请输入所属银行');
    return;
  }
  if (!safeForm.phone) {
    ElMessage.error('请输入绑定手机号');
    return;
  }

  sendBindVerificationCode(
    {
      realName: safeForm.verifiedRealName,
      idCard: safeForm.verifiedIdentityNo,
      bankCardNo: safeForm.bankCardNo,
      bankName: safeForm.bankName,
      bankPhone: safeForm.phone,
    },
    (data) => {
      if (Number(data?.is_signed) === 0 && data?.url) {
        window.location.href = data.url;
        return;
      }
      if (data?.step === 'VERIFY_CODE_SENT' && data?.ref) {
        bindCaptchaRef.value = data.ref;
        ElMessage.success('验证码已发送，请输入验证码继续绑卡');
        return;
      }
      ElMessage.error('验证码发送结果异常，请稍后再试');
    },
    (message) => {
      ElMessage.error(message || '验证码发送失败');
    },
  );
}

function verifyIdentity() {
  const safeForm = sanitizeIdentityVerifyForm(identityVerifyForm.value);
  if (!safeForm.realName) {
    ElMessage.error('请输入姓名');
    return;
  }
  if (!safeForm.identityNo) {
    ElMessage.error('请输入身份证');
    return;
  }

  verifyBindIdentity(
    safeForm,
    (data) => {
      if (Number(data?.is_signed) === 0) {
        if (data?.url) {
          window.location.href = data.url;
          return;
        }
        ElMessage.error('未签约，且未返回签约地址');
        return;
      }

      // 将验证通过的身份信息回填到绑卡表单
      bindBankForm.value.verifiedRealName = safeForm.realName;
      bindBankForm.value.verifiedIdentityNo = safeForm.identityNo;
      bindCaptchaRef.value = '';
      // 切换到绑定银行卡抽屉
      activeDrawer.value = 'bindBankCard';
    },
    (message) => {
      ElMessage.error(message || '身份验证失败');
    },
  );
}

// 金额输入处理，实时标准化格式
function handleAmountInput(event: Event) {
  amount.value = normalizeMoney((event.target as HTMLInputElement).value);
}


function runConfiguredAction(config: WithdrawActionConfig, payload?: string) {
  if (config.type === 'loadAccounts') {
    loadBoundAccounts();
    return;
  }

  if (config.type === 'openDrawer') {
    activeDrawer.value = config.drawer || '';
    return;
  }

  if (config.type === 'closeDrawer') {
    closeDrawer();
    return;
  }

  if (config.type === 'navigate' && config.route) {
    router.push(config.route);
    return;
  }

  if (config.type === 'submitWithdraw') {
    handleConfirmSubmit();
    return;
  }

  if (config.type === 'verifyBindIdentity') {
    verifyIdentity();
    return;
  }

  if (config.type === 'submitBindBankCard') {
    submitBindBankCard();
    return;
  }

  if (config.type === 'selectMethod' && config.channel) {
    selectChannel(config.channel);
    return;
  }

  if (config.type === 'selectAccount' && payload) {
    chooseAccount(payload);
    return;
  }

  if (config.type === 'scanBankCard') {
    handleScanBankCard();
    return;
  }

  if (config.type === 'validateBankCard') {
    validateBankCard();
    return;
  }

  if (config.type === 'validateBankName') {
    validateBankName();
    return;
  }

  if (config.type === 'validatePhone') {
    validatePhone();
    return;
  }

  if (config.type === 'sendBindCode') {
    sendBindCode();
  }
}
</script>

<style scoped>
.withdraw-page {
  --withdraw-blue: #2f55f5;
  --withdraw-red: #ff3d3d;
  --withdraw-bg: #f6f6f6;
  min-height: 100vh;
  padding-bottom: 142px;
  background: #fff;
  color: #050505;
  font-family: "Microsoft YaHei", sans-serif;
  box-sizing: border-box;
}

button {
  cursor: pointer;
}

.amount-section,
.method-section {
  padding: 30px 26px 0;
  box-sizing: border-box;
}

h2 {
  display: flex;
  align-items: center;
  margin: 0 0 24px;
  color: #050505;
  font-size: 23px;
  font-weight: 800;
  line-height: 32px;
}

h2 span {
  width: 4px;
  height: 18px;
  margin-right: 8px;
  border-radius: 2px;
  background: var(--withdraw-blue);
}

.amount-input {
  display: flex;
  align-items: center;
  width: 100%;
  height: 66px;
  padding: 0 16px;
  border-radius: 5px;
  background: #f4f6fa;
  box-sizing: border-box;
}

.amount-input span {
  margin-right: 13px;
  color: #050505;
  font-size: 26px;
  font-weight: 900;
}

.amount-input input {
  flex: 1;
  min-width: 0;
  border: 0;
  outline: none;
  color: #050505;
  background: transparent;
  font-size: 20px;
  font-weight: 800;
}

.amount-input input::placeholder {
  color: #bdc2cc;
}

.balance-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 14px;
  color: #050505;
  font-size: 18px;
  line-height: 28px;
}

.balance-line button {
  border: 0;
  color: var(--withdraw-red);
  background: transparent;
  font-size: 17px;
  font-weight: 800;
}

.method-section {
  margin-top: 22px;
  padding-bottom: 0;
  background: var(--withdraw-bg);
}

.method-section h2 {
  padding-top: 38px;
}

.method-item {
  display: flex;
  align-items: center;
  width: 100%;
  height: 104px;
  padding: 0 4px 0 14px;
  border: 0;
  border-bottom: 1px solid #e8e8e8;
  background: #fff;
  text-align: left;
  box-sizing: border-box;
}

.method-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  margin-right: 16px;
  font-size: 25px;
  font-weight: 800;
  text-align: center;
  border-radius: 10px;
}

.method-icon img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.wechat-icon {
  color: #12b312;
}

.bank-icon {
  color: #28a9e8;
}

.method-name {
  color: #050505;
  font-size: 22px;
  line-height: 32px;
}

.recommend-tag {
  margin-left: 10px;
  padding: 3px 8px;
  border: 1px solid #19b84a;
  border-radius: 4px;
  color: #19b84a;
  font-size: 15px;
  line-height: 20px;
}

.method-radio {
  position: relative;
  width: 22px;
  height: 22px;
  margin-left: auto;
  border: 2px solid #cad0dd;
  border-radius: 50%;
  box-sizing: border-box;
  transition: all 0.24s ease;
}

/* 选中态：蓝色实心圆底 */
.method-item.active .method-radio {
  border-color: var(--withdraw-blue);
  background: var(--withdraw-blue);
}

/* 用 CSS 边框绘制白色对勾 √ */
.method-item.active .method-radio::after {
  content: '';
  position: absolute;
  left: 6px;
  top: 2px;
  width: 6px;
  height: 11px;
  border: solid #fff;
  border-width: 0 2.5px 2.5px 0;
  transform: rotate(45deg);
}

.account-section {
  padding: 0 16px 104px;
  background: var(--withdraw-bg);
}

.account-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 26px;
}

.account-title h3 {
  margin: 0;
  color: #050505;
  font-size: 20px;
  font-weight: 800;
}

.account-title button {
  border: 0;
  color: var(--withdraw-blue);
  background: transparent;
  font-size: 19px;
  font-weight: 800;
}

.empty-account {
  width: 100%;
  height: 126px;
  border: 0;
  border-radius: 8px;
  color: var(--withdraw-blue);
  background: #fff;
  font-size: 20px;
  font-weight: 800;
}


.withdraw-footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 10;
  padding: 24px 22px 14px;
  background: #fff;
  box-sizing: border-box;
}

.submit-btn {
  display: block;
  width: calc(100% - 32px);
  height: 63px;
  margin: 0 auto 20px;
  border: 0;
  border-radius: 5px;
  color: #fff;
  background: var(--withdraw-blue);
  font-size: 23px;
  font-weight: 800;
}

.submit-btn:disabled {
  background: #c5cbd4;
}

/* 提现协议勾选样式 */
.withdraw-agreement {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  color: #7b8190;
  font-size: 15px;
  line-height: 20px;
}

/* 未勾选态：灰色圆环 */
.agreement-radio {
  width: 18px;
  height: 18px;
  border: 1px solid #777;
  border-radius: 50%;
  background: #fff;
  box-sizing: border-box;
}

/* 勾选态：蓝色实心圆（粗边框模拟） */
.agreement-radio.checked {
  border: 6px solid #2f55f5;
}

.withdraw-agreement a {
  color: #2f55f5;
  text-decoration: none;
}

@media (max-width: 430px) {
  .amount-section,
  .method-section {
    padding-left: 22px;
    padding-right: 22px;
  }

  .balance-line {
    font-size: 16px;
  }

  .balance-line button {
    font-size: 16px;
  }
}
</style>
