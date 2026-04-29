import { request } from '@/utils/request'
import type { R, UserStatistics } from '@/types/user'

/**
 * 用户信息表单
 */
export interface UserProfile {
  userId?: number
  nickName?: string
  phonenumber?: string
  email?: string
  sex?: string
}

/**
 * 密码修改表单
 */
export interface PasswordForm {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

/**
 * 获取用户个人信息
 */
export function getUserProfile(): Promise<R<any>> {
  return request.get<R<any>>('/system/user/profile')
}

/**
 * 修改用户个人信息
 */
export function updateUserProfile(data: UserProfile): Promise<R<void>> {
  return request.put<R<void>>('/system/user/profile', data)
}

/**
 * 修改用户密码
 */
export function updateUserPwd(oldPassword: string, newPassword: string): Promise<R<void>> {
  return request.put<R<void>>('/system/user/profile/updatePwd', {
    oldPassword,
    newPassword
  })
}

/**
 * 上传用户头像
 */
export function uploadAvatar(formData: FormData): Promise<R<{ imgUrl: string }>> {
  return request.upload<R<{ imgUrl: string }>>('/system/user/profile/avatar', formData)
}

/**
 * 获取用户统计数据
 */
export function getUserStatistics(): Promise<R<UserStatistics>> {
  return request.get<UserStatistics>('/h5/user/statistics')
}
