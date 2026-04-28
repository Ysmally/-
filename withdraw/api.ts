import { ApiRequest } from '@/util/request';
import type {
  IdentityVerifyForm,
  WalletDetail,
  WithdrawAccount,
} from './types';
import { toMoney } from './security';

type ApiSuccess<T> = (data: T) => void;
type ApiFailure = (message: string) => void;

export interface WithdrawApplyPayload {
  amount: string;
  channel: 'wechat' | 'bank';
  bankCardNo?: string;
  bankName?: string;
  wxAccount?: string;
  idCard?: string;
  remark?: string;
}

export interface WithdrawApplyResult {
  withdrawRecordId?: string;
  orderSn?: string;
  ref?: string;
  amount?: string;
  bankCardNo?: string;
}

export interface WechatWithdrawPayload {
  amount: string;
}

export interface AccountListResult {
  hasBoundAccounts: boolean;
  accounts: WithdrawAccount[];
}

export interface IdentityVerifyResult {
  is_signed: number;
  url?: string;
}

export interface BankValidationPayload {
  bankCardNo?: string;
  bankName?: string;
  phone?: string;
  verificationCode?: string;
}

export interface SendBindCodePayload {
  realName: string;
  idCard: string;
  bankCardNo: string;
  bankName: string;
  bankPhone: string;
}

export interface SendBindCodeResult {
  step?: string;
  ref?: string;
  is_signed?: number;
  url?: string;
}

export interface BindBankCardPayload extends SendBindCodePayload {
  captcha: string;
  ref: string;
}

export interface BindBankCardResult {
  id?: string;
  userId?: string;
  idCard?: string;
  bankCardNo?: string;
  bankName?: string;
  bankPhone?: string;
  idCardBindId?: string;
  isVerified?: number;
}

/* -------------------- 提现模块联调开关 -------------------- */
// 纯本地测试启用 true，真实联调请保持 false
const USE_MOCK_DATA = true;

const MOCK_WALLET_DETAIL: WalletDetail = {
  withdrawable_balance: '10000.00',
  frozen_balance: '500.00',
  total_income: '50000.00',
  pending_withdraw_amount: '2000.00',
  today_income: '150.00',
  total_withdrawn: '38000.00',
  next_release_at: Date.now() + 86400000,
};

function resolveBizErrorMessage(response: any, fallback: string) {
  return response?.data?.msg || response?.data?.message || fallback;
}

function isBizFailure(response: any) {
  const code = Number(response?.data?.code);
  return !Number.isNaN(code) && code !== 200;
}

export function fetchWalletDetail(
  success: ApiSuccess<WalletDetail>,
  failure: ApiFailure,
) {
  if (USE_MOCK_DATA) {
    console.log('提现模块-钱包详情-使用 Mock 数据');
    console.log('提现模块-钱包详情-成功返回', MOCK_WALLET_DETAIL);
    success({ ...MOCK_WALLET_DETAIL });
    return;
  }

  console.log('提现模块-钱包详情-请求参数', {});
  ApiRequest(
    '/wallet/detail',
    {},
    (response: any) => {
      if (isBizFailure(response)) {
        console.log('提现模块-钱包详情-业务失败返回', response.data);
        failure(resolveBizErrorMessage(response, '钱包详情加载失败'));
        return;
      }
      console.log('提现模块-钱包详情-成功返回', response.data);
      success(response?.data?.data || {});
    },
    (errorMessage: any) => {
      console.log('提现模块-钱包详情-失败返回', errorMessage);
      failure(errorMessage);
    },
    true,
  );
}

export function applyWithdraw(
  payload: WithdrawApplyPayload,
  success: ApiSuccess<WithdrawApplyResult>,
  failure: ApiFailure,
) {
  if (USE_MOCK_DATA) {
    console.log('提现模块-提现申请-使用 Mock 数据');
    const mockResult: WithdrawApplyResult = {
      withdrawRecordId: `mock_withdraw_${Date.now()}`,
      orderSn: `ORD${Date.now()}`,
      ref: `REF${Date.now()}`,
      amount: payload.amount,
    };
    console.log('提现模块-提现申请-成功返回', mockResult);
    success(mockResult);
    return;
  }

  if (payload.channel === 'bank') {
    const requestPayload = {
      idCard: payload.idCard || '',
      bankCardNo: payload.bankCardNo || '',
      amount: toMoney(payload.amount),
    };

    console.log('提现模块-银行卡提现-请求参数', requestPayload);
    ApiRequest(
      '/withdraw/payment/bank-card',
      requestPayload,
      (response: any) => {
        if (isBizFailure(response)) {
          console.log('提现模块-银行卡提现-业务失败返回', response.data);
          failure(resolveBizErrorMessage(response, '银行卡提现失败'));
          return;
        }
        console.log('提现模块-银行卡提现-成功返回', response.data);
        success(response?.data?.data || {});
      },
      (errorMessage: any) => {
        console.log('提现模块-银行卡提现-失败返回', errorMessage);
        failure(errorMessage);
      },
      true,
    );
    return;
  }

  console.log('提现模块-微信提现-请求参数', payload);
  ApiRequest(
    '/wallet/withdraw/apply',
    payload,
    (response: any) => {
      if (isBizFailure(response)) {
        console.log('提现模块-微信提现-业务失败返回', response.data);
        failure(resolveBizErrorMessage(response, '微信提现失败'));
        return;
      }
      console.log('提现模块-微信提现-成功返回', response.data);
      success(response?.data?.data || {});
    },
    (errorMessage: any) => {
      console.log('提现模块-微信提现-失败返回', errorMessage);
      failure(errorMessage);
    },
    true,
  );
}

