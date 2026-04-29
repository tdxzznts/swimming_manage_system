import axios, { type AxiosInstance, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// API基础配置
const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'
const TOKEN_KEY = 'swimming_token'

// 自定义请求配置接口
interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  hideErrorMsg?: boolean
  skipAuth?: boolean
}
axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8';
axios.defaults.headers['clientid'] = import.meta.env.VITE_APP_CLIENT_ID;
// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

/**
 * Token管理
 */
export const setToken = (token: string) => {
  localStorage.setItem(TOKEN_KEY, token)
}

export const getToken = () => {
  return localStorage.getItem(TOKEN_KEY)
}

export const removeToken = () => {
  localStorage.removeItem(TOKEN_KEY)
}

/**
 * 检查是否已登录
 */
export const isLoggedIn = () => {
  return !!getToken()
}

/**
 * 清除所有认证信息
 */
export const clearAuth = () => {
  removeToken()
  localStorage.removeItem('swimming_user')
  sessionStorage.clear()
}

/**
 * 处理未授权（401）
 */
let isHandling401 = false

function handleUnauthorized() {
  if (isHandling401) return

  isHandling401 = true

  // 清除Token
  removeToken()

  // 清除用户信息
  localStorage.removeItem('swimming_user')

  // 延迟跳转，避免重复触发
  setTimeout(() => {
    const currentPath = router.currentRoute.value.path

    // 如果不在登录页或注册页，则跳转
    if (currentPath !== '/login' && currentPath !== '/register') {
      router.push({
        path: '/login',
        query: {
          redirect: currentPath
        }
      })
    }

    isHandling401 = false
  }, 100)
}

/**
 * 请求拦截器
 */
service.interceptors.request.use(
  (config: CustomAxiosRequestConfig) => {
    // 如果是 FormData，删除 Content-Type 让浏览器自动设置
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }

    // 添加Token
    const token = getToken()
    if (token && !config.skipAuth) {
      config.headers['Authorization'] = `Bearer ${token}`
    }

    // 添加时间戳防止缓存
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now()
      }
    }

    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 */
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    const config = response.config as CustomAxiosRequestConfig

    // R对象结构: { code: number, msg: string, data: any }
    if (res.code === 200) {
      return res
    }

    // 业务错误码处理
    const errorMsg = res.msg || '请求失败'

    // 特定错误码处理
    switch (res.code) {
      case 401:
        handleUnauthorized()
        return Promise.reject(new Error('登录已过期'))
      case 403:
        if (!config.hideErrorMsg) {
          ElMessage.error('没有权限访问')
        }
        break
      default:
        if (!config.hideErrorMsg) {
          ElMessage.error(errorMsg)
        }
    }

    return Promise.reject(new Error(errorMsg))
  },
  (error) => {
    const config = error.config as CustomAxiosRequestConfig

    if (axios.isCancel(error)) {
      console.log('请求已取消:', error.message)
      return Promise.reject(error)
    }

    let message = '请求失败'

    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 401:
          message = '登录已过期，请重新登录'
          handleUnauthorized()
          break
        case 403:
          message = '没有权限访问'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = data?.msg || '服务器错误'
          break
        case 502:
          message = '网关错误'
          break
        case 503:
          message = '服务不可用'
          break
        case 504:
          message = '网关超时'
          break
        default:
          message = data?.msg || `请求失败 (${status})`
      }
    } else if (error.request) {
      message = '网络连接失败，请检查网络'
    } else if (error.message) {
      if (error.message.includes('timeout')) {
        message = '请求超时，请稍后重试'
      } else if (error.message.includes('Network Error')) {
        message = '网络连接失败'
      } else {
        message = error.message
      }
    }

    if (!config.hideErrorMsg) {
      ElMessage.error(message)
    }

    return Promise.reject(error)
  }
)

/**
 * 通用请求方法
 */
export const request = {
  get<T = any>(
    url: string,
    params?: any,
    config?: CustomAxiosRequestConfig
  ): Promise<T> {
    return service.get(url, {
      params,
      ...config
    })
  },

  post<T = any>(
    url: string,
    data?: any,
    config?: CustomAxiosRequestConfig
  ): Promise<T> {
    return service.post(url, data, config)
  },

  put<T = any>(
    url: string,
    data?: any,
    config?: CustomAxiosRequestConfig
  ): Promise<T> {
    return service.put(url, data, config)
  },

  delete<T = any>(
    url: string,
    params?: any,
    config?: CustomAxiosRequestConfig
  ): Promise<T> {
    return service.delete(url, {
      params,
      ...config
    })
  },

  /**
   * 文件上传
   */
  upload<T = any>(
    url: string,
    formData: FormData,
    onProgress?: (percent: number) => void
  ): Promise<T> {
    return service.post(url, formData, {
      headers: {
        // 不设置 Content-Type，让浏览器自动设置并生成正确的 boundary
        'Content-Type': undefined as any
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          onProgress(percent)
        }
      }
    })
  },

  /**
   * 文件下载
   */
  download(
    url: string,
    params?: any,
    filename?: string
  ): Promise<void> {
    return service.get(url, {
      params,
      responseType: 'blob'
    }).then((response: any) => {
      const blob = new Blob([response])
      const link = document.createElement('a')
      link.href = window.URL.createObjectURL(blob)
      link.download = filename || `download_${Date.now()}`
      link.click()
      window.URL.revokeObjectURL(link.href)
    })
  }
}

export default service
