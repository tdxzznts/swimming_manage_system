import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoginData, UserInfo } from '@/types/user'
import { login as loginApi, logout as logoutApi, getInfo as getUserInfoApi } from '@/api/auth'
import { setToken, removeToken, getToken } from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const token = ref<string>(getToken() || '')
  const roles = ref<string[]>([])
  const permissions = ref<string[]>([])
  const isLogin = ref<boolean>(!!token.value)

  /**
   * 登录 - 完全匹配Plus-UI的实现
   */
  const login = async (loginForm: LoginData): Promise<void> => {
    const res = await loginApi(loginForm)
    if (res.code === 200 && res.data) {
      const accessToken = res.data.access_token
      token.value = accessToken
      isLogin.value = true
      setToken(accessToken)

      // 登录成功后获取用户信息
      await getInfo()

      return Promise.resolve()
    }
    return Promise.reject(res)
  }

  /**
   * 获取用户信息 - 匹配Plus-UI
   */
  const getInfo = async (): Promise<void> => {
    const res = await getUserInfoApi()
    if (res.code === 200 && res.data) {
      const user = res.data.user
      userInfo.value = user
      if (res.data.roles && res.data.roles.length > 0) {
        roles.value = res.data.roles
        permissions.value = res.data.permissions
      } else {
        roles.value = ['ROLE_DEFAULT']
      }
      // 保存到localStorage
      localStorage.setItem('swimming_user', JSON.stringify(userInfo.value))
      return Promise.resolve()
    }
    return Promise.reject(res)
  }

  /**
   * 退出登录 - 匹配Plus-UI
   */
  const logout = async () => {
    try {
      await logoutApi()
    } finally {
      token.value = ''
      userInfo.value = null
      roles.value = []
      permissions.value = []
      isLogin.value = false
      removeToken()
      localStorage.removeItem('swimming_user')
      sessionStorage.clear()
    }
  }

  /**
   * 注册
   */
  const register = async (data: any) => {
    const res = await (await import('@/api/auth')).register(data)
    return res.code === 200
  }

  /**
   * 重置状态
   */
  const resetState = () => {
    token.value = ''
    userInfo.value = null
    roles.value = []
    permissions.value = []
    isLogin.value = false
  }

  return {
    userInfo,
    token,
    roles,
    permissions,
    isLogin,
    login,
    getInfo,
    logout,
    register,
    resetState
  }
})
