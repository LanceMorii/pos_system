<template>
  <div class="category-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>供应商分类管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增分类
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="分类编码">
            <el-input v-model="searchForm.categoryCode" placeholder="请输入分类编码" clearable />
          </el-form-item>
          <el-form-item label="分类名称">
            <el-input v-model="searchForm.categoryName" placeholder="请输入分类名称" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="启用" value="ACTIVE" />
              <el-option label="停用" value="INACTIVE" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 分类表格 -->
      <el-table :data="categoryList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="categoryCode" label="分类编码" width="120" />
        <el-table-column prop="categoryName" label="分类名称" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag 
              :type="scope.row.status === 'ACTIVE' ? 'success' : 'warning'"
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
              <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
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
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      

    </el-card>
    
    <!-- 新增/编辑分类对话框 -->
    <el-dialog
      :title="editMode === 'add' ? '新增分类' : '编辑分类'"
      v-model="editDialogVisible"
      width="600px"
      @close="resetEditForm"
    >
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="分类编码" prop="categoryCode">
          <el-input v-model="editForm.categoryCode" placeholder="留空自动生成" />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="editForm.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="editForm.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="editForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="启用" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input 
            v-model="editForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入分类描述"
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ArrowDown } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  getCategoryList, 
  getCategoryById, 
  createCategory, 
  updateCategory, 
  deleteCategory,
  updateCategoryStatus
} from '@/api/supplierCategory'

// 搜索表单
const searchForm = reactive({
  categoryCode: '',
  categoryName: '',
  status: ''
})



// 对话框状态
const editDialogVisible = ref(false)
const editMode = ref<'add' | 'edit'>('add')

// 表单引用
const editFormRef = ref<FormInstance>()

// 编辑表单
const editForm = reactive({
  id: null,
  categoryCode: '',
  categoryName: '',
  description: '',
  sortOrder: 1,
  status: 'ACTIVE'
})

// 表单验证规则
const editRules = reactive<FormRules>({
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  sortOrder: [{ required: true, message: '请输入排序值', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

// 分类列表
const categoryList = ref([])
const loading = ref(false)

// 加载分类列表
const loadCategoryList = async () => {
  loading.value = true
  try {
    const params = {
      categoryCode: searchForm.categoryCode || undefined,
      categoryName: searchForm.categoryName || undefined,
      status: searchForm.status || undefined
    }
    
    const response = await getCategoryList(params)
    if (response.code === 200) {
      categoryList.value = response.data.items || response.data || []
    } else {
      ElMessage.error(response.message || '获取分类列表失败')
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  loadCategoryList()
}

// 重置搜索
const handleReset = () => {
  searchForm.categoryCode = ''
  searchForm.categoryName = ''
  searchForm.status = ''
  handleSearch()
}



// 新增分类
const handleAdd = () => {
  editMode.value = 'add'
  resetEditForm()
  editDialogVisible.value = true
}

// 编辑分类
const handleEdit = (row: any) => {
  editMode.value = 'edit'
  resetEditForm()
  
  // 填充表单数据
  Object.assign(editForm, {
    id: row.id,
    categoryCode: row.categoryCode,
    categoryName: row.categoryName,
    description: row.description,
    sortOrder: row.sortOrder,
    status: row.status
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
    categoryCode: '',
    categoryName: '',
    description: '',
    sortOrder: 1,
    status: 'ACTIVE'
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
          response = await createCategory(submitData)
        } else {
          response = await updateCategory(editForm.id, submitData)
        }
        
        if (response.code === 200) {
          ElMessage.success(editMode.value === 'add' ? '新增分类成功' : '更新分类成功')
          editDialogVisible.value = false
          loadCategoryList()
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

// 删除分类
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类 "${row.categoryName}" 吗？此操作不可撤销。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await deleteCategory(row.id)
    if (response.code === 200) {
      ElMessage.success('删除分类成功')
      loadCategoryList()
    } else {
      ElMessage.error(response.message || '删除分类失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除分类失败:', error)
      ElMessage.error('删除分类失败')
    }
  }
}

// 更改分类状态
const handleStatusChange = async (row: any, status: string) => {
  try {
    const statusText = status === 'ACTIVE' ? '启用' : '停用'
    
    await ElMessageBox.confirm(
      `确定要${statusText}分类 "${row.categoryName}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await updateCategoryStatus(row.id, status)
    if (response.code === 200) {
      ElMessage.success(`${statusText}成功`)
      loadCategoryList()
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

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 页面加载时获取数据
onMounted(() => {
  loadCategoryList()
})
</script>

<style scoped>
.category-container {
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
  gap: 8px;
  flex-wrap: wrap;
}
</style>