import request from '@/utils/request'

// 获取供应商列表
export function getSupplierList(params: any) {
  return request({
    url: '/supplier/list',
    method: 'get',
    params
  })
}

// 获取供应商详情
export function getSupplierById(id: number) {
  return request({
    url: `/supplier/${id}`,
    method: 'get'
  })
}

// 根据供应商编码获取供应商详情
export function getSupplierByCode(supplierCode: string) {
  return request({
    url: `/supplier/code/${supplierCode}`,
    method: 'get'
  })
}

// 创建供应商
export function createSupplier(data: any) {
  return request({
    url: '/supplier',
    method: 'post',
    data
  })
}

// 更新供应商
export function updateSupplier(id: number, data: any) {
  return request({
    url: `/supplier/${id}`,
    method: 'put',
    data
  })
}

// 删除供应商
export function deleteSupplier(id: number) {
  return request({
    url: `/supplier/${id}`,
    method: 'delete'
  })
}

// 更新供应商状态
export function updateSupplierStatus(id: number, status: string) {
  return request({
    url: `/supplier/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 获取所有分类
export function getSupplierCategories() {
  return request({
    url: '/supplier/categories',
    method: 'get'
  })
}

// 获取正常状态的供应商列表
export function getActiveSuppliers() {
  return request({
    url: '/supplier/active',
    method: 'get'
  })
}

// 获取供应商统计信息
export function getSupplierStatistics() {
  return request({
    url: '/supplier/statistics',
    method: 'get'
  })
}

// 批量导入供应商
export function batchImportSuppliers(data: any[]) {
  return request({
    url: '/supplier/batch-import',
    method: 'post',
    data
  })
}

// 导出供应商数据
export function exportSupplierData(params: any) {
  return request({
    url: '/supplier/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}