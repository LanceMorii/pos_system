import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    // 仅当 token 看起来像 JWT（包含两个点）时才附带 Authorization
    if (token && token.split('.').length === 3) {
      config.headers['Authorization'] = `Bearer ${token}`
    } else {
      // 防止后端解析无效 token
      delete (config.headers as any)['Authorization']
    }
    return config
  },
  error => {
    console.error(error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // API 测试器可通过配置 bypassUnified 跳过统一处理并拿到完整响应
    if ((response.config as any).bypassUnified) {
      return response
    }
    // 如果是文件下载请求（Blob类型），直接返回response.data
    if (response.config.responseType === 'blob') {
      return response.data
    }

    const res = response.data
    const url = response.config.url || ''
    
    // 对于收银台接口或 OpenAPI 文档，直接返回原始响应数据
    if (url.includes('/cashier/') || url.includes('/v3/api-docs') || (res && typeof res === 'object' && (res.openapi || res.paths))) {
      return res
    }
    
    if (res.code !== 200) {
      ElMessage({
        message: res.message || '系统错误',
        type: 'error',
        duration: 5 * 1000
      })

      // 401: 未登录或token过期
      if (res.code === 401) {
        const skip = (response.config as any).skipAuthRedirect
        if (!skip) {
          localStorage.removeItem('token')
          router.push('/login')
        }
      }

      return Promise.reject(new Error(res.message || '系统错误'))
    } else {
      return res
    }
  },
  error => {
    console.error('请求错误', error)
    ElMessage({
      message: error.message || '请求失败',
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service