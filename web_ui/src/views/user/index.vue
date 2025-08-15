<template>
  <div class="user-container">
    <el-card shadow="never" class="main-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">用户管理</span>
          <div class="header-buttons">
            <el-button type="success" @click="exportUserData">
              <el-icon><Download /></el-icon>
              导出用户
            </el-button>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增用户
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="searchForm.role" placeholder="请选择角色" clearable>
              <el-option label="全部" value="" />
              <el-option label="管理员" value="admin" />
              <el-option label="收银员" value="cashier" />
              <el-option label="库管员" value="warehouse" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="正常" value="active" />
              <el-option label="禁用" value="disabled" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 用户表格 -->
      <el-table :data="userList" border style="width: 100%">
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="getRoleTagType(scope.row.role)">
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'active' ? 'success' : 'danger'">
              {{ scope.row.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="150" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="warning" @click="handleResetPassword(scope.row)">密码</el-button>
            <el-button link :type="scope.row.status === 'active' ? 'danger' : 'success'" @click="handleToggleStatus(scope.row)">
              {{ scope.row.status === 'active' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      

    </el-card>
    
    <!-- 用户编辑/新增对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userFormRules"
        label-width="100px"
        label-position="right"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="userForm.username" 
            placeholder="请输入用户名" 
            :disabled="dialogType === 'edit'"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input 
            v-model="userForm.realName" 
            placeholder="请输入真实姓名"
            prefix-icon="UserFilled"
          />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="收银员" value="cashier" />
            <el-option label="库管员" value="warehouse" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="userForm.phone" 
            placeholder="请输入手机号"
            prefix-icon="Phone"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input 
            v-model="userForm.email" 
            placeholder="请输入邮箱"
            prefix-icon="Message"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input 
            v-model="userForm.password" 
            placeholder="请输入密码" 
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="dialogType === 'add'">
          <el-input 
            v-model="userForm.confirmPassword" 
            placeholder="请再次输入密码" 
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="userForm.statusActive"
            active-text="正常"
            inactive-text="禁用"
            :active-value="true"
            :inactive-value="false"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUserForm">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Download
} from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  username: '',
  realName: '',
  role: '',
  status: ''
})



// 用户列表
const userList = ref([
  {
    id: '1',
    username: 'admin',
    realName: '系统管理员',
    role: 'admin',
    phone: '13800138000',
    email: 'admin@supermarket.com',
    lastLoginTime: '2025-01-22 14:30:25',
    status: 'active',
    createTime: '2024-01-01 09:00:00'
  },
  {
    id: '2',
    username: 'cashier01',
    realName: '张三',
    role: 'cashier',
    phone: '13800138001',
    email: 'zhangsan@supermarket.com',
    lastLoginTime: '2025-01-22 14:25:18',
    status: 'active',
    createTime: '2024-02-15 10:30:00'
  },
  {
    id: '3',
    username: 'cashier02',
    realName: '李四',
    role: 'cashier',
    phone: '13800138002',
    email: 'lisi@supermarket.com',
    lastLoginTime: '2025-01-22 14:20:45',
    status: 'active',
    createTime: '2024-03-10 11:15:00'
  },
  {
    id: '4',
    username: 'warehouse01',
    realName: '王五',
    role: 'warehouse',
    phone: '13800138003',
    email: 'wangwu@supermarket.com',
    lastLoginTime: '2025-01-22 09:15:30',
    status: 'active',
    createTime: '2024-04-20 14:45:00'
  },
  {
    id: '5',
    username: 'cashier03',
    realName: '赵六',
    role: 'cashier',
    phone: '13800138004',
    email: 'zhaoliu@supermarket.com',
    lastLoginTime: '2025-01-21 18:30:00',
    status: 'disabled',
    createTime: '2024-05-08 16:20:00'
  }
])

// 获取角色文本
const getRoleText = (role: string) => {
  const roleMap: Record<string, string> = {
    admin: '管理员',
    cashier: '收银员',
    warehouse: '库管员'
  }
  return roleMap[role] || '未知'
}

// 获取角色标签类型
const getRoleTagType = (role: string) => {
  const typeMap: Record<string, string> = {
    admin: 'danger',
    cashier: 'primary',
    warehouse: 'warning'
  }
  return typeMap[role] || 'info'
}

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const userFormRef = ref()

// 用户表单
const userForm = reactive({
  id: '',
  username: '',
  realName: '',
  role: 'cashier',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
  statusActive: true
})

// 表单验证规则
const validatePass = (_rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (userForm.confirmPassword !== '') {
      userFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (_rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== userForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const userFormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ]
}

// 重置表单
const resetUserForm = () => {
  userForm.id = ''
  userForm.username = ''
  userForm.realName = ''
  userForm.role = 'cashier'
  userForm.phone = ''
  userForm.email = ''
  userForm.password = ''
  userForm.confirmPassword = ''
  userForm.statusActive = true
  
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
}

// 方法
const handleSearch = () => {
  console.log('搜索用户', searchForm)
  // 实际项目中这里应该调用API获取数据
  // 这里仅做模拟
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.realName = ''
  searchForm.role = ''
  searchForm.status = ''
  handleSearch()
}

const handleAdd = () => {
  dialogType.value = 'add'
  resetUserForm()
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  resetUserForm()
  
  // 填充表单数据
  userForm.id = row.id
  userForm.username = row.username
  userForm.realName = row.realName
  userForm.role = row.role
  userForm.phone = row.phone
  userForm.email = row.email
  userForm.statusActive = row.status === 'active'
  
  dialogVisible.value = true
}

// 提交用户表单
const submitUserForm = () => {
  if (!userFormRef.value) return
  
  userFormRef.value.validate((valid: boolean) => {
    if (valid) {
      if (dialogType.value === 'add') {
        // 模拟添加用户
        const newUser = {
          id: String(userList.value.length + 1),
          username: userForm.username,
          realName: userForm.realName,
          role: userForm.role,
          phone: userForm.phone,
          email: userForm.email,
          lastLoginTime: '-',
          status: userForm.statusActive ? 'active' : 'disabled',
          createTime: new Date().toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
          }).replace(/\//g, '-')
        }
        
        userList.value.unshift(newUser)
        ElMessage.success('用户添加成功')
      } else {
        // 模拟编辑用户
        const index = userList.value.findIndex(item => item.id === userForm.id)
        if (index !== -1) {
          userList.value[index].realName = userForm.realName
          userList.value[index].role = userForm.role
          userList.value[index].phone = userForm.phone
          userList.value[index].email = userForm.email
          userList.value[index].status = userForm.statusActive ? 'active' : 'disabled'
          ElMessage.success('用户信息更新成功')
        }
      }
      
      dialogVisible.value = false
    } else {
      return false
    }
  })
}

const handleResetPassword = (row: any) => {
  ElMessageBox.confirm(`确认重置用户 "${row.realName}" 的密码吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('密码重置成功，新密码为：123456')
  }).catch(() => {})
}

const handleToggleStatus = (row: any) => {
  const action = row.status === 'active' ? '禁用' : '启用'
  ElMessageBox.confirm(`确认${action}用户 "${row.realName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    row.status = row.status === 'active' ? 'disabled' : 'active'
    ElMessage.success(`${action}成功`)
  }).catch(() => {})
}



