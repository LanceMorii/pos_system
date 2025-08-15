<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="val => emit('update:visible', val)"
    title="供应商详情"
    width="800px"
    :before-close="handleClose"
  >
    <el-descriptions :column="2" border>
      <el-descriptions-item label="供应商ID">{{ supplier?.id }}</el-descriptions-item>
      <el-descriptions-item label="供应商名称">{{ supplier?.name }}</el-descriptions-item>
      <el-descriptions-item label="联系人">{{ supplier?.contact }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ supplier?.phone }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ supplier?.email }}</el-descriptions-item>
      <el-descriptions-item label="主营类别">{{ supplier?.category }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="supplier?.status === 'normal' ? 'success' : 'danger'">
          {{ supplier?.status === 'normal' ? '正常' : '停用' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="信用等级">
        <el-tag :type="getCreditRatingType(supplier?.creditRating)">
          {{ supplier?.creditRating }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="付款条件">
        {{ getPaymentTermsText(supplier?.paymentTerms) }}
      </el-descriptions-item>
      <el-descriptions-item label="税号">{{ supplier?.taxNumber || '未设置' }}</el-descriptions-item>
      <el-descriptions-item label="银行账户">{{ supplier?.bankAccount || '未设置' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ supplier?.createTime }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ supplier?.updateTime || '未更新' }}</el-descriptions-item>
      <el-descriptions-item label="详细地址" :span="2">{{ supplier?.address }}</el-descriptions-item>
      <el-descriptions-item label="备注说明" :span="2">
        {{ supplier?.description || '无' }}
      </el-descriptions-item>
    </el-descriptions>
    
    <!-- 供应商交易记录 -->
    <div class="transaction-history" v-if="transactions.length > 0">
      <h3>近期交易记录</h3>
      <el-table :data="transactions" border style="width: 100%">
        <el-table-column prop="date" label="交易日期" width="120" />
        <el-table-column prop="orderNo" label="订单编号" width="150" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit">编辑</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { type Supplier } from '@/api/supplier'

interface Props {
  visible: boolean
  supplier?: Supplier
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'edit'): void
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  supplier: undefined
})

const emit = defineEmits<Emits>()

// 模拟交易记录数据
const transactions = ref([
  {
    date: '2024-07-10',
    orderNo: 'PO20240710001',
    amount: 12500.00,
    status: '已完成',
    remark: '季度常规采购'
  },
  {
    date: '2024-06-25',
    orderNo: 'PO20240625003',
    amount: 8750.50,
    status: '已完成',
    remark: '紧急补货'
  },
  {
    date: '2024-06-15',
    orderNo: 'PO20240615002',
    amount: 15200.00,
    status: '已完成',
    remark: '月度常规采购'
  }
])

// 获取信用等级对应的标签类型
const getCreditRatingType = (rating?: string) => {
  if (!rating) return 'info'
  
  switch (rating) {
    case 'AAA':
      return 'success'
    case 'AA':
      return 'success'
    case 'A':
      return 'primary'
    case 'B':
      return 'warning'
    case 'C':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取付款条件文本
const getPaymentTermsText = (terms?: string) => {
  if (!terms) return '未设置'
  
  const termsMap: Record<string, string> = {
    'cash': '现金支付',
    'net30': '月结30天',
    'net60': '月结60天',
    'net90': '月结90天'
  }
  
  return termsMap[terms] || terms
}

// 获取交易状态对应的标签类型
const getStatusType = (status: string) => {
  switch (status) {
    case '已完成':
      return 'success'
    case '处理中':
      return 'primary'
    case '待付款':
      return 'warning'
    case '已取消':
      return 'danger'
    default:
      return 'info'
  }
}

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
}

// 编辑供应商
const handleEdit = () => {
  emit('edit')
  handleClose()
}

// 监听弹窗显示状态，加载供应商详情
watch(() => props.visible, (newVal) => {
  if (newVal && props.supplier?.id) {
    // 这里可以加载供应商的详细信息和交易记录
    // 实际项目中可以调用API获取
    console.log('加载供应商详情:', props.supplier.id)
  }
})
</script>

<style scoped>
.dialog-footer {
  text-align: right;
  margin-top: 20px;
}

.transaction-history {
  margin-top: 30px;
}

.transaction-history h3 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}
</style>