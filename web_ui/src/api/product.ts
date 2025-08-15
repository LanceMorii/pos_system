import request from '@/utils/request'

// 获取商品列表
export function getProductList(params: any) {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

// 获取商品详情
export function getProductById(id: number) {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

// 创建商品
export function createProduct(data: any) {
  return request({
    url: '/product/add',
    method: 'post',
    data
  })
}

// 更新商品
export function updateProduct(id: number, data: any) {
  return request({
    url: `/product/update/${id}`,
    method: 'put',
    data
  })
}

// 删除商品
export function deleteProduct(id: number) {
  return request({
    url: `/product/delete/${id}`,
    method: 'delete'
  })
}

// 批量删除商品
export function batchDeleteProducts(ids: number[]) {
  return request({
    url: '/product/batch-delete',
    method: 'delete',
    data: ids
  })
}

// 根据条形码查找商品
export function getProductByBarcode(barcode: string) {
  return request({
    url: `/product/barcode/${barcode}`,
    method: 'get'
  })
}

// 获取库存不足的商品
export function getLowStockProducts() {
  return request({
    url: '/product/low-stock',
    method: 'get'
  })
}

// 更新商品库存
export function updateProductStock(id: number, quantity: number, operation: string) {
  return request({
    url: `/product/stock/${id}`,
    method: 'put',
    params: {
      quantity,
      operation
    }
  })
}

// 获取分类列表
export function getCategoryList() {
  return request({
    url: '/category/list',
    method: 'get'
  })
}

// 创建分类
export function createCategory(data: any) {
  return request({
    url: '/category/add',
    method: 'post',
    data
  })
}

// 更新分类
export function updateCategory(id: number, data: any) {
  return request({
    url: `/category/update/${id}`,
    method: 'put',
    data
  })
}

// 删除分类
export function deleteCategory(id: number) {
  return request({
    url: `/category/delete/${id}`,
    method: 'delete'
  })
}