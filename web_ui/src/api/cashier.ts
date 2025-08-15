import request from '@/utils/request'

// 获取商品列表（收银台用）
export function getCashierProducts(params: any) {
  return request({
    url: '/cashier/products',
    method: 'get',
    params
  })
}

// 根据条码查询商品
export function getProductByBarcode(barcode: string) {
  return request({
    url: `/cashier/product/barcode/${barcode}`,
    method: 'get'
  })
}

// 创建订单
export function createOrder(data: any) {
  return request({
    url: '/cashier/order',
    method: 'post',
    data
  })
}

// 订单结算
export function checkoutOrder(data: any) {
  return request({
    url: '/cashier/checkout',
    method: 'post',
    data
  })
}

// 打印小票
export function printReceipt(orderId: string) {
  return request({
    url: `/cashier/print/${orderId}`,
    method: 'post'
  })
}

// 获取商品分类
export function getProductCategories() {
  return request({
    url: '/cashier/categories',
    method: 'get'
  })
}

// 健康检查
export function healthCheck() {
  return request({
    url: '/cashier/health',
    method: 'get'
  })
}

// 根据手机号查询会员
export function getMemberByPhone(phone: string) {
  return request({
    url: `/cashier/member/phone/${phone}`,
    method: 'get'
  })
}

// 初始化示例数据
export function initSampleData() {
  return request({
    url: '/cashier/init-sample-data',
    method: 'post'
  })
}