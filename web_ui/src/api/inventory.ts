import request from '@/utils/request'

// 获取库存列表
export function getInventoryList(params: any) {
  return request({
    url: '/inventory/list',
    method: 'get',
    params
  })
}

// 获取库存预警商品列表
export function getLowStockProducts() {
  return request({
    url: '/inventory/low-stock',
    method: 'get'
  })
}

// 商品入库
export function stockIn(data: any) {
  return request({
    url: '/inventory/stock-in',
    method: 'post',
    data
  })
}

// 商品出库
export function stockOut(data: any) {
  return request({
    url: '/inventory/stock-out',
    method: 'post',
    data
  })
}

// 获取商品库存记录
export function getStockRecords(productId: number, params: any) {
  return request({
    url: `/inventory/records/${productId}`,
    method: 'get',
    params
  })
}

// 批量入库
export function batchStockIn(data: any[]) {
  return request({
    url: '/inventory/batch-stock-in',
    method: 'post',
    data
  })
}

// 批量出库
export function batchStockOut(data: any[]) {
  return request({
    url: '/inventory/batch-stock-out',
    method: 'post',
    data
  })
}

// 库存盘点
export function inventoryCheck(data: any) {
  return request({
    url: '/inventory/inventory-check',
    method: 'post',
    data
  })
}

// 获取库存统计
export function getInventoryStatistics(params: any) {
  return request({
    url: '/inventory/statistics',
    method: 'get',
    params
  })
}

// 导出库存报表
export function exportInventoryReport(params: any) {
  return request({
    url: '/inventory/export',
    method: 'get',
    params
  })
}

// 设置库存预警阈值
export function setAlertThreshold(data: any) {
  return request({
    url: '/inventory/set-alert-threshold',
    method: 'post',
    data
  })
}