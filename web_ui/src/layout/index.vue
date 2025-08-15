<template>
  <div class="app-container">
    <el-container>
      <el-aside width="220px">
        <div class="logo">
          <h2>智慧超市系统</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
          @select="handleMenuSelect"
        >
          <el-menu-item index="/dashboard/home">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/product">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/inventory">
            <el-icon><List /></el-icon>
            <span>库存管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/supplier">
            <el-icon><Sell /></el-icon>
            <span>供应商管理</span>
          </el-menu-item>

          <el-menu-item index="/dashboard/user">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/cashier">
            <el-icon><Money /></el-icon>
            <span>收银台</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/member">
            <el-icon><Star /></el-icon>
            <span>会员管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/orders">
            <el-icon><Tickets /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/system-status">
            <el-icon><Monitor /></el-icon>
            <span>系统状态</span>
          </el-menu-item>


        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-left">
            <el-button type="primary" @click="navigateToCashier">直接跳转到收银台</el-button>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                {{ userInfo.realName || userInfo.username }}
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getUserInfo } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, Goods, List, ShoppingCart, Sell, DataLine, UserFilled, Money, Star, ArrowDown, Monitor, Tickets } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => {
  return route.path
})

const userInfo = computed(() => {
  return userStore.userInfo
})

onMounted(async () => {
  if (!userStore.userInfo.id) {
    try {
      const res = await getUserInfo()
      userStore.setUserInfo(res.data)
    } catch (error) {
      console.error('获取用户信息失败', error)
    }
  }
})

const navigateToCashier = () => {
  router.push('/dashboard/cashier')
  ElMessage({
    type: 'success',
    message: '正在跳转到收银台...'
  })
}

const handleMenuSelect = (index: string) => {
  console.log('菜单选择:', index)
  // 确保路由能够正常跳转
  if (route.path !== index) {
    router.push(index).catch(err => {
      console.error('路由跳转失败:', err)
    })
  }
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确认退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
      ElMessage({
        type: 'success',
        message: '退出登录成功'
      })
    }).catch(() => {})
  } else if (command === 'profile') {
    // 跳转到个人信息页面
    // router.push('/dashboard/profile')
    ElMessage({
      type: 'info',
      message: '功能开发中...'
    })
  }
}
</script>

<style scoped>
.app-container {
  height: 100%;
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  border-bottom: 1px solid #1f2d3d;
}

.el-menu-vertical {
  border-right: none;
}

.el-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  margin-left: 20px;
}

.header-right {
  margin-right: 20px;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  display: flex;
  align-items: center;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>