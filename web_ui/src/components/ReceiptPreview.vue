<template>
  <div class="receipt-preview">
    <div class="receipt-header">
      <h3>便民超市</h3>
      <p>北京市朝阳区XX街道XX号</p>
      <p>电话: 010-12345678</p>
      <div class="divider">================================</div>
    </div>
    
    <div class="receipt-info">
      <div class="info-row">
        <span>订单号: {{ orderInfo.orderNo }}</span>
      </div>
      <div class="info-row">
        <span>收银员: {{ orderInfo.cashier }}</span>
      </div>
      <div class="info-row">
        <span>时间: {{ orderInfo.createTime }}</span>
      </div>
      <div v-if="orderInfo.memberName" class="info-row">
        <span>会员: {{ orderInfo.memberName }} ({{ orderInfo.memberLevel }})</span>
      </div>
      <div class="divider">--------------------------------</div>
    </div>
    
    <div class="receipt-items">
      <div class="items-header">
        <span class="item-name">商品名称</span>
        <span class="item-price">单价</span>
        <span class="item-qty">数量</span>
        <span class="item-total">小计</span>
      </div>
      <div class="divider">--------------------------------</div>
      
      <div v-for="item in orderInfo.items" :key="item.id" class="item-row">
        <span class="item-name">{{ item.name }}</span>
        <span class="item-price">{{ item.price.toFixed(2) }}</span>
        <span class="item-qty">{{ item.quantity }}</span>
        <span class="item-total">{{ (item.price * item.quantity).toFixed(2) }}</span>
      </div>
      
      <div class="divider">--------------------------------</div>
    </div>
    
    <div class="receipt-summary">
      <div class="summary-row">
        <span>商品总数:</span>
        <span>{{ orderInfo.totalQuantity }} 件</span>
      </div>
      <div class="summary-row">
        <span>商品金额:</span>
        <span>¥{{ orderInfo.totalAmount.toFixed(2) }}</span>
      </div>
      <div v-if="orderInfo.discount < 100" class="summary-row">
        <span>会员折扣:</span>
        <span>{{ orderInfo.discount }}%</span>
      </div>
      <div v-if="orderInfo.discountAmount > 0" class="summary-row">
        <span>优惠金额:</span>
        <span>-¥{{ orderInfo.discountAmount.toFixed(2) }}</span>
      </div>
      <div class="summary-row total-row">
        <span>应付金额:</span>
        <span>¥{{ orderInfo.finalAmount.toFixed(2) }}</span>
      </div>
      <div class="summary-row">
        <span>支付方式:</span>
        <span>{{ orderInfo.paymentMethod }}</span>
      </div>
      <div class="summary-row">
        <span>实付金额:</span>
        <span>¥{{ orderInfo.paidAmount.toFixed(2) }}</span>
      </div>
      <div v-if="orderInfo.changeAmount > 0" class="summary-row">
        <span>找零:</span>
        <span>¥{{ orderInfo.changeAmount.toFixed(2) }}</span>
      </div>
    </div>
    
    <div class="receipt-footer">
      <div class="divider">================================</div>
      <p>谢谢惠顾，欢迎再次光临！</p>
      <p>如有问题请联系客服</p>
    </div>
  </div>
</template>

<script setup lang="ts">
interface OrderInfo {
  orderNo: string
  cashier: string
  createTime: string
  memberName?: string
  memberLevel?: string
  items: Array<{
    id: string
    name: string
    price: number
    quantity: number
  }>
  totalQuantity: number
  totalAmount: number
  discount: number
  discountAmount: number
  finalAmount: number
  paymentMethod: string
  paidAmount: number
  changeAmount: number
}

defineProps<{
  orderInfo: OrderInfo
}>()
</script>

<style scoped>
.receipt-preview {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.4;
  color: #333;
  background: white;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  max-height: 400px;
  overflow-y: auto;
}

.receipt-header {
  text-align: center;
  margin-bottom: 10px;
}

.receipt-header h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: bold;
}

.receipt-header p {
  margin: 2px 0;
  font-size: 11px;
}

.divider {
  text-align: center;
  margin: 5px 0;
  color: #666;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 3px;
}

.items-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 5px;
  font-weight: bold;
  margin-bottom: 5px;
}

.item-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 5px;
  margin-bottom: 3px;
}

.item-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-price, .item-qty, .item-total {
  text-align: right;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 3px;
}

.total-row {
  font-weight: bold;
  font-size: 13px;
  border-top: 1px solid #333;
  padding-top: 3px;
  margin-top: 5px;
}

.receipt-footer {
  text-align: center;
  margin-top: 10px;
}

.receipt-footer p {
  margin: 3px 0;
  font-size: 11px;
}
</style>