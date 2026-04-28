import type { BindBankForm, IdentityVerifyForm } from './types';

export function normalizeMoney(value: string) {
  const normalized = String(value || '').replace(/[^\d.]/g, '');
  const firstDot = normalized.indexOf('.');
  const compact =
    firstDot === -1
      ? normalized
      : normalized.slice(0, firstDot + 1) + normalized.slice(firstDot + 1).replace(/\./g, '');
  const [integer = '', decimal = ''] = compact.split('.');
  const safeInteger = integer.replace(/^0+(?=\d)/, '') || '0';
  return compact.includes('.') ? `${safeInteger}.${decimal.slice(0, 2)}` : safeInteger;
}

//将金额字符串转为两位小数格式，用于提交提现申请
export function toMoney(value: string) {
  return Number(normalizeMoney(value) || 0).toFixed(2);
}


export function normalizeBankCard(cardNo: string) {
  return String(cardNo || '').replace(/\D/g, '').slice(0, 32);
}

export function normalizeIdentityNo(identityNo: string) {
  return String(identityNo || '').replace(/[^0-9Xx]/g, '').slice(0, 18).toUpperCase();
}

export function normalizePhone(phone: string) {
  return String(phone || '').replace(/\D/g, '').slice(0, 11);
}


export function sanitizeName(name: string) {
  return String(name || '').replace(/[^\u4e00-\u9fa5a-zA-Z·\s]/g, '').trim().slice(0, 24);
}

export function maskPhone(phone: string) {
  const normalized = normalizePhone(phone);
  if (normalized.length < 7) {
    return normalized || phone;
  }
  return `${normalized.slice(0, 3)}****${normalized.slice(-4)}`;
}


export function maskBankCard(cardNo: string) {
  const normalized = normalizeBankCard(cardNo);
  if (normalized.length < 8) {
    return normalized;
  }
  return `${normalized.slice(0, 4)} **** **** ${normalized.slice(-4)}`;
}

export function sanitizeBindBankForm(form: BindBankForm): BindBankForm {
  return {
    verifiedRealName: sanitizeName(form.verifiedRealName),
    verifiedIdentityNo: normalizeIdentityNo(form.verifiedIdentityNo),
    bankCardNo: normalizeBankCard(form.bankCardNo),
    phone: normalizePhone(form.phone),
    bankName: sanitizeName(form.bankName) || '银行卡',
    verificationCode: String(form.verificationCode || '').replace(/\D/g, '').slice(0, 6),
  };
}


export function sanitizeIdentityVerifyForm(form: IdentityVerifyForm): IdentityVerifyForm {
  return {
    realName: sanitizeName(form.realName),
    identityNo: normalizeIdentityNo(form.identityNo),
  };
}

export function isValidBankCard(cardNo: string) {
  return /^\d{8,32}$/.test(normalizeBankCard(cardNo));
}