export function requestWechatWithdraw(
  payload: WechatWithdrawPayload,
  success: ApiSuccess<{ prepared: boolean }>,
  failure: ApiFailure,
) {
  if (USE_MOCK_DATA) {
    console.log('提现模块-微信提现-使用 Mock 数据');
    console.log('提现模块-微信提现-成功返回', { prepared: true });
    success({ prepared: true });
    return;
  }

  console.log('提现模块-微信提现-请求参数', payload);
  ApiRequest(
    '/wallet/withdraw/apply',
    {
      channel: 'wechat',
      amount: toMoney(payload.amount),
    },
    (response: any) => {
      if (isBizFailure(response)) {
        console.log('提现模块-微信提现-业务失败返回', response.data);
        failure(resolveBizErrorMessage(response, '微信提现失败'));
        return;
      }
      console.log('提现模块-微信提现-成功返回', response.data);
      success({ prepared: true });
    },
    (errorMessage: any) => {
      console.log('提现模块-微信提现-失败返回', errorMessage);
      failure(errorMessage);
    },
    true,
  );
}

export function fetchBoundAccounts(
  success: ApiSuccess<AccountListResult>,
  failure: ApiFailure,
) {
  if (USE_MOCK_DATA) {
    console.log('提现模块-查询已绑定账户-使用 Mock 数据');
    const mockAccounts: WithdrawAccount[] = [
      {
        id: 'mock-wechat-1',
        type: 'wechat',
        bankName: '微信',
        accountName: '张三',
        maskedAccount: '微信号: wxid_****',
        bankCardNo: '',
        wxAccount: 'wxid_xxxxx',
        limitText: '单笔5万/日10万',
        logoText: '微',
        logoClass: 'logo-wechat',
        recommended: true,
      },
      {
        id: 'mock-bank-1',
        type: 'bank',
        bankName: '中国工商银行',
        accountName: '张三',
        maskedAccount: '622202*******1234',
        bankCardNo: '622202123456781234',
        wxAccount: '',
        limitText: '单笔5万/日10万',
        logoText: '卡',
        logoClass: 'logo-bank',
        recommended: false,
      },
    ];
    const mockResult: AccountListResult = {
      hasBoundAccounts: true,
      accounts: mockAccounts,
    };
    console.log('提现模块-查询已绑定账户-成功返回', mockResult);
    success(mockResult);
    return;
  }

  console.log('提现模块-查询已绑定账户-请求参数', {});
  ApiRequest(
    '/wallet/withdraw/accounts',
    {},
    (response: any) => {
      if (isBizFailure(response)) {
        console.log('提现模块-查询已绑定账户-业务失败返回', response.data);
        failure(resolveBizErrorMessage(response, '收款账号加载失败'));
        return;
      }
      console.log('提现模块-查询已绑定账户-成功返回', response.data);
      const data = response?.data?.data || {};
      const rawAccounts = Array.isArray(data.accounts) ? data.accounts : [];
      const normalizedAccounts: WithdrawAccount[] = rawAccounts
        .filter((item: any) => item && typeof item === 'object')
        .map((item: any, index: number) => {
          const channel = item.type || item.accountType || 'bank';
          const maskedAccount = item.maskedAccount || item.maskedAccountNo || '';
          const bankCardNo = item.bankCardNo || item.accountNo || '';

          return {
            id: item.id || `withdraw-account-${index}`,
            type: channel === 'wechat' ? 'wechat' : 'bank',
            bankName: item.bankName || (channel === 'wechat' ? '微信' : '银行卡'),
            accountName: item.accountName || '',
            maskedAccount,
            bankCardNo,
            wxAccount: item.wxAccount || '',
            limitText: item.limitText || '',
            logoText: item.logoText || (channel === 'wechat' ? '微' : '卡'),
            logoClass: item.logoClass || (channel === 'wechat' ? 'logo-wechat' : 'logo-bank'),
            recommended: Boolean(item.recommended),
          };
        });

      success({
        hasBoundAccounts: Boolean(data.hasBoundAccounts),
        accounts: normalizedAccounts,
      });
    },
    (errorMessage: any) => {
      console.log('提现模块-查询已绑定账户-失败返回', errorMessage);
      failure(errorMessage);
    },
    true,
  );
}

