<template>
  <div class="supplier-contract">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>合同管理</span>
          <el-button type="primary" size="small" @click="handleAddContract">
            <el-icon><Plus /></el-icon>
            新增合同
          </el-button>
        </div>
      </template>
      
      <el-table :data="contractList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="contractNo" label="合同编号" width="120" />
        <el-table-column prop="supplierName" label="供应商名称" width="150" />
        <el-table-column prop="contractName" label="合同名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="contractType" label="合同类型" width="100">
          <template #default="scope">
            <el-tag :type="getContractTypeTag(scope.row.contractType)">
              {{ scope.row.contractType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="100" />
        <el-table-column prop="endDate" label="结束日期" width="100" />
        <el-table-column prop="amount" label="合同金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.amount.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleViewContract(scope.row)">查看</el-button>
            <el-button link type="primary" @click="handleEditContract(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="handleDeleteContract(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 合同表单对话框 -->
    <el-dialog
      :model-value="dialogVisible"
      @update:model-value="dialogVisible = $event"
      :title="isEdit ? '编辑合同' : '新增合同'"
      width="700px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="contractForm"
        :rules="contractRules"
        label-width="100px"
        label-position="right"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同编号" prop="contractNo">
              <el-input v-model="contractForm.contractNo" placeholder="请输入合同编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplierId">
              <el-select v-model="contractForm.supplierId" placeholder="请选择供应商" style="width: 100%">
                <el-option
                  v-for="supplier in supplierOptions"
                  :key="supplier.id"
                  :label="supplier.name"
                  :value="supplier.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="合同名称" prop="contractName">
          <el-input v-model="contractForm.contractName" placeholder="请输入合同名称" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同类型" prop="contractType">
              <el-select v-model="contractForm.contractType" placeholder="请选择合同类型" style="width: 100%">
                <el-option label="采购合同" value="采购合同" />
                <el-option label="框架协议" value="框架协议" />
                <el-option label="服务合同" value="服务合同" />
                <el-option label="代理合同" value="代理合同" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同金额" prop="amount">
              <el-input-number v-model="contractForm.amount" :precision="2" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="contractForm.startDate"
                type="date"
                placeholder="选择开始日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="contractForm.endDate"
                type="date"
                placeholder="选择结束日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="合同状态" prop="status">
          <el-select v-model="contractForm.status" placeholder="请选择合同状态" style="width: 100%">
            <el-option label="执行中" value="执行中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已终止" value="已终止" />
            <el-option label="待签署" value="待签署" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="合同附件">
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :limit="5"
            :file-list="fileList"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持 PDF、Word、Excel 等格式文件，单个文件不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input
            v-model="contractForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmitContract">
            {{ isEdit ? '更新' : '保存' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

interface Props {
  supplierId?: string
}

const props = withDefaults(defineProps<Props>(), {
  supplierId: undefined
})

// 合同列表
const contractList = ref([
  {
    id: '1',
    contractNo: 'HT20250001',
    supplierName: '新鲜果蔬供应商',
    supplierId: 'SUP001',
    contractName: '2025年度水果采购合同',
    contractType: '采购合同',
    startDate: '2025-01-01',
    endDate: '2025-12-31',
    amount: 120000.00,
    status: '执行中',
    remark: '按季度结算'
  },
  {
    id: '2',
    contractNo: 'HT20250002',
    supplierName: '优质肉类供应商',
    supplierId: 'SUP002',
    contractName: '肉类供应框架协议',
    contractType: '框架协议',
    startDate: '2025-01-15',
    endDate: '2026-01-14',
    amount: 350000.00,
    status: '执行中',
    remark: '月结30天'
  },
  {
    id: '3',
    contractNo: 'HT20250003',
    supplierName: '粮油调味供应商',
    supplierId: 'SUP003',
    contractName: '调味品采购合同',
    contractType: '采购合同',
    startDate: '2025-02-01',
    endDate: '2025-07-31',
    amount: 85000.00,
    status: '执行中',
    remark: '按月结算'
  }
])

// 供应商选项
const supplierOptions = ref([
  { id: 'SUP001', name: '新鲜果蔬供应商' },
  { id: 'SUP002', name: '优质肉类供应商' },
  { id: 'SUP003', name: '粮油调味供应商' },
  { id: 'SUP004', name: '饮料酒水供应商' },
  { id: 'SUP005', name: '日用百货供应商' }
])

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 3
})

// 加载状态
const loading = ref(false)
const submitLoading = ref(false)

// 对话框状态
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const fileList = ref([])

// 合同表单
const contractForm = reactive({
  id: '',
  contractNo: '',
  supplierId: '',
  contractName: '',
  contractType: '',
  startDate: '',
  endDate: '',
  amount: 0,
  status: '待签署',
  remark: ''
})

// 表单验证规则
const contractRules: FormRules = {
  contractNo: [
    { required: true, message: '请输入合同编号', trigger: 'blur' }
  ],
  supplierId: [
    { required: true, message: '请选择供应商', trigger: 'change' }
  ],
  contractName: [
    { required: true, message: '请输入合同名称', trigger: 'blur' }
  ],
  contractType: [
    { required: true, message: '请选择合同类型', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入合同金额', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择合同状态', trigger: 'change' }
  ]
}

// 获取合同类型对应的标签类型
const getContractTypeTag = (type: string) => {
  switch (type) {
    case '采购合同':
      return 'primary'
    case '框架协议':
      return 'success'
    case '服务合同':
      return 'warning'
    case '代理合同':
      return 'info'
    default:
      return 'info'
  }
}

// 获取合同状态对应的标签类型
const getStatusTag = (status: string) => {
  switch (status) {
    case '执行中':
      return 'success'
    case '已完成':
      return 'info'
    case '已终止':
      return 'danger'
    case '待签署':
      return 'warning'
    default:
      return 'info'
  }
}

// 加载合同列表
const loadContractList = () => {
  loading.value = true
  
  // 模拟API调用
  setTimeout(() => {
    // 如果指定了供应商ID，则过滤合同列表
    if (props.supplierId) {
      contractList.value = contractList.value.filter(item => item.supplierId === props.supplierId)
    }
    
    pagination.total = contractList.value.length
    loading.value = false
  }, 500)
}

// 新增合同
const handleAddContract = () => {
  isEdit.value = false
  resetContractForm()
  
  // 如果指定了供应商ID，则自动选择该供应商
  if (props.supplierId) {
    contractForm.supplierId = props.supplierId
  }
  
  // 生成合同编号
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0')
  contractForm.contractNo = `HT${year}${month}${day}${random}`
  
  dialogVisible.value = true
}

// 查看合同
const handleViewContract = (row: any) => {
  ElMessage.info(`查看合同: ${row.contractName}`)
}

// 编辑合同
const handleEditContract = (row: any) => {
  isEdit.value = true
  resetContractForm()
  Object.assign(contractForm, row)
  dialogVisible.value = true
}

// 删除合同
const handleDeleteContract = (row: any) => {
  ElMessageBox.confirm(`确认删除合同 "${row.contractName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟删除操作
    const index = contractList.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      contractList.value.splice(index, 1)
      pagination.total = contractList.value.length
      ElMessage.success('删除成功')
    }
  }).catch(() => {})
}

// 提交合同表单
const handleSubmitContract = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    // 获取供应商名称
    const supplier = supplierOptions.value.find(item => item.id === contractForm.supplierId)
    const supplierName = supplier ? supplier.name : ''
    
    // 模拟API调用
    setTimeout(() => {
      if (isEdit.value) {
        // 编辑合同
        const index = contractList.value.findIndex(item => item.id === contractForm.id)
        if (index !== -1) {
          contractList.value[index] = {
            ...contractForm,
            supplierName
          }
        }
        ElMessage.success('合同更新成功')
      } else {
        // 新增合同
        const newContract = {
          ...contractForm,
          id: Math.random().toString(36).substring(2, 10),
          supplierName
        }
        contractList.value.push(newContract)
        pagination.total = contractList.value.length
        ElMessage.success('合同创建成功')
      }
      
      submitLoading.value = false
      dialogVisible.value = false
    }, 500)
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 重置合同表单
const resetContractForm = () => {
  Object.assign(contractForm, {
    id: '',
    contractNo: '',
    supplierId: '',
    contractName: '',
    contractType: '',
    startDate: '',
    endDate: '',
    amount: 0,
    status: '待签署',
    remark: ''
  })
  fileList.value = []
  formRef.value?.clearValidate()
}

// 关闭对话框
const handleDialogClose = () => {
  dialogVisible.value = false
}

// 分页处理
const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  loadContractList()
}

const handleCurrentChange = (val: number) => {
  pagination.currentPage = val
  loadContractList()
}

onMounted(() => {
  loadContractList()
})
</script>

<style scoped>
.supplier-contract {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-section {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}
</style>