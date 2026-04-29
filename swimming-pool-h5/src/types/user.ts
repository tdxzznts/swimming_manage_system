/**
 * 用户类型定义 - 匹配Plus-UI
 */

/**
 * API响应结构 - R对象
 */
export interface R<T = any> {
  code: number
  msg: string
  data: T
}

export interface LoginData {
  tenantId?: string
  username?: string
  password?: string
  rememberMe?: boolean
  code?: string
  uuid?: string
  clientId: string
  grantType: string
}

export interface LoginResult {
  access_token: string
}

export interface VerifyCodeResult {
  captchaEnabled: boolean
  uuid?: string
  img?: string
}

export interface RegisterRequest {
  username: string
  password: string
  confirmPassword: string
  email: string
  verifyCode: string
}

export interface UserInfo {
  userId: number
  username: string
  nickname?: string
  nickName?: string
  email: string
  phonenumber: string
  avatar?: string
  tenantId?: string
  roles?: string[]
  permissions?: string[]
}

/**
 * 用户统计数据
 */
export interface UserStatistics {
  reservationCount: number
  totalHours: number
  balance: number
  totalPoints: number
}
