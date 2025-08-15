import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, register as registerApi, type LoginRequest, type RegisterRequest } from '@/api/user'

interface UserInfo {
  id: string
  username: string
  realName: string
  phone: string
  email: string
  role: string
  avatar: string
  permissions: string[]
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo>({
    id: '',
    username: '',
    realName: '',
    phone: '',
    email: '',
    role: '',
    avatar: '',
    permissions: []
  })

  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
  }

  const clearUserInfo = () => {
    userInfo.value = {
      id: '',
      username: '',
      realName: '',
      phone: '',
      email: '',
      role: '',
      avatar: '',
      permissions: []
    }
  }

  // 登录
  const login = async (loginData: LoginRequest) => {
    try {
      const response = await loginApi(loginData)
      const { data } = response
      
      // 保存token
      localStorage.setItem('token', data.token)
      
      // 保存用户信息
      setUserInfo(data.user)
      
      return response
      
      // 保存token
      localStorage.setItem('token', data.token)
      
      // 保存用户信息
      const userInfo: UserInfo = {
        id: data.userId.toString(),
        username: data.username,
        realName: data.realName,
        phone: data.phone || '',
        email: data.email || '',
        role: data.role,
        avatar: data.avatar || '',
        permissions: data.permissions || []
      }
      
      setUserInfo(userInfo)
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      
      return response
    } catch (error) {
      throw error
    }
  }

  // 注册
  const register = async (registerData: RegisterRequest) => {
    try {
      const response = await registerApi(registerData)
      return response
    } catch (error) {
      throw error
    }
  }

  // 登出
  const logout = () => {
    clearUserInfo()
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 初始化用户信息（从localStorage恢复）
  const initUserInfo = () => {
    const token = localStorage.getItem('token')
    const savedUserInfo = localStorage.getItem('userInfo')
    
    if (token && savedUserInfo) {
      try {
        const parsedUserInfo = JSON.parse(savedUserInfo)
        setUserInfo(parsedUserInfo)
      } catch (error) {
        console.error('解析用户信息失败:', error)
        logout()
      }
    }
  }

  return {
    userInfo,
    setUserInfo,
    clearUserInfo,
    login,
    register,
    logout,
    initUserInfo
  }
})