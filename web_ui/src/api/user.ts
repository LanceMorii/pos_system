import request from '@/utils/request'

// 登录
export function login(data: { username: string; password: string }) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 注册
export function register(data: { username: string; password: string; email?: string; realName?: string }) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/profile/info',
    method: 'get'
  })
}

// 获取用户列表
export function getUserList(params: any) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

// 添加用户
export function addUser(data: any) {
  return request({
    url: '/user/add',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(data: any) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id: number) {
  return request({
    url: `/user/delete/${id}`,
    method: 'delete'
  })
}

// 获取用户详细信息
export function getUserProfile() {
  return request({
    url: '/profile/info',
    method: 'get'
  })
}

// 更新个人信息
export function updateUserProfile(data: any) {
  return request({
    url: '/profile/update',
    method: 'put',
    data
  })
}

// 修改密码
export function changeUserPassword(data: { oldPassword: string; newPassword: string }) {
  return request({
    url: '/profile/password',
    method: 'put',
    data
  })
}

// 获取收银员列表
export function getCashierList() {
  return request({
    url: '/user/cashiers',
    method: 'get'
  })
}