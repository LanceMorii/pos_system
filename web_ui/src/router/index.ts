import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'product',
        name: 'Product',
        component: () => import('@/views/product/index.vue'),
        meta: { title: '商品管理', icon: 'Goods' }
      },
      {
        path: 'inventory',
        name: 'Inventory',
        component: () => import('@/views/inventory/index.vue'),
        meta: { title: '库存管理', icon: 'List' }
      },

      {
        path: 'supplier',
        name: 'Supplier',
        component: () => import('@/views/supplier/index.vue'),
        meta: { title: '供应商管理', icon: 'Sell' }
      },
      {
        path: 'supplier-category',
        name: 'SupplierCategory',
        component: () => import('@/views/supplier/category.vue'),
        meta: { title: '供应商分类', icon: 'Collection' }
      },

      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'UserFilled' }
      },
      {
        path: 'cashier',
        name: 'Cashier',
        component: () => import('@/views/cashier/new-cashier.vue'),
        meta: { title: '收银台', icon: 'Money' }
      },
      {
        path: 'sales',
        name: 'Sales',
        component: () => import('@/views/sales/index.vue'),
        meta: { title: '销售管理', icon: 'TrendCharts' }
      },
      {
        path: 'member',
        name: 'Member',
        component: () => import('@/views/member/index.vue'),
        meta: { title: '会员管理', icon: 'Star' }
      },

      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/order/index.vue'),
        meta: { title: '订单管理', icon: 'Tickets' }
      },


      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人信息', icon: 'User', hidden: true }
      },
      {
        path: 'system-status',
        name: 'SystemStatus',
        component: () => import('@/views/system-status/index.vue'),
        meta: { title: '系统状态', icon: 'Monitor' }
      },
      {
        path: 'data-init-test',
        name: 'DataInitTest',
        component: () => import('@/views/test/data-init.vue'),
        meta: { title: '数据初始化测试', icon: 'Setting', hidden: true }
      },
      {
        path: 'connection-test',
        name: 'ConnectionTest',
        component: () => import('@/views/test/connection-test.vue'),
        meta: { title: '连接测试', icon: 'Link', hidden: true }
      },

    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  console.log(`路由跳转: 从 ${from.path} 到 ${to.path}`)
  
  // 注释掉自动设置token的代码，确保需要登录
  // if (process.env.NODE_ENV === 'development') {
  //   localStorage.setItem('token', 'dev-token')
  // }
  
  const token = localStorage.getItem('token')
  // 免登录白名单
  const publicPaths = ['/login', '/register', '/dashboard/cashier']

  if (publicPaths.includes(to.path)) {
    console.log('免登录页面，直接放行')
    next()
    return
  }

  if (token) {
    console.log('已登录，允许访问请求页面')
    next()
  } else {
    console.log('未登录，重定向到登录页')
    next('/login')
  }
})

// 路由后置钩子，用于调试
router.afterEach((to, from) => {
  console.log(`路由跳转完成: 从 ${from.path} 到 ${to.path}`)
})

export default router