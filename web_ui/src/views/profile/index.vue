<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 个人信息卡片 -->
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
            </div>
          </template>
          
          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名">
                  <el-input v-model="profileForm.username" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="角色">
                  <el-input v-model="profileForm.roleName" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态">
                  <el-tag :type="profileForm.status === 1 ? 'success' : 'danger'">
                    {{ profileForm.status === 1 ? '正常' : '禁用' }}
                  </el-tag>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="创建时间">
              <el-input v-model="profileForm.createdAt" disabled />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="updateProfile" :loading="updateLoading">
                更新信息
              </el-button>
              <el-button @click="showChangePasswordDialog">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <!-- 头像和统计信息 -->
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>用户头像</span>
            </div>
          </template>
          
          <div class="avatar-section">
            <div class="avatar-container">
              <el-avatar :size="120" :src="profileForm.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
            </div>
            <div class="avatar-actions">
              <el-button type="primary" size="small" @click="uploadAvatar">
                上传头像
              </el-button>
            </div>
          </div>
        </el-card>
        
        <el-card shadow="never" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>账户统计</span>
            </div>
          </template>
          
          <div class="stats-section">
            <div class="stat-item">
              <div class="stat-label">最后登录</div>
              <div class="stat-value">{{ profileForm.lastLoginTime || '暂无记录' }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">登录次数</div>
              <div class="stat-value">{{ profileForm.loginCount || 0 }} 次</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">账户创建</div>
              <div class="stat-value">{{ profileForm.createdAt }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 修改密码对话框 -->
    <el-dialog
      title="修改密码"
      v-model="passwordDialogVisible"
      width="400px"
      @close="resetPasswordForm"
    >
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="changePassword" :loading="passwordLoading">
            确认修改
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getUserProfile, updateUserProfile, changeUserPassword } from '@/api/user'

const userStore = useUserStore()

// 表单引用
const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

// 个人信息表单
const profileForm = reactive({
  id: '',
  username: '',
  realName: '',
  email: '',
  phone: '',
  roleName: '',
  status: 1,
  avatar: '',
  createdAt: '',
  lastLoginTime: '',
  loginCount: 0
})

// 修改密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const profileRules = reactive<FormRules>({
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
})

const passwordRules = reactive<FormRules>({
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 状态变量
const updateLoading = ref(false)
const passwordLoading = ref(false)
const passwordDialogVisible = ref(false)

// 加载用户信息
const loadUserProfile = async () => {
  try {
    const response = await getUserProfile()
    if (response.code === 200) {
      Object.assign(profileForm, response.data)
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 使用store中的用户信息作为备用
    const userInfo = userStore.userInfo
    Object.assign(profileForm, {
      id: userInfo.id || '',
      username: userInfo.username || '',
      realName: userInfo.realName || '',
      email: userInfo.email || '',
      phone: userInfo.phone || '',
      roleName: userInfo.roleName || '系统管理员',
      status: userInfo.status || 1,
      avatar: userInfo.avatar || '',
      createdAt: userInfo.createdAt || new Date().toLocaleString(),
      lastLoginTime: userInfo.lastLoginTime || '',
      loginCount: userInfo.loginCount || 0
    })
  }
}

// 更新个人信息
const updateProfile = async () => {
  if (!profileFormRef.value) return
  
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      updateLoading.value = true
      try {
        const data = {
          realName: profileForm.realName,
          email: profileForm.email,
          phone: profileForm.phone
        }
        
        const response = await updateUserProfile(data)
        if (response.code === 200) {
          ElMessage.success('个人信息更新成功')
          // 更新store中的用户信息
          userStore.updateUserInfo(data)
        } else {
          ElMessage.error(response.message || '更新失败')
        }
      } catch (error) {
        console.error('更新个人信息失败:', error)
        ElMessage.error('更新失败，请重试')
      } finally {
        updateLoading.value = false
      }
    }
  })
}

// 显示修改密码对话框
const showChangePasswordDialog = () => {
  resetPasswordForm()
  passwordDialogVisible.value = true
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true
      try {
        const data = {
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        }
        
        const response = await changeUserPassword(data)
        if (response.code === 200) {
          ElMessage.success('密码修改成功，请重新登录')
          passwordDialogVisible.value = false
          resetPasswordForm()
          // 可以选择自动退出登录
          setTimeout(() => {
            userStore.logout()
          }, 2000)
        } else {
          ElMessage.error(response.message || '密码修改失败')
        }
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error('修改密码失败，请重试')
      } finally {
        passwordLoading.value = false
      }
    }
  })
}

// 重置密码表单
const resetPasswordForm = () => {
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
}

// 上传头像
const uploadAvatar = () => {
  ElMessage.info('头像上传功能开发中...')
}

// 页面加载时获取用户信息
onMounted(() => {
  loadUserProfile()
})



// 页面加载时获取用户信息
onMounted(() => {
  // TODO: 从后端获取用户信息
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-content {
  max-width: 600px;
}

.avatar-section {
  text-align: center;
  padding: 20px 0;
}

.avatar-container {
  margin-bottom: 20px;
}

.avatar-actions {
  display: flex;
  justify-content: center;
}

.stats-section {
  padding: 10px 0;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.stat-value {
  color: #303133;
  font-weight: 500;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
