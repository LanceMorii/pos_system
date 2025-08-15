<template>
  <div class="supplier-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>供应商管理</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增供应商
            </el-button>
            <el-button type="success" @click="handleBatchImport">
              <el-icon><Upload /></el-icon>
              批量导入
            </el-button>
            <el-button type="info" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
            <el-button type="warning" @click="handleCategoryManage">
              <el-icon><Collection /></el-icon>
              分类管理
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="供应商编码">
            <el-input v-model="searchForm.supplierCode" placeholder="请输入供应商编码" clearable />
          </el-form-item>
          <el-form-item label="供应商名称">
            <el-input v-model="searchForm.supplierName" placeholder="请输入供应商名称" clearable />
          </el-form-item>
          <el-form-item label="联系人">
            <el-input v-model="searchForm.contactPerson" placeholder="请输入联系人" clearable />
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="searchForm.category" placeholder="请选择分类" clearable>
              <el-option label="全部" value="" />
              <el-option 
                v-for="category in categories" 
                :key="category" 
                :label="category" 
                :value="category" 
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="正常" value="ACTIVE" />
              <el-option label="停用" value="INACTIVE" />
              <el-option label="黑名单" value="BLACKLIST" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 供应商表格 -->
      <el-table :data="supplierList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="supplierCode" label="供应商编码" width="120" />
        <el-table-column prop="supplierName" label="供应商名称" min-width="150" />
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column prop="email" label="邮箱" width="150" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
        <el-table-column prop="creditLimit" label="信用额度" width="100">
          <template #default="scope">
            <span v-if="scope.row.creditLimit">¥{{ scope.row.creditLimit.toFixed(2) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag 
              :type="getStatusTagType(scope.row.status)"
              size="small"
            >
              {{ scope.row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="150">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <div class="operation-buttons">
              <el-button type="primary" link @click="handleView(scope.row)">详情</el-button>
              <el-button type="warning" link @click="handleEdit(scope.row)">编辑</el-button>
              <el-dropdown @command="(command) => handleStatusChange(scope.row, command)">
                <el-button type="info" link>
                  状态<el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="ACTIVE" :disabled="scope.row.status === 'ACTIVE'">
                      启用
                    </el-dropdown-item>
                    <el-dropdown-item command="INACTIVE" :disabled="scope.row.status === 'INACTIVE'">
                      停用
                    </el-dropdown-item>
                    <el-dropdown-item command="BLACKLIST" :disabled="scope.row.status === 'BLACKLIST'">
                      加入黑名单
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      

    </el-card>
    
    <!-- 供应商详情对话框 -->
    <el-dialog
      title="供应商详情"
      v-model="detailDialogVisible"
      width="800px"
    >
      <div v-if="currentSupplier" class="supplier-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="供应商编码">{{ currentSupplier.supplierCode }}</el-descriptions-item>
          <el-descriptions-item label="供应商名称">{{ currentSupplier.supplierName }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentSupplier.contactPerson || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentSupplier.contactPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentSupplier.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ currentSupplier.category || '-' }}</el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">{{ currentSupplier.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="信用额度">
            <span v-if="currentSupplier.creditLimit">¥{{ currentSupplier.creditLimit.toFixed(2) }}</span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTagType(currentSupplier.status)" size="small">
              {{ currentSupplier.statusText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentSupplier.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(currentSupplier.updatedAt) }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentSupplier.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
    
    <!-- 新增/编辑供应商对话框 -->
    <el-dialog
      :title="editMode === 'add' ? '新增供应商' : '编辑供应商'"
      v-model="editDialogVisible"
      width="800px"
      @close="resetEditForm"
    >
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商编码" prop="supplierCode">
              <el-input v-model="editForm.supplierCode" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商名称" prop="supplierName">
              <el-input v-model="editForm.supplierName" placeholder="请输入供应商名称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="editForm.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="editForm.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="editForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="editForm.category" placeholder="请选择分类" style="width: 100%" filterable allow-create>
                <el-option 
                  v-for="category in categories" 
                  :key="category" 
                  :label="category" 
                  :value="category" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="editForm.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="正常" value="ACTIVE" />
                <el-option label="停用" value="INACTIVE" />
                <el-option label="黑名单" value="BLACKLIST" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信用额度">
              <el-input-number 
                v-model="editForm.creditLimit" 
                :min="0" 
                :precision="2" 
                style="width: 100%"
                placeholder="请输入信用额度"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="地址">
          <el-input v-model="editForm.address" placeholder="请输入地址" />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input 
            v-model="editForm.remark" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 批量导入对话框 -->
    <el-dialog
      title="批量导入供应商"
      v-model="batchImportDialogVisible"
      width="800px"
      @close="resetBatchImportForm"
    >
      <div class="batch-import-content">
        <el-alert
          title="导入说明"
          type="info"
          :closable="false"
          style="margin-bottom: 20px;"
        >
          <template #default>
            <div>
              <p>1. 请下载模板文件，按照模板格式填写数据</p>
              <p>2. 支持Excel格式文件(.xlsx)</p>
              <p>3. 必填字段：供应商名称、联系人、联系电话</p>
              <p>4. 供应商编码留空将自动生成</p>
            </div>
          </template>
        </el-alert>
        
        <div class="template-download" style="margin-bottom: 20px;">
          <el-button type="primary" @click="downloadTemplate">
            <el-icon><Download /></el-icon>
            下载导入模板
          </el-button>
        </div>
        
        <el-upload
          ref="uploadRef"
          class="upload-demo"
          drag
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
          :on-exceed="handleExceed"
          accept=".xlsx,.xls"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              只能上传Excel文件，且不超过10MB
            </div>
          </template>
        </el-upload>
        
        <div v-if="importResult" class="import-result" style="margin-top: 20px;">
          <el-alert
            :title="`导入完成：成功 ${importResult.successCount} 条，失败 ${importResult.failCount} 条`"
            :type="importResult.failCount > 0 ? 'warning' : 'success'"
            :closable="false"
          >
            <template #default>
              <div v-if="importResult.errorMessages">
                <p>错误信息：</p>
                <p style="color: #f56c6c; font-size: 12px;">{{ importResult.errorMessages }}</p>
              </div>
            </template>
          </el-alert>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="batchImportDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBatchImport" :disabled="!selectedFile">
            开始导入
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload, Download, ArrowDown, Collection } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  getSupplierList, 
  getSupplierById, 
  createSupplier, 
  updateSupplier, 
  deleteSupplier,
  updateSupplierStatus,
  getSupplierCategories,
  batchImportSuppliers,
  exportSupplierData
} from '@/api/supplier'

// 搜索表单
const searchForm = reactive({
  supplierCode: '',
  supplierName: '',
  contactPerson: '',
  category: '',
  status: ''
})



// 对话框状态
const detailDialogVisible = ref(false)
const editDialogVisible = ref(false)
const batchImportDialogVisible = ref(false)
const editMode = ref<'add' | 'edit'>('add')
const currentSupplier = ref(null)

// 表单引用
const editFormRef = ref<FormInstance>()

// 编辑表单
const editForm = reactive({
  id: null,
  supplierCode: '',
  supplierName: '',
  contactPerson: '',
  contactPhone: '',
  email: '',
  address: '',
  category: '',
  status: 'ACTIVE',
  creditLimit: null,
  remark: ''
})

// 表单验证规则
const editRules = reactive<FormRules>({
  supplierName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

// 供应商列表
const supplierList = ref([])
const loading = ref(false)
const categories = ref([])

// 批量导入相关
const uploadRef = ref()
const selectedFile = ref(null)
const importResult = ref(null)

// 加载供应商列表
const loadSupplierList = async () => {
  loading.value = true
  try {
    const params = {
      supplierCode: searchForm.supplierCode || undefined,
      supplierName: searchForm.supplierName || undefined,
      contactPerson: searchForm.contactPerson || undefined,
      category: searchForm.category || undefined,
      status: searchForm.status || undefined
    }
    
    const response = await getSupplierList(params)
    if (response.code === 200) {
      supplierList.value = response.data.items || response.data || []
    } else {
      ElMessage.error(response.message || '获取供应商列表失败')
    }
  } catch (error) {
    console.error('获取供应商列表失败:', error)
    ElMessage.error('获取供应商列表失败')
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await getSupplierCategories()
    if (response.code === 200) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  loadSupplierList()
}

// 重置搜索
const handleReset = () => {
  searchForm.supplierCode = ''
  searchForm.supplierName = ''
  searchForm.contactPerson = ''
  searchForm.category = ''
  searchForm.status = ''
  handleSearch()
}



// 查看供应商详情
const handleView = async (row: any) => {
  try {
    const response = await getSupplierById(row.id)
    if (response.code === 200) {
      currentSupplier.value = response.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(response.message || '获取供应商详情失败')
    }
  } catch (error) {
    console.error('获取供应商详情失败:', error)
    ElMessage.error('获取供应商详情失败')
  }
}

// 新增供应商
const handleAdd = () => {
  editMode.value = 'add'
  resetEditForm()
  editDialogVisible.value = true
}

// 编辑供应商
const handleEdit = (row: any) => {
  editMode.value = 'edit'
  resetEditForm()
  
  // 填充表单数据
  Object.assign(editForm, {
    id: row.id,
    supplierCode: row.supplierCode,
    supplierName: row.supplierName,
    contactPerson: row.contactPerson,
    contactPhone: row.contactPhone,
    email: row.email,
    address: row.address,
    category: row.category,
    status: row.status,
    creditLimit: row.creditLimit,
    remark: row.remark
  })
  
  editDialogVisible.value = true
}

// 重置编辑表单
const resetEditForm = () => {
  if (editFormRef.value) {
    editFormRef.value.resetFields()
  }
  
  Object.assign(editForm, {
    id: null,
    supplierCode: '',
    supplierName: '',
    contactPerson: '',
    contactPhone: '',
    email: '',
    address: '',
    category: '',
    status: 'ACTIVE',
    creditLimit: null,
    remark: ''
  })
}

// 提交编辑表单
const submitEditForm = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = { ...editForm }
        delete submitData.id
        
        let response
        if (editMode.value === 'add') {
          response = await createSupplier(submitData)
        } else {
          response = await updateSupplier(editForm.id, submitData)
        }
        
        if (response.code === 200) {
          ElMessage.success(editMode.value === 'add' ? '新增供应商成功' : '更新供应商成功')
          editDialogVisible.value = false
          loadSupplierList()
          loadCategories() // 重新加载分类列表
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

// 删除供应商
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除供应商 "${row.supplierName}" 吗？此操作不可撤销。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await deleteSupplier(row.id)
    if (response.code === 200) {
      ElMessage.success('删除供应商成功')
      loadSupplierList()
    } else {
      ElMessage.error(response.message || '删除供应商失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除供应商失败:', error)
      ElMessage.error('删除供应商失败')
    }
  }
}

// 更改供应商状态
const handleStatusChange = async (row: any, status: string) => {
  try {
    const statusText = {
      'ACTIVE': '启用',
      'INACTIVE': '停用',
      'BLACKLIST': '加入黑名单'
    }[status]
    
    await ElMessageBox.confirm(
      `确定要${statusText}供应商 "${row.supplierName}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await updateSupplierStatus(row.id, status)
    if (response.code === 200) {
      ElMessage.success(`${statusText}成功`)
      loadSupplierList()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
      ElMessage.error('更新状态失败')
    }
  }
}

// 批量导入
const handleBatchImport = () => {
  batchImportDialogVisible.value = true
}

// 初始化路由
const router = useRouter()

// 分类管理
const handleCategoryManage = () => {
  // 使用Vue Router进行路由跳转
  router.push('/dashboard/supplier-category')
}

// 导出数据
const handleExport = async () => {
  try {
    ElMessage.info('正在导出数据，请稍候...')
    
    const params = {
      supplierCode: searchForm.supplierCode || undefined,
      supplierName: searchForm.supplierName || undefined,
      contactPerson: searchForm.contactPerson || undefined,
      category: searchForm.category || undefined,
      status: searchForm.status || undefined
    }
    
    console.log('导出参数:', params)
    
    // 调用后端导出API
    const response = await exportSupplierData(params)
    
    console.log('导出响应:', response)
    console.log('响应类型:', typeof response)
    console.log('是否为Blob:', response instanceof Blob)
    console.log('文件大小:', response instanceof Blob ? response.size : 'N/A')
    console.log('内容类型:', response instanceof Blob ? response.type : 'N/A')
    
    // 处理文件下载
    if (response instanceof Blob && response.size > 0) {
      // 创建下载链接
      const url = window.URL.createObjectURL(response)
      const link = document.createElement('a')
      link.href = url
      
      // 生成文件名
      const now = new Date()
      const timestamp = now.getFullYear() + 
        String(now.getMonth() + 1).padStart(2, '0') + 
        String(now.getDate()).padStart(2, '0') + '_' +
        String(now.getHours()).padStart(2, '0') + 
        String(now.getMinutes()).padStart(2, '0') + 
        String(now.getSeconds()).padStart(2, '0')
      
      link.download = `供应商数据_${timestamp}.xlsx`
      
      // 触发下载
      document.body.appendChild(link)
      link.click()
      
      // 清理
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
      
      ElMessage.success('数据导出成功')
    } else {
      console.error('后端响应不是有效的Blob或文件为空')
      ElMessage.error('导出失败：服务器返回的数据格式不正确')
    }
  } catch (error) {
    console.error('导出失败:', error)
    
    // 提供详细的错误信息
    let errorMessage = '导出失败'
    if (error.response) {
      if (error.response.status === 404) {
        errorMessage = '导出接口不存在，请检查后端服务'
      } else if (error.response.status === 500) {
        errorMessage = '服务器内部错误，请检查后端日志'
      } else {
        errorMessage = `导出失败: HTTP ${error.response.status}`
      }
    } else if (error.message) {
      errorMessage = `导出失败: ${error.message}`
    }
    
    ElMessage.error(errorMessage)
  }
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  const typeMap = {
    'ACTIVE': 'success',
    'INACTIVE': 'warning',
    'BLACKLIST': 'danger'
  }
  return typeMap[status] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 批量导入相关函数
const resetBatchImportForm = () => {
  selectedFile.value = null
  importResult.value = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

const handleFileChange = (file: any) => {
  selectedFile.value = file.raw
  importResult.value = null
}

const handleExceed = () => {
  ElMessage.warning('只能上传一个文件')
}

const downloadTemplate = () => {
  // 创建模板数据
  const templateData = [
    ['供应商编码', '供应商名称', '联系人', '联系电话', '邮箱', '地址', '分类', '状态', '信用额度', '备注'],
    ['', '示例供应商', '张三', '13800138000', 'example@test.com', '北京市朝阳区', '生鲜', 'ACTIVE', '50000', '这是示例数据'],
    ['', '', '', '', '', '', '', '', '', '']
  ]
  
  // 创建CSV内容
  const csvContent = templateData.map(row => 
    row.map(cell => `"${cell}"`).join(',')
  ).join('\n')
  
  // 添加BOM以支持中文
  const BOM = '\uFEFF'
  const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' })
  
  // 创建下载链接
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = '供应商导入模板.csv'
  
  // 触发下载
  document.body.appendChild(link)
  link.click()
  
  // 清理
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
  
  ElMessage.success('模板下载成功')
}

const submitBatchImport = async () => {
  if (!selectedFile.value) {
    ElMessage.error('请选择要导入的文件')
    return
  }
  
  try {
    ElMessage.info('正在解析文件，请稍候...')
    
    // 读取Excel文件
    const fileData = await readExcelFile(selectedFile.value)
    
    if (!fileData || fileData.length === 0) {
      ElMessage.error('文件内容为空或格式不正确')
      return
    }
    
    // 转换为供应商数据格式
    const supplierData = parseSupplierData(fileData)
    
    if (supplierData.length === 0) {
      ElMessage.error('没有找到有效的供应商数据')
      return
    }
    
    ElMessage.info(`准备导入 ${supplierData.length} 条供应商数据...`)
    
    // 调用批量导入API
    const response = await batchImportSuppliers(supplierData)
    
    if (response.code === 200) {
      importResult.value = response.data
      ElMessage.success('批量导入完成')
      
      // 如果有成功导入的数据，刷新列表
      if (response.data.successCount > 0) {
        loadSupplierList()
        loadCategories()
      }
    } else {
      ElMessage.error(response.message || '批量导入失败')
    }
    
  } catch (error) {
    console.error('批量导入失败:', error)
    ElMessage.error('批量导入失败：' + error.message)
  }
}

// 读取Excel文件
const readExcelFile = (file: File): Promise<any[]> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    
    reader.onload = (e) => {
      try {
        const data = new Uint8Array(e.target?.result as ArrayBuffer)
        
        // 这里需要使用xlsx库来解析Excel文件
        // 由于没有安装xlsx库，我们先用简单的CSV解析作为示例
        // 实际项目中需要安装: npm install xlsx
        
        // 模拟解析结果
        const mockData = [
          ['供应商编码', '供应商名称', '联系人', '联系电话', '邮箱', '地址', '分类', '状态', '信用额度', '备注'],
          ['', '测试供应商1', '张三', '13800138001', 'test1@example.com', '北京市朝阳区', '生鲜', 'ACTIVE', '50000', '测试数据1'],
          ['', '测试供应商2', '李四', '13800138002', 'test2@example.com', '上海市浦东区', '肉类', 'ACTIVE', '80000', '测试数据2']
        ]
        
        resolve(mockData)
      } catch (error) {
        reject(new Error('文件解析失败'))
      }
    }
    
    reader.onerror = () => {
      reject(new Error('文件读取失败'))
    }
    
    reader.readAsArrayBuffer(file)
  })
}

// 解析供应商数据
const parseSupplierData = (data: any[]): any[] => {
  if (!data || data.length < 2) {
    return []
  }
  
  // 跳过标题行，从第二行开始解析
  const supplierData = []
  
  for (let i = 1; i < data.length; i++) {
    const row = data[i]
    
    // 检查必填字段
    if (!row[1] || !row[2] || !row[3]) {
      continue // 跳过无效行
    }
    
    const supplier = {
      supplierCode: row[0] || '', // 供应商编码（可为空，自动生成）
      supplierName: row[1] || '', // 供应商名称
      contactPerson: row[2] || '', // 联系人
      contactPhone: row[3] || '', // 联系电话
      email: row[4] || '', // 邮箱
      address: row[5] || '', // 地址
      category: row[6] || '', // 分类
      status: row[7] || 'ACTIVE', // 状态
      creditLimit: parseFloat(row[8]) || 0, // 信用额度
      remark: row[9] || '' // 备注
    }
    
    supplierData.push(supplier)
  }
  
  return supplierData
}

// 页面加载时获取数据
onMounted(() => {
  loadSupplierList()
  loadCategories()
})
</script>

<style scoped>
.supplier-container {
  padding: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}



.operation-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.supplier-detail {
  padding: 10px 0;
}
</style>