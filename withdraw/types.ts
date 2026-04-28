export type WithdrawChannel = 'wechat' | 'bank';

export type WithdrawDrawer = '' | 'accountSelect' | 'identityVerify' | 'bindBankCard';

/**
 * 收款账户模型
 * - 老用户：后端返回已绑定账户列表，前端展示"收款账号"区域
 * - 新用户：hasBoundAccounts=false，前端展示"绑定账号"入口
 */
export interface WithdrawAccount {
  id: string;
  type: WithdrawChannel;
  bankName: string;
  accountName: string;
  //脱敏后的账号展示文本
  maskedAccount: string;
  //银行卡完整卡号
  bankCardNo?: string;
  //微信账号标识，微信提现时使用
  wxAccount?: string;
  // 提现额度提示文案
  limitText: string;
  // 银行渠道 logo
  logoText: string;
  // 银行渠道 logo 样式类名
  logoClass: string;
  //是否推荐该渠道，"推荐"标签
  recommended?: boolean;
}

export interface WalletDetail {
  //可提现余额
  withdrawable_balance: string;
  // 冻结金额
  frozen_balance: string;
  //累计总收入
  total_income: string;
  // 提现处理中金额
  pending_withdraw_amount: string;
  //今日收入
  today_income: string;
  // 累计已提现金额（元）
  total_withdrawn: string;
  // 下次冻结释放时间戳，null 表示无冻结释放计划
  next_release_at: number | null;
}

export interface BindBankForm {
  // 身份验证通过的姓名
  verifiedRealName: string;
  // 身份验证通过的身份证号
  verifiedIdentityNo: string;
  //银行卡号
  bankCardNo: string;
  // 所属银行名称
  bankName: string;
  // 绑定手机号
  phone: string;
  // 短信验证码，6位数字
  verificationCode: string;
}


export interface IdentityVerifyForm {
  realName: string;
  identityNo: string;
}


export type WithdrawActionType =
  | 'openDrawer'
  | 'closeDrawer'
  | 'navigate'
  | 'submitWithdraw'
  | 'submitBindBankCard'
  | 'toggleAgreement'
  | 'sendVerifyCode'
  | 'verifyIdentity'
  | 'selectMethod'
  | 'fillAllAmount'
  | 'selectAccount'
  | 'scanBankCard'
  | 'loadAccounts'
  | 'verifyBindIdentity'
  | 'validateBankCard'
  | 'validateBankName'
  | 'validatePhone'
  | 'sendBindCode';


export interface WithdrawActionConfig {
  type: WithdrawActionType;
  drawer?: WithdrawDrawer;
  route?: string;
  next?: WithdrawActionConfig | null;
  channel?: WithdrawChannel;
}

export interface WithdrawMethodOption {
  type: WithdrawChannel;
  label: string;
  iconText: string;
  iconClass: string;
  iconAssetKey: 'wechat' | 'bank';
  recommended: boolean;
}
