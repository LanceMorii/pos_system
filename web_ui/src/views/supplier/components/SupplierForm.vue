<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="val => emit('update:visible', val)"
    :title="isEdit ? '编辑供应商' : '新增供应商'"
    width="800px"
    :before-close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      label-position="left"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="供应商名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入供应商名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系人" prop="contact">
            <el-input v-model="form.contact" placeholder="请输入联系人姓名" />
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入联系电话" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱地址" />
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="主营类别" prop="category">
            <el-select v-model="form.category" placeholder="请选择主营类别" style="width: 100%">
              <el-option
                v-for="category in categories"
                :key="category.value"
                :label="category.label"
                :value="category.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio label="normal">正常</el-radio>
              <el-radio label="disabled">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-form-item label="地址" prop="address">
        <el-input v-model="form.address" placeholder="请输入详细地址" />
      </el-form-item>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="信用等级">
            <el-select v-model="form.creditRating" placeholder="请选择信用等级" style="width: 100%">
              <el-option label="AAA级" value="AAA" />
              <el-option label="AA级" value="AA" />
              <el-option label="A级" value="A" />
              <el-option label="B级" value="B" />
              <el-option label="C级" value="C" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="付款条件">
            <el-select v-model="form.paymentTerms" placeholder="请选择付款条件" style="width: 100%">
              <el-option label="现金支付" value="cash" />
              <el-option label="月结30天" value="net30" />
              <el-option label="月结60天" value="net60" />
              <el-option label="月结90天" value="net90" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="税号">
            <el-input v-model="form.taxNumber" placeholder="请输入税务登记号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="银行账户">
            <el-input v-model="form.bankAccount" placeholder="请输入银行账户" />
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-form-item label="备注说明">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="请输入备注说明"
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { addSupplier, updateSupplier, type Supplier } from '@/api/supplier'

interface Props {
  visible: boolean
  isEdit: boolean
  supplierData?: Supplier
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  isEdit: false,
  supplierData: undefined
})

const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()
const loading = ref(false)

// 供应商分类选项
const categories = [
  { label: '生鲜果蔬', value: '生鲜果蔬' },
  { label: '肉禽蛋品', value: '肉禽蛋品' },
  { label: '粮油调味', value: '粮油调味' },
  { label: '酒水饮料', value: '酒水饮料' },
  { label: '日用百货', value: '日用百货' },
  { label: '冷冻食品', value: '冷冻食品' },
  { label: '烘焙原料', value: '烘焙原料' },
  { label: '清洁用品', value: '清洁用品' }
]

// 表单数据
const form = reactive<Supplier>({
  name: '',
  contact: '',
  phone: '',
  email: '',
  address: '',
  category: '',
  status: 'normal',
  creditRating: '',
  paymentTerms: '',
  taxNumber: '',
  bankAccount: '',
  description: ''
})

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入供应商名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 5, max: 200, message: '长度在 5 到 200 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择主营类别', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 监听弹窗显示状态
watch(() => props.visible, (newVal) => {
  if (newVal) {
    resetForm()
    if (props.isEdit && props.supplierData) {
      Object.assign(form, props.supplierData)
    }
  }
})

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    name: '',
    contact: '',
    phone: '',
    email: '',
    address: '',
    category: '',
    status: 'normal',
    creditRating: '',
    paymentTerms: '',
    taxNumber: '',
    bankAccount: '',
    description: ''
  })
  formRef.value?.clearValidate()
}

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    if (props.isEdit) {
      await updateSupplier(form)
      ElMessage.success('供应商更新成功')
    } else {
      await addSupplier(form)
      ElMessage.success('供应商创建成功')
    }
    
    emit('success')
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>