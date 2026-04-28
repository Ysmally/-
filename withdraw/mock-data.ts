//哈哈哈这个是临时数据，只作为测试用，后续替换为接口调用，可删除
import type { WithdrawAccount } from './types';
export const initialWithdrawAccounts: WithdrawAccount[] = [];
export const placeholderBoundAccounts: WithdrawAccount[] = [
  {
    id: 'wechat-default',
    type: 'wechat',
    bankName: '微信',
    accountName: 'test',
    maskedAccount: '123****4567',
    wxAccount: '123****4567',
    limitText: '该身份证号2026年度剩余的提现额度为 5,000,000元',
    logoText: '微',
    logoClass: 'logo-wechat',
    recommended: true,
  },
  {
    id: 'bank-icbc',
    type: 'bank',
    bankName: '中国工商银行',
    accountName: 'test',
    maskedAccount: '1101 **** **** 1101',
    bankCardNo: '1101000000001101',
    limitText: '该身份证号2026年度剩余的提现额度为 5,000,000元',
    logoText: '工',
    logoClass: 'logo-icbc',
  },
  {
    id: 'bank-boc',
    type: 'bank',
    bankName: '中国银行',
    accountName: 'test',
    maskedAccount: '1102 **** **** 1102',
    bankCardNo: '1102000000001102',
    limitText: '该身份证号2026年度剩余的提现额度为 5,000,000元',
    logoText: '中',
    logoClass: 'logo-boc',
  },
];
