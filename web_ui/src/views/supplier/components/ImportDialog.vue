<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="val => emit('update:visible', val)"
    title="批量导入供应商"
    width="600px"
    :before-close="handleClose"
  >
    <div class="import-container">
      <el-upload
        ref="uploadRef"
        class="upload-demo"
        drag
        action="#"
        :auto-upload="false"
        :limit="1"
        :on-change="handleFileChange"
        :on-exceed="handleExceed"
        :file-list="fileList"
        accept=".xlsx,.xls,.csv"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            请上传Excel或CSV格式文件，文件大小不超过10MB
          </div>
        </template>
      </el-upload>
      
      <div class="template-download">
        <span>没有导入模板？</span>
        <el-button type="primary" link @click="downloadTemplate">
          下载导入模板
        </el-button>
      </div>
      
      <div class="import-tips">
        <h4>导入说明：</h4>
        <ol>
          <li>请严格按照模板格式填写数据，不要修改表头</li>
          <li>供应商名称、联系人、联系电话、邮箱、地址、主营类别为必填项</li>
          <li>状态栏填写"正常"或"停用"，默认为"正常"</li>
          <li>信用等级可填写：AAA、AA、A、B、C</li>
          <li>付款条件可填写：现金支付、月结30天、月结60天、月结90天</li>
          <li>单次最多可导入500条数据</li>
        </ol>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleUpload">
          开始导入
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, UploadInstance, UploadProps, UploadUserFile } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { importSuppliers } from '@/api/supplier'

interface Props {
  visible: boolean
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}

const props = withDefaults(defineProps<Props>(), {
  visible: false
})

const emit = defineEmits<Emits>()

const uploadRef = ref<UploadInstance>()
const fileList = ref<UploadUserFile[]>([])
const loading = ref(false)

// 文件变更
const handleFileChange: UploadProps['onChange'] = (uploadFile) => {
  fileList.value = [uploadFile]
}

// 超出文件数量限制
const handleExceed = () => {
  ElMessage.warning('只能上传一个文件')
}

// 下载导入模板
const downloadTemplate = () => {
  // 实际项目中应该调用后端接口下载模板
  ElMessage.success('模板下载中...')
  
  // 模拟下载过程
  const link = document.createElement('a')
  link.href = '/templates/supplier_import_template.xlsx' // 实际项目中替换为真实路径
  link.download = '供应商导入模板.xlsx'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 关闭弹窗
const handleClose = () => {
  fileList.value = []
  emit('update:visible', false)
}

// 上传文件
const handleUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择要上传的文件')
    return
  }
  
  const file = fileList.value[0].raw
  if (!file) {
    ElMessage.error('文件获取失败')
    return
  }
  
  // 检查文件大小
  const maxSize = 10 * 1024 * 1024 // 10MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过10MB')
    return
  }
  
  // 检查文件类型
  const validTypes = [
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'text/csv'
  ]
  if (!validTypes.includes(file.type)) {
    ElMessage.error('只支持Excel或CSV格式文件')
    return
  }
  
  try {
    loading.value = true
    
    // 创建FormData对象
    const formData = new FormData()
    formData.append('file', file)
    
    // 调用导入API
    await importSuppliers(formData)
    
    ElMessage.success('导入成功')
    emit('success')
    handleClose()
  } catch (error) {
    console.error('导入失败:', error)
    ElMessage.error('导入失败，请检查文件格式是否正确')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.import-container {
  padding: 10px;
}

.template-download {
  margin-top: 20px;
  text-align: center;
}

.import-tips {
  margin-top: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.import-tips h4 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 14px;
  color: #606266;
}

.import-tips ol {
  margin: 0;
  padding-left: 20px;
}

.import-tips li {
  font-size: 13px;
  line-height: 1.8;
  color: #606266;
}

.dialog-footer {
  text-align: right;
}
</style>