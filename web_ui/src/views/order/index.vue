<template>
  <div class="orders-page">
    <el-card class="mb12" shadow="never">
      <div class="filters">
        <el-input v-model="query.orderNo" placeholder="订单号" clearable style="width: 200px" @keyup.enter="load" />
        <el-input v-model="query.cashier" placeholder="收银员" clearable style="width: 180px" @keyup.enter="load" />
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
        <el-button type="primary" :loading="loading" @click="load">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" :loading="exporting" @click="doExport">导出CSV</el-button>
      </div>
    </el-card>

    <el-card shadow="never">
      <el-table :data="rows" border :loading="loading" height="520">
        <el-table-column prop="orderNo" label="订单号" min-width="160" />
        <el-table-column prop="customerName" label="顾客/会员" min-width="140" />
        <el-table-column prop="cashierName" label="收银员" width="120" />
        <el-table-column prop="finalAmount" label="应收金额" width="120">
          <template #default="{ row }">￥{{ Number(row.finalAmount).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="110" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag type="success" v-if="row.status === 'COMPLETED'">已完成</el-tag>
            <el-tag type="warning" v-else-if="row.status === 'PENDING'">待支付</el-tag>
            <el-tag type="danger" v-else-if="row.status === 'CANCELLED'">已取消</el-tag>
            <el-tag type="info" v-else>其他</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="330" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-divider direction="vertical" />
            <el-button link type="warning" @click="openEdit(row)">编辑</el-button>
            <el-divider direction="vertical" />
            <el-button link type="warning" :disabled="row.status!=='COMPLETED'" @click="handleCancel(row)">取消</el-button>
            <el-divider direction="vertical" />
            <el-button link type="danger" :disabled="row.status!=='COMPLETED'" @click="handleRefund(row)">退款</el-button>
            <el-divider direction="vertical" />
            <el-tooltip :disabled="canDelete(row)" :content="deleteDisabledReason(row)" placement="top">
              <el-button link type="danger" :disabled="!canDelete(row)" @click="handleDelete(row)">删除</el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
      <div class="pager">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10,20,30,50]"
          :current-page="query.page"
          :page-size="query.limit"
          :total="total"
          @update:current-page="(p:number)=>{query.page=p; load()}"
          @update:page-size="(s:number)=>{query.limit=s; query.page=1; load()}"
        />
      </div>
    </el-card>

    <el-drawer v-model="detailVisible" title="订单详情" size="50%">
      <el-descriptions :column="2" border v-if="current">
        <el-descriptions-item label="订单号">
          <el-text copyable>{{ current.orderNo }}</el-text>
        </el-descriptions-item>
        <el-descriptions-item label="订单ID">{{ current.id }}</el-descriptions-item>
        <el-descriptions-item label="顾客/会员">{{ current.customerName || current.memberName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="会员ID">{{ current.memberId ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="收银员">{{ current.cashierName }}</el-descriptions-item>
        <el-descriptions-item label="收银员ID">{{ current.cashierId }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ current.paymentMethod }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ current.status }}</el-descriptions-item>
        <el-descriptions-item label="总金额">￥{{ Number(current.totalAmount ?? current.finalAmount).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="优惠">￥{{ Number(current.discountAmount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="应收">￥{{ Number(current.finalAmount).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="积分">{{ current.pointsEarned ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="商品件数">{{ current.itemCount ?? (current.items?.length || 0) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(current.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDate(current.updatedAt) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ current.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <el-table :data="current?.items || []" border height="360">
        <el-table-column prop="productName" label="商品" min-width="180" />
        <el-table-column prop="productCode" label="编码" width="120" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">￥{{ Number(row.unitPrice).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="90" />
        <el-table-column prop="subtotal" label="小计" width="120">
          <template #default="{ row }">￥{{ Number(row.subtotal).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </el-drawer>

    <el-dialog v-model="editVisible" title="编辑订单" width="520px">
      <el-form label-width="96px">
        <el-form-item label="顾客/会员">
          <el-input v-model="editForm.customerName" placeholder="顾客姓名" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width: 200px">
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="待支付" value="PENDING" />
            <el-option label="已取消" value="CANCELLED" />
            <el-option label="已退款" value="REFUNDED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" rows="3" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editVisible=false">取 消</el-button>
          <el-button type="primary" @click="submitEdit">保 存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, getOrderById, exportOrders, updateOrder, cancelOrder, refundOrder, deleteOrder, type OrderListQuery } from '@/api/order'

const loading = ref(false)
const exporting = ref(false)
const rows = ref<any[]>([])
const total = ref(0)

const query = reactive<OrderListQuery>({ page: 1, limit: 20, orderNo: '', cashier: '' })
const dateRange = ref<[string, string] | null>(null)

function formatDate(val: any) {
  if (!val) return '-'
  try {
    const d = new Date(val)
    const pad = (n:number)=> String(n).padStart(2,'0')
    return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
  } catch {
    return String(val)
  }
}

async function load() {
  loading.value = true
  try {
    const params: OrderListQuery = {
      page: query.page,
      limit: query.limit,
      orderNo: query.orderNo?.trim() || undefined,
      cashier: query.cashier?.trim() || undefined,
      startTime: dateRange.value ? dateRange.value[0] : undefined,
      endTime: dateRange.value ? dateRange.value[1] : undefined
    }
    const res: any = await getOrderList(params)
    if (res.code === 200) {
      rows.value = res.data?.items || []
      total.value = res.data?.total || 0
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (e:any) {
    ElMessage.error(e.message || '请求失败')
  } finally {
    loading.value = false
  }
}

function reset() {
  query.orderNo = ''
  query.cashier = ''
  query.page = 1
  query.limit = 20
  dateRange.value = null
  load()
}

const detailVisible = ref(false)
const current = ref<any>(null)

async function openDetail(row:any) {
  try {
    const res:any = await getOrderById(row.id)
    if (res.code === 200) {
      current.value = res.data
      detailVisible.value = true
    }
  } catch (e:any) {
    ElMessage.error(e.message || '加载详情失败')
  }
}

async function doExport() {
  exporting.value = true
  try {
    const params: OrderListQuery = {
      orderNo: query.orderNo?.trim() || undefined,
      cashier: query.cashier?.trim() || undefined,
      startTime: dateRange.value ? dateRange.value[0] : undefined,
      endTime: dateRange.value ? dateRange.value[1] : undefined
    }
    const blob = await exportOrders(params)
    const url = window.URL.createObjectURL(new Blob([blob]))
    const a = document.createElement('a')
    a.href = url
    a.download = `订单导出_${Date.now()}.csv`
    a.click()
    window.URL.revokeObjectURL(url)
  } catch (e:any) {
    ElMessage.error(e.message || '导出失败')
  } finally {
    exporting.value = false
  }
}

onMounted(load)

// 编辑对话框
const editVisible = ref(false)
const editForm = reactive<any>({ id: null, customerName: '', remark: '', status: '' })

// 删除按钮可用性与提示
function canDelete(row:any){
  return row && (row.status === 'CANCELLED' || row.status === 'REFUNDED')
}

function deleteDisabledReason(row:any){
  if(!row) return '无效订单'
  if(canDelete(row)) return ''
  return '仅允许删除已取消或已退款的订单（为保护数据与对账安全）'
}

// 操作密码校验（静态密码：admin）
async function askPassword(): Promise<boolean> {
  try {
    await ElMessageBox.prompt('请输入操作密码', '安全验证', {
      confirmButtonText: '验证',
      cancelButtonText: '取消',
      inputType: 'password',
      inputPlaceholder: '请输入密码',
      inputValidator: (val: string) => {
        if (!val) return '请输入密码'
        return val === 'admin' ? true : '密码错误'
      }
    })
    return true
  } catch {
    return false
  }
}

function openEdit(row:any){
  editForm.id = row.id
  editForm.customerName = row.customerName
  editForm.remark = row.remark
  editForm.status = row.status
  editVisible.value = true
}

async function submitEdit(){
  if(!editForm.id) return
  try{
    const res:any = await updateOrder(editForm.id, {
      customerName: editForm.customerName,
      remark: editForm.remark,
      status: editForm.status
    })
    if(res.code===200){
      ElMessage.success('更新成功')
      editVisible.value = false
      load()
    }else{
      ElMessage.error(res.message||'更新失败')
    }
  }catch(e:any){
    ElMessage.error(e.message||'更新失败')
  }
}

async function handleCancel(row:any){
  try{
    const ok = await askPassword()
    if(!ok) return
    await ElMessageBox.confirm('确认取消该订单吗？','提示',{type:'warning'})
    const res:any = await cancelOrder(row.id)
    if(res.code===200){
      ElMessage.success('已取消')
      load()
    }else{
      ElMessage.error(res.message||'取消失败')
    }
  }catch{}
}

async function handleRefund(row:any){
  try{
    await ElMessageBox.confirm('确认为该订单办理退款吗？','提示',{type:'warning'})
    const res:any = await refundOrder(row.id,{ reason:'前台操作' })
    if(res.code===200){
      ElMessage.success('已退款')
      load()
    }else{
      ElMessage.error(res.message||'退款失败')
    }
  }catch{}
}

async function handleDelete(row:any){
  try{
    const ok = await askPassword()
    if(!ok) return
    await ElMessageBox.confirm('删除不可恢复，确认继续？','危险操作',{type:'warning'})
    const res:any = await deleteOrder(row.id)
    if(res.code===200){
      ElMessage.success('删除成功')
      load()
    }else{
      ElMessage.error(res.message||'删除失败')
    }
  }catch{}
}
</script>

<style scoped>
.orders-page { padding: 12px; }
.mb12 { margin-bottom: 12px; }
.filters { display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }
.pager { display: flex; justify-content: flex-end; margin-top: 10px; }
</style>
