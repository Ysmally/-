// 提现模块配置化入口
import type { WithdrawActionConfig, WithdrawMethodOption } from './types';

/** 提现方式选项列表，用于 P1 提现方式区域的按钮渲染 */
export const withdrawMethods: WithdrawMethodOption[] = [
  {
    type: 'wechat',
    label: '提现到 微信',
    iconText: '微',
    iconClass: 'wechat-icon',
    iconAssetKey: 'wechat',
    recommended: true,
  },
  {
    type: 'bank',
    label: '提现到 银行卡',
    iconText: '卡',
    iconClass: 'bank-icon',
    iconAssetKey: 'bank',
    recommended: false,
  },
];

/*
提现模块全量入口配置
每个入口对应页面上的一个交互点，type 决定 runConfiguredAction 分发到哪个处理函数。
openDrawer: 打开指定抽屉（accountSelect / identityVerify / bindBankCard）
closeDrawer: 关闭当前抽屉
navigate: 路由跳转（如跳转到协议页、指引页）
submitWithdraw: 提交提现申请
verifyBindIdentity: 提交身份验证
submitBindBankCard: 提交绑定银行卡
loadAccounts: 加载已绑定账户列表
scanBankCard: 扫描银行卡（占位）
validateBankCard / validateBankName / validatePhone: 表单字段校验
sendBindCode: 发送短信验证码
selectMethod: 切换提现渠道
 */
export const withdrawEntryConfig = {
  loadAccountsEntry: {
    type: 'loadAccounts',
  } satisfies WithdrawActionConfig,

  chooseAmountEntry: {
    type: 'fillAllAmount',
  } satisfies WithdrawActionConfig,

  guideEntry: {
    type: 'navigate',
    route: '/myself/withdraw/guide',
  } satisfies WithdrawActionConfig,

  confirmSubmitEntry: {
    type: 'submitWithdraw',
  } satisfies WithdrawActionConfig,

  agreementEntry: {
    type: 'navigate',
    route: '/myself/withdraw/agreement',
  } satisfies WithdrawActionConfig,

  accountSwitchEntry: {
    type: 'openDrawer',
    drawer: 'accountSelect',
  } satisfies WithdrawActionConfig,

  noAccountBindEntry: {
    type: 'openDrawer',
    drawer: 'identityVerify',
  } satisfies WithdrawActionConfig,

  bindBankCardEntry: {
    type: 'openDrawer',
    drawer: 'identityVerify',
  } satisfies WithdrawActionConfig,

  identityVerifySubmitEntry: {
    type: 'verifyBindIdentity',
  } satisfies WithdrawActionConfig,

  identityVerifyCloseEntry: {
    type: 'closeDrawer',
  } satisfies WithdrawActionConfig,

  bindBankCardSubmitEntry: {
    type: 'submitBindBankCard',
  } satisfies WithdrawActionConfig,

  bindBankCardCloseEntry: {
    type: 'closeDrawer',
  } satisfies WithdrawActionConfig,
  scanBankCardEntry: {
    type: 'scanBankCard',
  } satisfies WithdrawActionConfig,
  validateBankCardEntry: {
    type: 'validateBankCard',
  } satisfies WithdrawActionConfig,
  validateBankNameEntry: {
    type: 'validateBankName',
  } satisfies WithdrawActionConfig,
  validatePhoneEntry: {
    type: 'validatePhone',
  } satisfies WithdrawActionConfig,
  sendBindCodeEntry: {
    type: 'sendBindCode',
  } satisfies WithdrawActionConfig,
};
