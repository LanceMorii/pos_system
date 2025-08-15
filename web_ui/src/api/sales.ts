import request from '@/utils/request'

// 获取销售订单列表
export function getSalesOrders(params: any) {
  return request({
    url: '/sales/orders',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getSalesOrderDetail(id: number) {
  return request({
    url: `/sales/orders/${id}`,
    method: 'get'
  })
}

// 取消订单
export function cancelSalesOrder(id: number) {
  return request({
    url: `/sales/orders/${id}/cancel`,
    method: 'put'
  })
}

// 获取销售统计
export function getSalesStats(params: any) {
  return request({
    url: '/sales/stats',
    method: 'get',
    params
  })
}