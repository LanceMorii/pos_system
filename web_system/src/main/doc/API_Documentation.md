# 智慧超市系统 API 文档

## 基础信息
- **基础URL**: `http://localhost:8080/api`
- **认证方式**: JWT Token (Bearer Token)
- **数据格式**: JSON
- **字符编码**: UTF-8

## 通用响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 错误响应
```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

## 1. 认证模块 (Auth)

### 1.1 用户登录
- **接口**: `POST /auth/login`
- **描述**: 用户登录获取Token
- **请求参数**:
```json
{
  "username": "admin",
  "password": "123456"
}
```
- **响应数据**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "admin",
  "realName": "管理员",
  "role": "ADMIN"
}
```

### 1.2 用户注册
- **接口**: `POST /auth/register`
- **描述**: 注册新用户
- **请求参数**:
```json
{
  "username": "newuser",
  "password": "123456",
  "confirmPassword": "123456",
  "realName": "新用户",
  "phone": "13800138000",
  "email": "user@example.com",
  "role": "CASHIER"
}
```

### 1.3 用户登出
- **接口**: `POST /auth/logout`
- **描述**: 用户登出
- **请求参数**:
```json
{
  "username": "admin"
}
```

### 1.4 获取用户信息
- **接口**: `GET /auth/info`
- **描述**: 获取当前登录用户信息
- **查询参数**: `username` (可选)

### 1.5 更新个人信息
- **接口**: `PUT /auth/profile`
- **描述**: 更新个人信息

### 1.6 修改密码
- **接口**: `POST /auth/change-password`
- **描述**: 修改用户密码

### 1.7 获取用户统计
- **接口**: `GET /auth/stats`
- **描述**: 获取用户统计信息

### 1.8 获取用户权限
- **接口**: `GET /auth/permissions`
- **描述**: 获取用户权限信息

## 2. 用户管理模块 (User)

### 2.1 获取用户列表
- **接口**: `GET /user/list`
- **描述**: 获取用户列表
- **查询参数**:
  - `username` (可选): 用户名筛选
  - `realName` (可选): 真实姓名筛选
  - `role` (可选): 角色筛选
  - `status` (可选): 状态筛选

### 2.2 添加用户
- **接口**: `POST /user/add`
- **描述**: 添加新用户

### 2.3 更新用户
- **接口**: `PUT /user/update`
- **描述**: 更新用户信息

### 2.4 删除用户
- **接口**: `DELETE /user/delete/{id}`
- **描述**: 删除用户

### 2.5 获取收银员列表
- **接口**: `GET /user/cashiers`
- **描述**: 获取所有收银员列表

## 3. 商品管理模块 (Product)

### 3.1 获取商品列表
- **接口**: `GET /product/list`
- **描述**: 获取商品列表
- **查询参数**:
  - `name` (可选): 商品名称
  - `categoryId` (可选): 分类ID
  - `status` (可选): 状态

### 3.2 获取商品详情
- **接口**: `GET /product/{id}`
- **描述**: 根据ID获取商品详情

### 3.3 创建商品
- **接口**: `POST /product/add`
- **描述**: 创建新商品

### 3.4 更新商品
- **接口**: `PUT /product/update/{id}`
- **描述**: 更新商品信息

### 3.5 删除商品
- **接口**: `DELETE /product/delete/{id}`
- **描述**: 删除商品

### 3.6 批量删除商品
- **接口**: `DELETE /product/batch-delete`
- **描述**: 批量删除商品

### 3.7 根据条形码查找商品
- **接口**: `GET /product/barcode/{barcode}`
- **描述**: 根据条形码查找商品

### 3.8 获取库存不足商品
- **接口**: `GET /product/low-stock`
- **描述**: 获取库存不足的商品列表

## 4. 分类管理模块 (Category)

### 4.1 获取分类列表
- **接口**: `GET /category/list`
- **描述**: 获取商品分类列表

### 4.2 获取分类详情
- **接口**: `GET /category/{id}`
- **描述**: 获取分类详情