// 导出用户数据
const exportUserData = () => {
  try {
    // 准备导出数据
    const exportData = userList.value.map(user => ({
      用户ID: user.id,
      用户名: user.username,
      真实姓名: user.realName,
      角色: getRoleText(user.role),
      手机号: user.phone,
      邮箱: user.email,
      最后登录: user.lastLoginTime,
      状态: user.status === 'active' ? '正常' : '禁用',
      创建时间: user.createTime
    }))
    
    // 创建CSV内容
    const headers = ['用户ID', '用户名', '真实姓名', '角色', '手机号', '邮箱', '最后登录', '状态', '创建时间']
    const csvContent = [
      headers.join(','),
      ...exportData.map(row => Object.values(row).join(','))
    ].join('\n')
    
    // 创建下载链接
    const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    
    // 设置下载属性
    const date = new Date().toISOString().split('T')[0]
    link.setAttribute('href', url)
    link.setAttribute('download', `用户数据_${date}.csv`)
    link.style.visibility = 'hidden'
    
    // 添加到页面并触发下载
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('用户数据导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请重试')
  }
}

// 辅助函数


onMounted(() => {
  // 页面初始化
})
</script>

<style scoped>
.user-container {
  padding: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}



.operation-buttons {
  display: flex;
  gap: 5px;
  justify-content: space-between;
  flex-wrap: nowrap;
  width: 100%;
}

.operation-buttons .el-button {
  padding: 5px 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
  min-width: 80px;
  font-size: 12px;
  flex: 1;
}

.operation-buttons .el-button:nth-child(1) {
  flex: 0.8; /* 编辑按钮占用较少空间 */
}

.operation-buttons .el-button:nth-child(2) {
  flex: 1.3; /* 给重置密码按钮更多空间 */
}

.operation-buttons .el-button:nth-child(3) {
  flex: 1; /* 禁用/启用按钮适当空间 */
}

.status-button {
  min-width: 85px !important; /* 确保禁用按钮有足够宽度 */
  padding: 5px 10px !important; /* 增加内边距 */
}

.operation-buttons .el-icon {
  margin-right: 3px;
  font-size: 14px;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.main-card {
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1) !important;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 10px;
}
</style>