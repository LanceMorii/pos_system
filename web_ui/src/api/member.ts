import request from '@/utils/request'

// 获取会员列表
export function getMemberList(params: any) {
  return request({
    url: '/member/list',
    method: 'get',
    params
  })
}

// 获取会员详情
export function getMemberById(id: number) {
  return request({
    url: `/member/${id}`,
    method: 'get'
  })
}

// 根据会员编号获取会员详情
export function getMemberByNo(memberNo: string) {
  return request({
    url: `/member/no/${memberNo}`,
    method: 'get'
  })
}

// 根据手机号获取会员详情
export function getMemberByPhone(phone: string) {
  return request({
    url: `/member/phone/${phone}`,
    method: 'get'
  })
}

// 创建会员
export function createMember(data: any) {
  return request({
    url: '/member',
    method: 'post',
    data
  })
}

// 更新会员
export function updateMember(id: number, data: any) {
  return request({
    url: `/member/${id}`,
    method: 'put',
    data
  })
}

// 删除会员
export function deleteMember(id: number) {
  return request({
    url: `/member/${id}`,
    method: 'delete'
  })
}

// 更新会员状态
export function updateMemberStatus(id: number, status: number) {
  return request({
    url: `/member/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 会员充值
export function rechargeMember(id: number, amount: number, remark: string) {
  return request({
    url: `/member/${id}/recharge`,
    method: 'post',
    data: { amount, remark }
  })
}

// 积分调整
export function adjustMemberPoints(id: number, points: number, type: string, remark: string) {
  return request({
    url: `/member/${id}/points`,
    method: 'post',
    data: { points, type, remark }
  })
}

// 获取会员统计信息
export function getMemberStatistics() {
  return request({
    url: '/member/statistics',
    method: 'get'
  })
}

// ========== 会员等级管理 ==========

// 获取所有会员等级
export function getMemberLevels() {
  return request({
    url: '/member-level/list',
    method: 'get'
  })
}

// 获取启用状态的会员等级
export function getActiveMemberLevels() {
  return request({
    url: '/member-level/active',
    method: 'get'
  })
}

// 获取等级详情
export function getMemberLevelById(id: number) {
  return request({
    url: `/member-level/${id}`,
    method: 'get'
  })
}

// 创建会员等级
export function createMemberLevel(data: any) {
  return request({
    url: '/member-level',
    method: 'post',
    data
  })
}

// 更新会员等级
export function updateMemberLevel(id: number, data: any) {
  return request({
    url: `/member-level/${id}`,
    method: 'put',
    data
  })
}

// 删除会员等级
export function deleteMemberLevel(id: number) {
  return request({
    url: `/member-level/${id}`,
    method: 'delete'
  })
}

// 更新等级状态
export function updateMemberLevelStatus(id: number, status: number) {
  return request({
    url: `/member-level/${id}/status`,
    method: 'put',
    data: { status }
  })
}