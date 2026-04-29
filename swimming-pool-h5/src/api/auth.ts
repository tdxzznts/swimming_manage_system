import { request } from '@/utils/request'
import type { R } from '@/types/user'

/**
 * 登录请求参数（完全匹配Plus-UI）
 */
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

/**
 * 登录响应
 */
export interface LoginResult {
  access_token: string
}

/**
 * 验证码返回
 */
export interface VerifyCodeResult {
  captchaEnabled: boolean
  uuid?: string
  img?: string
}

/**
 * 注册请求参数
 */
export interface RegisterRequest {
  username: string
  password: string
  confirmPassword: string
  email: string
  verifyCode: string
}

// H5端固定使用app客户端 - 使用数据库中实际的client_id值
const CLIENT_ID = '428a8310cd442757ae699df5d894f051'

/**
 * 登录 - 完全匹配Plus-UI的实现
 */
export function login(data: LoginData): Promise<R<LoginResult>> {
  const params = {
    ...data,
    clientId: data.clientId || CLIENT_ID,
    grantType: data.grantType || 'password'
  }
  return request.post<R<LoginResult>>('/auth/login', params)
}

/**
 * 注册 - H5专用注册接口
 */
export function register(data: RegisterRequest): Promise<R<void>> {
  return request.post<R<void>>('/h5/auth/register', data)
}

/**
 * 发送邮箱验证码
 */
export function sendEmailCode(email: string): Promise<R<void>> {
  return request.post<R<void>>(`/h5/auth/sendEmailCode?email=${email}`)
}

/**
 * 获取验证码 - 匹配Plus-UI
 */
export function getCodeImg(): Promise<R<VerifyCodeResult>> {
  return request.get<R<VerifyCodeResult>>('/auth/code')
}

/**
 * 获取用户信息 - 匹配Plus-UI
 */
export function getInfo(): Promise<R<any>> {
  return request.get<R<any>>('/system/user/getInfo')
}

/**
 * 退出登录 - 匹配Plus-UI
 */
export function logout(): Promise<R<void>> {
  return request.post<R<void>>('/auth/logout')
}