### 4.3 创建分类
- **接口**: `POST /category/add`
- **描述**: 创建新分类

### 4.4 更新分类
- **接口**: `PUT /category/update/{id}`
- **描述**: 更新分类信息

### 4.5 删除分类
- **接口**: `DELETE /category/delete/{id}`
- **描述**: 删除分类

## 5. 订单管理模块 (Order)

### 5.1 获取订单列表
- **接口**: `GET /order/list`
- **描述**: 获取订单列表
- **查询参数**:
  - `page` (默认1): 页码
  - `limit` (默认20): 每页数量
  - `orderNo` (可选): 订单号
  - `cashier` (可选): 收银员
  - `startTime` (可选): 开始时间
  - `endTime` (可选): 结束时间

### 5.2 获取订单详情
- **接口**: `GET /order/{id}`
- **描述**: 获取订单详情

### 5.3 创建订单
- **接口**: `POST /order/create`
- **描述**: 创建新订单

### 5.4 更新订单
- **接口**: `PUT /order/update/{id}`
- **描述**: 更新订单信息

### 5.5 取消订单
- **接口**: `PUT /order/cancel/{id}`
- **描述**: 取消订单

### 5.6 订单退款
- **接口**: `POST /order/refund/{id}`
- **描述**: 订单退款

### 5.7 导出订单数据
- **接口**: `GET /order/export`
- **描述**: 导出订单数据

## 6. 会员管理模块 (Member)

### 6.1 获取会员列表
- **接口**: `GET /member/list`
- **描述**: 获取会员列表
- **查询参数**:
  - `pageNum` (默认1): 页码
  - `pageSize` (默认10): 每页数量
  - `name` (可选): 会员姓名
  - `phone` (可选): 手机号
  - `level` (可选): 会员等级
  - `status` (可选): 状态

### 6.2 获取会员详情
- **接口**: `GET /member/{id}`
- **描述**: 获取会员详情

### 6.3 创建会员
- **接口**: `POST /member/add`
- **描述**: 创建新会员

### 6.4 更新会员
- **接口**: `PUT /member/update/{id}`
- **描述**: 更新会员信息

### 6.5 删除会员
- **接口**: `DELETE /member/delete/{id}`
- **描述**: 删除会员

### 6.6 根据手机号查询会员
- **接口**: `GET /member/phone/{phone}`
- **描述**: 根据手机号查询会员

### 6.7 会员充值
- **接口**: `POST /member/recharge`
- **描述**: 会员账户充值

### 6.8 会员积分操作
- **接口**: `POST /member/points`
- **描述**: 会员积分增减

## 7. 会员等级管理模块 (Member Level)

### 7.1 获取会员等级列表
- **接口**: `GET /member-level/list`
- **描述**: 获取会员等级列表

### 7.2 获取会员等级详情
- **接口**: `GET /member-level/{id}`
- **描述**: 获取会员等级详情

### 7.3 创建会员等级
- **接口**: `POST /member-level/add`
- **描述**: 创建新会员等级

### 7.4 更新会员等级
- **接口**: `PUT /member-level/update/{id}`
- **描述**: 更新会员等级

### 7.5 删除会员等级
- **接口**: `DELETE /member-level/delete/{id}`
- **描述**: 删除会员等级

## 8. 供应商管理模块 (Supplier)

### 8.1 获取供应商列表
- **接口**: `GET /supplier/list`
- **描述**: 获取供应商列表
- **查询参数**:
  - `supplierCode` (可选): 供应商编码
  - `supplierName` (可选): 供应商名称
  - `contactPerson` (可选): 联系人
  - `category` (可选): 分类
  - `status` (可选): 状态

### 8.2 获取供应商详情
- **接口**: `GET /supplier/{id}`
- **描述**: 根据ID获取供应商详情

### 8.3 根据编码获取供应商
- **接口**: `GET /supplier/code/{supplierCode}`
- **描述**: 根据供应商编码获取详情

### 8.4 创建供应商
- **接口**: `POST /supplier`
- **描述**: 创建新供应商

