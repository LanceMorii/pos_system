import request from '@/utils/request'

// 获取分类列表
export function getCategoryList(params: any) {
  return request({
    url: '/supplier-category/list',
    method: 'get',
    params
  })
}

// 获取分类详情
export function getCategoryById(id: number) {
  return request({
    url: `/supplier-category/${id}`,
    method: 'get'
  })
}

// 创建分类
export function createCategory(data: any) {
  return request({
    url: '/supplier-category',
    method: 'post',
    data
  })
}

// 更新分类
export function updateCategory(id: number, data: any) {
  return request({
    url: `/supplier-category/${id}`,
    method: 'put',
    data
  })
}

// 删除分类
export function deleteCategory(id: number) {
  return request({
    url: `/supplier-category/${id}`,
    method: 'delete'
  })
}

// 更新分类状态
export function updateCategoryStatus(id: number, status: string) {
  return request({
    url: `/supplier-category/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 获取启用状态的分类列表
export function getActiveCategories() {
  return request({
    url: '/supplier-category/active',
    method: 'get'
  })
}