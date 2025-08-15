<template>
  <div class="product-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增商品
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="商品名称">
            <el-input v-model="searchForm.name" placeholder="请输入商品名称" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 商品表格 -->
      <el-table :data="productList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="商品ID" width="80" />
        <el-table-column prop="name" label="商品名称" min-width="150" />
        <el-table-column prop="barcode" label="条码" width="120" />
        <el-table-column prop="salePrice" label="售价" width="80">
          <template #default="scope">
            ¥{{ (scope.row.salePrice || 0).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="currentStock" label="库存" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'danger'">
              {{ scope.row.status === 'ACTIVE' ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      

    </el-card>
    
    <!-- 商品编辑/新增对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="productFormRef" :model="productForm" :rules="productRules" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        
        <el-form-item label="商品条码" prop="barcode">
          <el-input v-model="productForm.barcode" placeholder="请输入商品条码" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="售价" prop="salePrice">
              <el-input-number v-model="productForm.salePrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成本价" prop="purchasePrice">
              <el-input-number v-model="productForm.purchasePrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存数量" prop="currentStock">
              <el-input-number v-model="productForm.currentStock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品状态" prop="status">
              <el-select v-model="productForm.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="上架" value="ACTIVE" />
                <el-option label="下架" value="INACTIVE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="商品描述">
          <el-input v-model="productForm.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  getProductList, 
  createProduct, 
  updateProduct, 
  deleteProduct
} from '@/api/product'

// 搜索表单
const searchForm = reactive({
  name: ''
})



// 商品列表
const productList = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('')

// 表单相关
const productFormRef = ref<FormInstance>()
const productForm = reactive({
  id: '',
  name: '',
  code: '',
  barcode: '',
  categoryId: 1,
  salePrice: 0,
  purchasePrice: 0,
  currentStock: 0,
  minStock: 10,
  maxStock: 1000,
  status: 'ACTIVE',
  description: ''
})

// 表单验证规则
const productRules = reactive<FormRules>({
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  barcode: [{ required: true, message: '请输入商品条码', trigger: 'blur' }],
  salePrice: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  purchasePrice: [{ required: true, message: '请输入成本价', trigger: 'blur' }],
  currentStock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
})

// 加载状态
const loading = ref(false)

// 重置表单
const resetForm = () => {
  if (productFormRef.value) {
    productFormRef.value.resetFields()
  }
  
  Object.assign(productForm, {
    id: '',
    name: '',
    code: '',
    barcode: '',
    categoryId: 1,
    salePrice: 0,
    purchasePrice: 0,
    currentStock: 0,
    minStock: 10,
    maxStock: 1000,
    status: 'ACTIVE',
    description: ''
  })
}

// 加载商品列表
const loadProductList = async () => {
  loading.value = true
  try {
    const params = {
      name: searchForm.name || undefined
    }
    
    const response = await getProductList(params)
    if (response.code === 200) {
      productList.value = response.data.items || response.data || []
    } else {
      ElMessage.error(response.message || '获取商品列表失败')
      productList.value = []
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败，请检查网络连接')
    productList.value = []
  } finally {
    loading.value = false
  }
}

// 新增商品
const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增商品'
  dialogVisible.value = true
}

// 编辑商品
const handleEdit = (row: any) => {
  resetForm()
  dialogTitle.value = '编辑商品'
  
  // 填充表单数据
  Object.assign(productForm, {
    id: row.id,
    name: row.name,
    code: row.code,
    barcode: row.barcode,
    categoryId: row.categoryId || 1,
    salePrice: row.salePrice || 0,
    purchasePrice: row.purchasePrice || 0,
    currentStock: row.currentStock || 0,
    minStock: row.minStock || 10,
    maxStock: row.maxStock || 1000,
    status: row.status || 'ACTIVE',
    description: row.description || ''
  })
  
  dialogVisible.value = true
}

// 删除商品
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除商品 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteProduct(row.id)
      ElMessage.success('删除成功')
      loadProductList()
    } catch (error) {
      console.error('删除商品失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }).catch(() => {})
}

// 提交表单
const submitForm = async () => {
  if (!productFormRef.value) return
  
  await productFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = {
          code: productForm.barcode,
          name: productForm.name,
          categoryId: productForm.categoryId,
          barcode: productForm.barcode,
          unit: '个',
          purchasePrice: productForm.purchasePrice,
          salePrice: productForm.salePrice,
          minStock: productForm.minStock,
          maxStock: productForm.maxStock,
          currentStock: productForm.currentStock,
          status: productForm.status,
          description: productForm.description
        }
        
        if (productForm.id) {
          await updateProduct(productForm.id, submitData)
          ElMessage.success('商品修改成功')
        } else {
          await createProduct(submitData)
          ElMessage.success('商品添加成功')
        }
        
        dialogVisible.value = false
        resetForm()
        loadProductList()
      } catch (error) {
        console.error('保存商品失败:', error)
        ElMessage.error('保存失败，请重试')
      }
    }
  })
}

// 搜索处理
const handleSearch = () => {
  loadProductList()
}

// 重置搜索
const handleReset = () => {
  searchForm.name = ''
  handleSearch()
}



onMounted(() => {
  loadProductList()
})
</script>

<style scoped>
.product-container {
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



.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>