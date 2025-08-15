import request from '@/utils/request'

export interface OrderListQuery {
  page?: number
  limit?: number
  orderNo?: string
  cashier?: string
  startTime?: string
  endTime?: string
}

export function getOrderList(params: OrderListQuery) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

export function getOrderById(id: number | string) {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}

export function getOrderByOrderNo(orderNo: string) {
  return request({
    url: `/order/orderNo/${encodeURIComponent(orderNo)}`,
    method: 'get'
  })
}

export function exportOrders(params: OrderListQuery) {
  return request({
    url: '/order/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function updateOrder(id: number | string, data: any) {
  return request({
    url: `/order/${id}`,
    method: 'put',
    data
  })
}

export function cancelOrder(id: number | string) {
  return request({
    url: `/order/${id}/cancel`,
    method: 'put'
  })
}

export function refundOrder(id: number | string, data: any = {}) {
  return request({
    url: `/order/${id}/refund`,
    method: 'put',
    data
  })
}

export function deleteOrder(id: number | string) {
  return request({
    url: `/order/${id}`,
    method: 'delete'
  })
}