export function verifyBindIdentity(
  payload: IdentityVerifyForm,
  success: ApiSuccess<IdentityVerifyResult>,
  failure: ApiFailure,
) {
  if (USE_MOCK_DATA) {
    console.log('提现模块-身份验证-使用 Mock 数据');
    const mockResult: IdentityVerifyResult = {
      is_signed: 1,
    };
    console.log('提现模块-身份验证-成功返回', mockResult);
    success(mockResult);
    return;
  }

  const requestPayload = {
    realName: payload.realName,
    idCard: payload.identityNo,
  };

  console.log('提现模块-身份验证-请求参数', requestPayload);
  ApiRequest(
    '/withdraw/sign/check',
    requestPayload,
    (response: any) => {
      if (isBizFailure(response)) {
        console.log('提现模块-身份验证-业务失败返回', response.data);
        failure(resolveBizErrorMessage(response, '身份验证失败'));
        return;
      }
      console.log('提现模块-身份验证-成功返回', response.data);
      success(response?.data?.data || { is_signed: 0 });
    },
    (errorMessage: any) => {
      console.log('提现模块-身份验证-失败返回', errorMessage);
      failure(errorMessage);
    },
    true,
  );
}

export function sendBindVerificationCode(
  payload: SendBindCodePayload,
  success: ApiSuccess<SendBindCodeResult>,
  failure: ApiFailure,
) {
  if (USE_MOCK_DATA) {
    console.log('提现模块-发送绑卡验证码-使用 Mock 数据');
    const mockResult: SendBindCodeResult = {
      step: 'verify_code',
      ref: `mock_ref_${Date.now()}`,
      is_signed: 1,
    };
    console.log('提现模块-发送绑卡验证码-成功返回', mockResult);
    success(mockResult);
    return;
  }

  console.log('提现模块-发送绑卡验证码-请求参数', payload);
  ApiRequest(
    '/withdraw/bind/bank-card/send-code',
    payload,
    (response: any) => {
      if (isBizFailure(response)) {
        console.log('提现模块-发送绑卡验证码-业务失败返回', response.data);
        failure(resolveBizErrorMessage(response, '验证码发送失败'));
        return;
      }
      console.log('提现模块-发送绑卡验证码-成功返回', response.data);
      success(response?.data?.data || {});
    },
    (errorMessage: any) => {
      console.log('提现模块-发送绑卡验证码-失败返回', errorMessage);
      failure(errorMessage);
    },
    true,
  );
}

export function submitBindBankCardRequest(
  payload: BindBankCardPayload,
  success: ApiSuccess<BindBankCardResult>,
  failure: ApiFailure,
) {
  if (USE_MOCK_DATA) {
    console.log('提现模块-绑定银行卡-使用 Mock 数据');
    const mockResult: BindBankCardResult = {
      id: `mock_card_id_${Date.now()}`,
      userId: 'mock_user_123',
      idCard: payload.idCard,
      bankCardNo: payload.bankCardNo,
      bankName: payload.bankName,
      bankPhone: payload.bankPhone,
      idCardBindId: `mock_bind_id_${Date.now()}`,
      isVerified: 1,
    };
    console.log('提现模块-绑定银行卡-成功返回', mockResult);
    success(mockResult);
    return;
  }

  console.log('提现模块-绑定银行卡-请求参数', payload);
  ApiRequest(
    '/withdraw/bind/bank-card',
    payload,
    (response: any) => {
      if (isBizFailure(response)) {
        console.log('提现模块-绑定银行卡-业务失败返回', response.data);
        failure(resolveBizErrorMessage(response, '绑定银行卡失败'));
        return;
      }
      console.log('提现模块-绑定银行卡-成功返回', response.data);
      success(response?.data?.data || {});
    },
    (errorMessage: any) => {
      console.log('提现模块-绑定银行卡-失败返回', errorMessage);
      failure(errorMessage);
    },
    true,
  );
}

export function validateBankCardInput(
  payload: BankValidationPayload,
  success: ApiSuccess<{ valid: boolean }>,
  _failure: ApiFailure,
) {
  console.log('提现模块-银行卡校验-请求参数', payload);
  const mockResponse = {
    code: 200,
    msg: '银行卡校验通过',
    data: { valid: true },
  };
  console.log('提现模块-银行卡校验-成功返回', mockResponse);
  success(mockResponse.data);
}

export function validateBankNameInput(
  payload: BankValidationPayload,
  success: ApiSuccess<{ valid: boolean }>,
  _failure: ApiFailure,
) {
  console.log('提现模块-所属银行校验-请求参数', payload);
  const mockResponse = {
    code: 200,
    msg: '所属银行校验通过',
    data: { valid: true },
  };
  console.log('提现模块-所属银行校验-成功返回', mockResponse);
  success(mockResponse.data);
}

export function buildApplyPayload(amount: string, account: WithdrawAccount, idCard = ''): WithdrawApplyPayload {
  if (account.type === 'wechat') {
    return {
      amount: toMoney(amount),
      channel: 'wechat',
      wxAccount: account.wxAccount || account.maskedAccount,
    };
  }

  return {
    amount: toMoney(amount),
    channel: 'bank',
    idCard,
    bankCardNo: account.bankCardNo || account.maskedAccount.replace(/\D/g, ''),
    bankName: account.bankName,
  };
}

