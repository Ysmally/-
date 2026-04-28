<template>
  <div class="account-card" :class="{ selected }" @click="$emit('select', account.id)">
    <div class="account-main">
      <!-- 银行/渠道 logo，通过 logoClass 匹配不同银行颜色 -->
      <div class="bank-logo" :class="account.logoClass">{{ account.logoText }}</div>
      <div class="account-content">
        <div class="bank-title">
          {{ account.bankName }} <span v-if="tailText">（ {{ tailText }} ）</span>
        </div>
        <div class="account-number">{{ account.accountName }}　{{ account.maskedAccount }}</div>
        <!-- 提现额度提示，金额部分高亮 -->
        <div class="limit-text">
          {{ limitPrefix }}<span>{{ limitAmount }}</span>
        </div>
      </div>
    </div>
    <span v-if="showCheck" class="check-mark">✓</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { WithdrawAccount } from '../types';

const props = defineProps<{
  //账户数据对象
  account: WithdrawAccount;
  // 是否选中态（P2 抽屉中多选场景）
  selected?: boolean;
  // 是否上一选中态，用于选中边框迁移动画
  wasSelected?: boolean;
  // 是否显示右侧选中标记，仅银行卡渠道显示
  showCheck?: boolean;
}>();

defineEmits<{
  // 点击卡片时触发，传递账户 ID
  (event: 'select', id: string): void;
}>();

// 从脱敏账号中提取末尾4位作为尾号展示
const tailText = computed(() => {
  const match = props.account.maskedAccount.match(/(\d{4})$/);
  return match ? match[1] : '';
});

// 额度提示文案前缀
const limitPrefix = computed(() => props.account.limitText.replace('5,000,000元', ''));

// 额度提示文案中的金额部分蓝色高亮展示
const limitAmount = computed(() => (props.account.limitText.includes('5,000,000元') ? '5,000,000元' : ''));
</script>

<style scoped>
.account-card {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 126px;
  padding: 20px 24px;
  background: #fff;
  border-radius: 8px;
  box-sizing: border-box;
  transition: transform 0.26s ease;
}

.account-card::after {
  content: '';
  position: absolute;
  inset: 0;
  border: 2px solid #19b84a;
  border-radius: 8px;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.28s ease;
}

.account-card.selected {
}

.account-card.selected::after {
  opacity: 1;
}

.account-card.was-selected:not(.selected) {
}

.account-card.was-selected:not(.selected)::after {
  opacity: 1;
}

.account-main {
  display: flex;
  align-items: flex-start;
  min-width: 0;
}

.bank-logo {
  flex: 0 0 auto;
  width: 78px;
  height: 22px;
  margin-top: 9px;
  margin-right: 18px;
  border-radius: 3px;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  line-height: 22px;
  text-align: center;
}

.logo-wechat {
  width: 34px;
  height: 26px;
  color: #12b312;
  background: transparent;
  font-size: 24px;
  line-height: 26px;
}

.logo-icbc { background: #d71920; }
.logo-cib { background: #514da1; }
.logo-abc { background: #129a7b; }
.logo-cmb { background: #d91e18; }
.logo-pab { background: #f58220; }
.logo-custom { background: #35a7e8; }

.account-content {
  min-width: 0;
}

.bank-title {
  color: #050505;
  font-size: 23px;
  font-weight: 800;
  line-height: 32px;
}

.account-number {
  margin-top: 14px;
  color: #1d2635;
  font-size: 20px;
  line-height: 28px;
}

.limit-text {
  margin-top: 14px;
  color: #1d2635;
  font-size: 18px;
  line-height: 26px;
}

.limit-text span {
  color: #2f55f5;
  font-weight: 800;
}

.check-mark {
  position: absolute;
  right: 18px;
  top: 50%;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  color: #fff;
  background: #2f55f5;
  font-size: 18px;
  font-weight: 700;
  line-height: 22px;
  text-align: center;
  transform: translateY(-50%);
  transition: opacity 0.24s ease, transform 0.24s ease;
}

.account-card:not(.selected) .check-mark {
  opacity: 0;
  transform: translateY(-50%) scale(0.8);
}

@media (max-width: 430px) {
  .account-card {
    padding: 18px 18px;
  }

  .bank-logo {
    width: 66px;
    margin-right: 12px;
  }

  .bank-title {
    font-size: 20px;
  }

  .account-number,
  .limit-text {
    font-size: 17px;
  }
}
</style>
