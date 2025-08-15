<template>
  <div class="error-container">
    <div class="error-content">
      <div class="error-image">
        <el-icon :size="120" color="#409EFF">
          <Warning />
        </el-icon>
      </div>
      
      <div class="error-info">
        <h1 class="error-code">404</h1>
        <h2 class="error-title">页面未找到</h2>
        <p class="error-description">
          抱歉，您访问的页面不存在或已被删除。
        </p>
        
        <div class="error-actions">
          <el-button type="primary" @click="goHome">
            <el-icon><HomeFilled /></el-icon>
            返回首页
          </el-button>
          <el-button @click="goBack">
            <el-icon><Back /></el-icon>
            返回上页
          </el-button>
        </div>
      </div>
    </div>
    
    <div class="error-suggestions">
      <h3>您可以尝试：</h3>
      <ul>
        <li>检查网址是否正确</li>
        <li>返回上一页面</li>
        <li>访问首页重新导航</li>
        <li>联系系统管理员</li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Warning, HomeFilled, Back } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/dashboard/home')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.error-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.error-content {
  text-align: center;
  background: white;
  border-radius: 12px;
  padding: 60px 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
  margin-bottom: 30px;
}

.error-image {
  margin-bottom: 30px;
}

.error-code {
  font-size: 72px;
  font-weight: bold;
  color: #409EFF;
  margin: 0 0 20px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.error-title {
  font-size: 28px;
  color: #303133;
  margin: 0 0 15px 0;
  font-weight: 600;
}

.error-description {
  font-size: 16px;
  color: #606266;
  margin: 0 0 30px 0;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.error-suggestions {
  background: white;
  border-radius: 8px;
  padding: 25px 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  max-width: 400px;
  width: 100%;
}

.error-suggestions h3 {
  color: #303133;
  margin: 0 0 15px 0;
  font-size: 18px;
}

.error-suggestions ul {
  margin: 0;
  padding-left: 20px;
  color: #606266;
}

.error-suggestions li {
  margin-bottom: 8px;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .error-content {
    padding: 40px 20px;
  }
  
  .error-code {
    font-size: 60px;
  }
  
  .error-title {
    font-size: 24px;
  }
  
  .error-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .error-actions .el-button {
    width: 200px;
  }
}
</style>