### 8.5 更新供应商
- **接口**: `PUT /supplier/{id}`
- **描述**: 更新供应商信息

### 8.6 删除供应商
- **接口**: `DELETE /supplier/{id}`
- **描述**: 删除供应商

### 8.7 更新供应商状态
- **接口**: `PUT /supplier/{id}/status`
- **描述**: 更新供应商状态

### 8.8 获取供应商分类
- **接口**: `GET /supplier/categories`
- **描述**: 获取所有供应商分类

### 8.9 获取活跃供应商
- **接口**: `GET /supplier/active`
- **描述**: 获取正常状态的供应商列表

### 8.10 获取供应商统计
- **接口**: `GET /supplier/statistics`
- **描述**: 获取供应商统计信息

### 8.11 批量导入供应商
- **接口**: `POST /supplier/batch-import`
- **描述**: 批量导入供应商

### 8.12 导出供应商数据
- **接口**: `GET /supplier/export`
- **描述**: 导出供应商数据

### 8.13 测试接口
- **接口**: `GET /supplier/test`
- **描述**: 验证供应商服务是否正常

## 9. 供应商分类管理模块 (Supplier Category)

### 9.1 获取分类列表
- **接口**: `GET /supplier-category/list`
- **描述**: 获取供应商分类列表
- **查询参数**:
  - `page` (默认1): 页码
  - `size` (默认10): 每页数量

### 9.2 获取分类详情
- **接口**: `GET /supplier-category/{id}`
- **描述**: 获取分类详情

### 9.3 创建分类
- **接口**: `POST /supplier-category`
- **描述**: 创建新分类

### 9.4 更新分类
- **接口**: `PUT /supplier-category/{id}`
- **描述**: 更新分类信息

### 9.5 删除分类
- **接口**: `DELETE /supplier-category/{id}`
- **描述**: 删除分类

### 9.6 更新分类状态
- **接口**: `PUT /supplier-category/{id}/status`
- **描述**: 更新分类状态

### 9.7 获取活跃分类
- **接口**: `GET /supplier-category/active`
- **描述**: 获取启用状态的分类列表

### 9.8 测试接口
- **接口**: `GET /supplier-category/test`
- **描述**: 验证分类管理服务是否正常

## 10. 库存管理模块 (Inventory)

### 10.1 获取库存列表
- **接口**: `GET /inventory/list`
- **描述**: 获取库存列表

### 10.2 获取库存预警
- **接口**: `GET /inventory/low-stock`
- **描述**: 获取库存预警商品

### 10.3 商品入库
- **接口**: `POST /inventory/stock-in`
- **描述**: 商品入库操作

### 10.4 商品出库
- **接口**: `POST /inventory/stock-out`
- **描述**: 商品出库操作

### 10.5 获取库存记录
- **接口**: `GET /inventory/records`
- **描述**: 获取库存变动记录

### 10.6 库存盘点
- **接口**: `POST /inventory/check`
- **描述**: 库存盘点操作

### 10.7 获取库存统计
- **接口**: `GET /inventory/statistics`
- **描述**: 获取库存统计报表

### 10.8 导出库存报表
- **接口**: `GET /inventory/export`
- **描述**: 导出库存报表

### 10.9 设置库存预警
- **接口**: `PUT /inventory/alert-threshold`
- **描述**: 设置库存预警阈值

## 11. 销售管理模块 (Sales)

### 11.1 获取销售订单
- **接口**: `GET /sales/orders`
- **描述**: 获取销售订单列表
- **查询参数**:
  - `keyword` (可选): 关键词搜索
  - `status` (可选): 订单状态
  - `startDate` (可选): 开始日期
  - `endDate` (可选): 结束日期
  - `page` (默认0): 页码
  - `size` (默认10): 每页数量

### 11.2 获取订单详情
- **接口**: `GET /sales/orders/{id}`
- **描述**: 获取订单详情

### 11.3 取消订单
- **接口**: `PUT /sales/orders/{id}/cancel`
- **描述**: 取消订单

