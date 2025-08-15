<template>
  <div class="cashier-page">
    <div class="left">
      <el-input v-model="keyword" placeholder="输入名称/条码搜索（回车）" clearable @keyup.enter="search">
        <template #append>
          <el-button :loading="loading" @click="search">搜索</el-button>
        </template>
      </el-input>

      <el-scrollbar class="product-list">
        <el-skeleton v-if="loading" animated :throttle="300">
          <template #template>
            <el-skeleton-item variant="text" style="width: 60%" />
            <el-skeleton-item variant="text" />
            <el-skeleton-item variant="text" />
          </template>
        </el-skeleton>
        <el-empty v-else-if="!products.length" description="无商品" />
        <el-card v-else v-for="p in products" :key="p.id" class="product-item" shadow="hover">
          <div class="item-row">
            <div class="thumb">
              <el-avatar shape="square" :size="50">{{ (p.name || '?').slice(0,1) }}</el-avatar>
            </div>
            <div class="meta">
              <div class="name">
                {{ p.name }}
                <el-tag v-if="Number(p.currentStock) <= Number(p.minStock)" type="warning" size="small" class="ml8">库存预警</el-tag>
              </div>
              <div class="code">编码: {{ p.code }} · 条码: {{ p.barcode || '-' }}</div>
              <div class="stock">库存：{{ p.currentStock ?? '-' }}</div>
            </div>
            <div class="price">
              <div class="sale">￥{{ p.salePrice }}</div>
              <el-button type="primary" @click="addToCart(p)">加入购物车</el-button>
            </div>
          </div>
        </el-card>
      </el-scrollbar>
    </div>

    <div class="right">
      <div class="cart-header">
        <div>购物车</div>
      </div>
      <div class="member-row">
        <el-input v-model="memberPhone" placeholder="会员手机号（可选）" clearable size="small" @keyup.enter="searchMember">
          <template #append>
            <el-button :loading="memberLoading" @click="searchMember">查询</el-button>
          </template>
        </el-input>
        <div class="member-hint">
          <template v-if="memberMatchedName">
            匹配会员：<b>{{ memberMatchedName }}</b>
          </template>
          <template v-else-if="memberPhone && !memberLoading">
            未找到会员，将使用默认名称
          </template>
        </div>
      </div>
      <el-table :data="cart" size="large" border height="420" class="cart-table">
        <el-table-column prop="name" label="商品" min-width="160" />
        <el-table-column label="单价" width="100">
          <template #default="{ row }">￥{{ row.salePrice }}</template>
        </el-table-column>
        <el-table-column label="数量" width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.qty" :min="1" :max="999" @change="recalc" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">￥{{ (row.salePrice * row.qty).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="90">
          <template #default="{ $index }">
            <el-button link type="danger" @click="removeAt($index)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="summary">
        <div class="total">合计：<b>￥{{ total.toFixed(2) }}</b></div>
        <el-button type="danger" link @click="clearCart" :disabled="!cart.length">清空</el-button>
        <el-button type="success" size="large" :disabled="!cart.length" :loading="submitting" @click="startPay">去支付</el-button>
      </div>

      <el-divider />
      <el-button @click="ping" :loading="pinging">后端连通性测试</el-button>
      <div class="ping-result">{{ pingText }}</div>
    </div>

    <!-- 模拟支付对话框 -->
    <el-dialog v-model="payVisible" :title="payTitle" width="560px" @close="resetPay">
      <div class="pay-method">
        <el-radio-group v-model="payMethod" size="large">
          <el-radio-button label="CASH">现金</el-radio-button>
          <el-radio-button label="WECHAT">微信</el-radio-button>
          <el-radio-button label="ALIPAY">支付宝</el-radio-button>
          <el-radio-button label="CARD">银行卡</el-radio-button>
        </el-radio-group>
      </div>
      <div v-if="payMethod === 'CASH'" class="pay-pane">
        <el-form label-width="100px">
          <el-form-item label="应收金额">
            <el-tag type="info">￥{{ total.toFixed(2) }}</el-tag>
          </el-form-item>
          <el-form-item label="实收金额">
            <el-input v-model.number="cashAmount" type="number" placeholder="输入顾客支付金额" />
          </el-form-item>
          <el-alert v-if="cashAmount >= total" type="success" :closable="false" show-icon>
            找零：￥{{ (cashAmount - total).toFixed(2) }}
          </el-alert>
          <el-alert v-else type="warning" :closable="false" show-icon>
            金额不足，请重新确认
          </el-alert>
        </el-form>
      </div>

      <div v-else-if="payMethod === 'WECHAT' || payMethod === 'ALIPAY'" class="pay-pane qr-pane">
        <div class="qr-box">
          <div class="qr">QR</div>
        </div>
        <div class="qr-tip">
          请使用 {{ payMethod === 'WECHAT' ? '微信' : '支付宝' }} 扫码支付
        </div>
        <el-alert type="info" :closable="false" show-icon>
          模拟：倒计时 {{ countdown }}s 后自动回调“支付成功”，或点击下方“已支付”立即完成
        </el-alert>
      </div>

      <div v-else-if="payMethod === 'CARD'" class="pay-pane">
        <el-form label-width="100px">
          <el-form-item label="银行卡末四位">
            <el-input v-model="cardLast4" maxlength="4" placeholder="例如 1234" />
          </el-form-item>
          <el-alert type="info" :closable="false" show-icon>
            模拟：输入任意 4 位数字并点击确认，即视为发卡行授权成功
          </el-alert>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="payVisible = false" :disabled="submitting">取消</el-button>
          <el-button v-if="payMethod === 'WECHAT' || payMethod === 'ALIPAY'" type="primary" @click="mockPaySuccess" :loading="submitting">已支付</el-button>
          <el-button v-else-if="payMethod === 'CASH'" type="primary" :disabled="cashAmount < total" @click="mockPaySuccess" :loading="submitting">确认收款</el-button>
          <el-button v-else type="primary" :disabled="cardLast4.length !== 4" @click="mockPaySuccess" :loading="submitting">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { getCashierProducts, checkoutOrder, healthCheck } from '@/api/cashier'
import { getMemberByPhone } from '@/api/member'

const keyword = ref('')
const loading = ref(false)
const products = ref<any[]>([])

const cart = reactive<any[]>([])
const total = computed(() => cart.reduce((s, it) => s + Number(it.salePrice) * it.qty, 0))
const payMethod = ref<'CASH'|'WECHAT'|'ALIPAY'|'CARD'>('CASH')
// 会员手机号与匹配到的姓名（可选）
const memberPhone = ref('')
const memberMatchedName = ref('')
const memberLoading = ref(false)
const submitting = ref(false)

// 支付弹窗状态
const payVisible = ref(false)
const cashAmount = ref<number>(0)
const cardLast4 = ref('')
const countdown = ref(12)
let timer: any = null

const payTitle = computed(() => {
  switch (payMethod.value) {
    case 'CASH': return '现金收款'
    case 'WECHAT': return '微信支付'
    case 'ALIPAY': return '支付宝支付'
    case 'CARD': return '银行卡支付'
  }
  return '支付'
})

async function search() {
  loading.value = true
  try {
    const res: any = await getCashierProducts({ name: keyword.value || undefined })
    if (res.code === 200) {
      products.value = res.data?.list || res.data || []
    } else {
      ElMessage.error(res.message || '查询失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '网络错误')
  } finally {
    loading.value = false
  }
}

// 按手机号查询会员姓名
async function searchMember() {
  if (!memberPhone.value) {
    memberMatchedName.value = ''
    return
  }
  memberLoading.value = true
  try {
    const res: any = await getMemberByPhone(memberPhone.value)
    if (res.code === 200 && res.data) {
      // 后端 MemberDTO 可能包含 name/username/realName 字段，做容错
      memberMatchedName.value = res.data.name || res.data.username || res.data.realName || ''
    } else {
      memberMatchedName.value = ''
    }
  } catch (e) {
    memberMatchedName.value = ''
  } finally {
    memberLoading.value = false
  }
}

function addToCart(p: any) {
  const found = cart.find((x) => x.id === p.id)
  if (found) {
    found.qty += 1
  } else {
    cart.push({ ...p, qty: 1 })
  }
}

function removeAt(i: number) {
  cart.splice(i, 1)
}

function recalc() {
  // 触发计算属性刷新
}

const pinging = ref(false)
const pingText = ref('')
async function ping() {
  pinging.value = true
  try {
    const res: any = await healthCheck()
    pingText.value = JSON.stringify(res, null, 2)
  } finally {
    pinging.value = false
  }
}

function startPay() {
  if (!cart.length) return
  resetPay()
  payVisible.value = true
  if (payMethod.value === 'WECHAT' || payMethod.value === 'ALIPAY') {
    // 模拟扫码支付倒计时
    timer = setInterval(() => {
      countdown.value -= 1
      if (countdown.value <= 0) {
        clearInterval(timer)
        timer = null
        mockPaySuccess()
      }
    }, 1000)
  }
}

function resetPay() {
  cashAmount.value = 0
  cardLast4.value = ''
  countdown.value = 12
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

async function mockPaySuccess() {
  submitting.value = true
  try {
    // 统一走后端结算
    const payload = {
      items: cart.map((x) => ({ productId: x.id, qty: x.qty, price: x.salePrice })),
      paymentMethod: payMethod.value,
      customerName: (memberMatchedName.value && memberMatchedName.value.trim()) ? memberMatchedName.value.trim() : '客户无会员信息'
    }
    const res: any = await checkoutOrder(payload)
    if (res.code === 200) {
      ElMessage.success('支付成功')
      cart.splice(0, cart.length)
      payVisible.value = false
      memberPhone.value = ''
      memberMatchedName.value = ''
    } else {
      ElMessage.error(res.message || '支付失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '网络错误')
  } finally {
    submitting.value = false
    if (timer) { clearInterval(timer); timer = null }
  }
}

function clearCart() {
  cart.splice(0, cart.length)
}

onBeforeUnmount(() => { if (timer) clearInterval(timer) })

// 初始加载
search()
</script>

<style scoped>
.cashier-page { display: grid; grid-template-columns: 1.2fr 1fr; gap: 16px; padding: 16px; }
.left { display: flex; flex-direction: column; gap: 12px; }
.product-list { height: 620px; }
.product-item { margin: 8px 0; }
.item-row { display: grid; grid-template-columns: 56px 1fr auto; align-items: center; gap: 12px; }
.thumb { display:flex; align-items:center; justify-content:center; }
.meta .name { font-weight: 600; font-size: 16px; display:flex; align-items:center; gap:8px; }
.meta .code { color: #909399; font-size: 12px; margin-top: 4px; }
.meta .stock { color: #67c23a; font-size: 12px; margin-top: 2px; }
.price { display: flex; align-items: center; gap: 12px; }
.right { background: #fff; border: 1px solid #ebeef5; border-radius: 8px; padding: 12px; position: sticky; top: 12px; height: fit-content; }
.cart-header { font-weight: 600; margin-bottom: 8px; display:flex; align-items:center; justify-content:space-between; }
.cart-table :deep(.el-input-number) { width: 120px; }
.summary { display: flex; align-items: center; gap: 12px; margin-top: 8px; justify-content: flex-end; }
.summary .total { font-size: 16px; margin-right: auto; }
.ml8 { margin-left: 8px; }
.ping-result { white-space: pre-wrap; font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace; font-size: 12px; margin-top: 6px; }

/* 支付对话框 */
.pay-pane { display: flex; flex-direction: column; gap: 12px; }
.qr-pane { align-items: center; }
.qr-box { width: 220px; height: 220px; border: 1px dashed #dcdfe6; display:flex; align-items:center; justify-content:center; border-radius: 4px; margin-bottom: 8px; }
.qr { width: 160px; height: 160px; background: repeating-linear-gradient(45deg, #eee, #eee 10px, #ddd 10px, #ddd 20px); display:flex; align-items:center; justify-content:center; color:#666; font-weight:600; border-radius: 4px; }
.qr-tip { color:#606266; margin-bottom: 8px; }
</style>