### 11.4 获取销售统计
- **接口**: `GET /sales/stats`
- **描述**: 获取销售统计数据
- **查询参数**:
  - `startDate` (可选): 开始日期
  - `endDate` (可选): 结束日期

## 12. 个人信息模块 (Profile)

### 12.1 获取个人信息
- **接口**: `GET /profile/info`
- **描述**: 获取当前用户个人信息

### 12.2 更新个人信息
- **接口**: `PUT /profile/update`
- **描述**: 更新个人信息

### 12.3 修改密码
- **接口**: `PUT /profile/password`
- **描述**: 修改密码

### 12.4 获取权限信息
- **接口**: `GET /profile/permissions`
- **描述**: 获取用户权限信息

## 13. 系统管理模块 (System)

### 13.1 系统健康检查
- **接口**: `GET /system/health`
- **描述**: 系统健康检查

### 13.2 系统信息
- **接口**: `GET /system/info`
- **描述**: 获取系统信息

### 13.3 系统指标
- **接口**: `GET /system/metrics`
- **描述**: 获取系统指标

## 14. 健康检查模块 (Health)

### 14.1 健康检查
- **接口**: `GET /health`
- **描述**: 应用健康检查

## 15. 测试模块 (Test)

### 15.1 数据库状态检查
- **接口**: `GET /test/database-status`
- **描述**: 检查数据库连接状态

### 15.2 创建测试订单
- **接口**: `POST /test/create-order`
- **描述**: 创建测试订单

### 15.3 获取仪表板统计
- **接口**: `GET /test/dashboard-stats`
- **描述**: 获取首页统计数据

### 15.4 获取最新动态
- **接口**: `GET /test/recent-activities`
- **描述**: 获取最新动态

## 16. 数据初始化模块 (Data Init)

### 16.1 初始化分类数据
- **接口**: `POST /data-init/categories`
- **描述**: 初始化商品分类数据

### 16.2 初始化商品数据
- **接口**: `POST /data-init/products`
- **描述**: 初始化商品数据

### 16.3 一键初始化所有数据
- **接口**: `POST /data-init/all`
- **描述**: 一键初始化所有测试数据

### 16.4 清空所有数据
- **接口**: `DELETE /data-init/clear`
- **描述**: 清空所有数据

### 16.5 获取数据统计
- **接口**: `GET /data-init/stats`
- **描述**: 获取数据统计信息

## 17. 根路径模块 (Root)

### 17.1 根路径
- **接口**: `GET /`
- **描述**: 系统根路径，返回基本信息

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/登录失效 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 认证说明

大部分接口需要在请求头中携带JWT Token：

```
Authorization: Bearer {token}
```

## 数据类型说明

### 用户角色 (Role)
- `ADMIN`: 系统管理员
- `MANAGER`: 店长
- `CASHIER`: 收银员
- `WAREHOUSE`: 库管员

### 用户状态 (Status)
- `ACTIVE`: 激活
- `INACTIVE`: 停用

### 订单状态 (Order Status)
- `PENDING`: 待处理
- `COMPLETED`: 已完成
- `CANCELLED`: 已取消
- `REFUNDED`: 已退款

### 支付方式 (Payment Method)
- `CASH`: 现金
- `WECHAT`: 微信支付
- `ALIPAY`: 支付宝
- `CARD`: 银行卡

### 商品状态 (Product Status)
- `ACTIVE`: 正常
- `INACTIVE`: 停用

### 供应商状态 (Supplier Status)
- `ACTIVE`: 正常
- `INACTIVE`: 停用
- `BLACKLIST`: 黑名单

## 注意事项

1. 所有时间格式统一使用：`yyyy-MM-dd HH:mm:ss`
2. 金额字段使用BigDecimal类型，保留2位小数
3. 分页参数：页码从1开始（前端），后端转换为从0开始
4. 所有接口都支持跨域访问
5. 请求和响应的Content-Type都是`application/json`
6. 文件上传接口使用`multipart/form-data`格式

## 更新日志

- v1.0.0 (2025-08-13): 初始版本，包含所有基础功能模块