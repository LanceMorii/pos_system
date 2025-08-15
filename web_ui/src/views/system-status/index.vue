<template>
  <div class="system-status-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="运行概览" name="overview">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>系统状态检查</span>
              <el-button type="primary" @click="checkAllStatus" :loading="loading">
                <el-icon><Refresh /></el-icon>
                刷新状态
              </el-button>
            </div>
          </template>

          <div class="status-grid">
            <el-card shadow="hover" class="status-card" :class="dbStatus.status">
              <div class="status-content">
                <div class="status-icon">
                  <el-icon v-if="dbStatus.status === 'success'"><SuccessFilled /></el-icon>
                  <el-icon v-else-if="dbStatus.status === 'error'"><CircleCloseFilled /></el-icon>
                  <el-icon v-else><Loading /></el-icon>
                </div>
                <div class="status-info">
                  <h3>数据库连接</h3>
                  <p>{{ dbStatus.message }}</p>
                  <div v-if="dbStatus.data" class="status-details">
                    <span>今日订单: {{ dbStatus.data.todayOrders || 0 }}</span>
                    <span>今日销售: ¥{{ (dbStatus.data.todaySales || 0).toFixed(2) }}</span>
                  </div>
                </div>
              </div>
            </el-card>

            <el-card shadow="hover" class="status-card" :class="orderStatus.status">
              <div class="status-content">
                <div class="status-icon">
                  <el-icon v-if="orderStatus.status === 'success'"><SuccessFilled /></el-icon>
                  <el-icon v-else-if="orderStatus.status === 'error'"><CircleCloseFilled /></el-icon>
                  <el-icon v-else><Loading /></el-icon>
                </div>
                <div class="status-info">
                  <h3>订单系统</h3>
                  <p>{{ orderStatus.message }}</p>
                  <div v-if="orderStatus.data" class="status-details">
                    <span>订单号: {{ orderStatus.data.orderNo }}</span>
                    <span>金额: ¥{{ (orderStatus.data.finalAmount || 0).toFixed(2) }}</span>
                  </div>
                </div>
              </div>
            </el-card>

            <el-card shadow="hover" class="status-card" :class="cashierStatus.status">
              <div class="status-content">
                <div class="status-icon">
                  <el-icon v-if="cashierStatus.status === 'success'"><SuccessFilled /></el-icon>
                  <el-icon v-else-if="cashierStatus.status === 'error'"><CircleCloseFilled /></el-icon>
                  <el-icon v-else><Loading /></el-icon>
                </div>
                <div class="status-info">
                  <h3>收银系统</h3>
                  <p>{{ cashierStatus.message }}</p>
                  <div v-if="cashierStatus.data" class="status-details">
                    <span>商品数: {{ cashierStatus.data.productCount || 0 }}</span>
                    <span>分类数: {{ cashierStatus.data.categoryCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>

          <div class="system-info">
            <el-descriptions title="系统信息" :column="2" border>
              <el-descriptions-item label="系统版本">{{ sysInfo.appVersion || 'v1.0.0' }}</el-descriptions-item>
              <el-descriptions-item label="数据库模式">{{ databaseMode ? '生产模式' : '开发模式' }}</el-descriptions-item>
              <el-descriptions-item label="最后检查时间">{{ lastCheckTime }}</el-descriptions-item>
              <el-descriptions-item label="系统状态">{{ overallStatus }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="API 文档 / 测试" name="api">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>API 测试</span>
              <div style="display:flex;align-items:center;gap:8px">
                <el-radio-group v-model="apiSource" size="small">
                  <el-radio-button label="preset">预置接口</el-radio-button>
                  <el-radio-button label="openapi">OpenAPI</el-radio-button>
                </el-radio-group>
                <template v-if="apiSource === 'openapi'">
                  <el-input v-model="openapiUrl" placeholder="OpenAPI JSON 地址，例如 /v3/api-docs" style="width:320px" />
                  <el-button type="primary" @click="loadOpenapi">加载</el-button>
                </template>
                <el-input v-model="keyword" placeholder="搜索接口 (路径/说明)" clearable style="width:260px" />
                <el-button @click="batchTest" :loading="batchLoading">全部批量测试</el-button>
                <el-button text @click="expandAll(true)">全部展开</el-button>
                <el-button text @click="expandAll(false)">全部收起</el-button>
              </div>
            </div>
          </template>
          <div class="api-docs">
            <template v-if="apiSource === 'preset'">
              <el-collapse v-model="openedGroups" :accordion="false">
                <el-collapse-item v-for="(rows, group) in groupedDisplay" :name="group" :key="'preset-'+group">
                  <template #title>
                    <div style="display:flex;align-items:center;gap:8px">
                      <b>{{ group }}</b>
                      <el-tag size="small">{{ rows.length }}</el-tag>
                      <el-button size="small" @click.stop="batchTestGroup(group)">批量测试</el-button>
                    </div>
                  </template>
                  <el-table :data="rows" border style="width: 100%" row-key="id" :expand-row-keys="expandedRows">
                    <el-table-column type="expand">
                      <template #default="{ row }">
                        <div class="row-editor">
                          <el-row :gutter="12">
                            <el-col :span="12">
                              <div class="json-box">
                                <div class="json-box__title">Headers (JSON)</div>
                                <el-input type="textarea" v-model="row.headersText" :rows="8" />
                              </div>
                            </el-col>
                            <el-col :span="12">
                              <div class="json-box">
                                <div class="json-box__title">Body (JSON)</div>
                                <el-input type="textarea" v-model="row.bodyText" :rows="8" />
                              </div>
                            </el-col>
                          </el-row>
                          <div class="json-box" style="margin-top:8px">
                            <div class="json-box__title">响应</div>
                            <pre class="response-pre">{{ row.responseText }}</pre>
                          </div>
                        </div>
                      </template>
                    </el-table-column>
                    <el-table-column label="状态" width="120">
                      <template #default="{row}">
                        <el-tag v-if="row.status==='success'" type="success" size="small">成功 {{ row.lastTime }}ms</el-tag>
                        <el-tag v-else-if="row.status==='error'" type="danger" size="small">失败</el-tag>
                        <el-tag v-else size="small">未测试</el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column label="方法" width="100">
                      <template #default="{row}">
                        <el-tag :type="methodTagType(row.method)" size="small">{{ row.method }}</el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="url" label="请求地址" width="360" />
                    <el-table-column prop="summary" label="说明" />
                    <el-table-column label="启用" width="90">
                      <template #default="{row}">
                        <el-switch v-model="row.enabled" size="small" />
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="160">
                      <template #default="{row}">
                        <el-button size="small" type="primary" @click="sendEndpoint(row)" :loading="row.loading">发送</el-button>
                        <el-button size="small" text @click="toggleRow(row)">编辑</el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-collapse-item>
              </el-collapse>
            </template>

            <template v-else>
              <el-empty v-if="!openapiDoc" description="点击右上角加载文档" />
              <div v-else>
                <el-collapse v-model="openedGroups" :accordion="false">
                  <el-collapse-item v-for="(rows, group) in groupedDisplay" :name="group" :key="'openapi-'+group">
                    <template #title>
                      <div style="display:flex;align-items:center;gap:8px">
                        <b>{{ group }}</b>
                        <el-tag size="small">{{ rows.length }}</el-tag>
                        <el-button size="small" @click.stop="batchTestGroup(group)">批量测试</el-button>
                      </div>
                    </template>
                    <el-table :data="rows" border style="width: 100%" row-key="id" :expand-row-keys="expandedRows">
                      <el-table-column type="expand">
                        <template #default="{ row }">
                          <div class="row-editor">
                            <el-row :gutter="12">
                              <el-col :span="12">
                                <div class="json-box">
                                  <div class="json-box__title">Headers (JSON)</div>
                                  <el-input type="textarea" v-model="row.headersText" :rows="8" />
                                </div>
                              </el-col>
                              <el-col :span="12">
                                <div class="json-box">
                                  <div class="json-box__title">Body (JSON)</div>
                                  <el-input type="textarea" v-model="row.bodyText" :rows="8" />
                                </div>
                              </el-col>
                            </el-row>
                            <div class="json-box" style="margin-top:8px">
                              <div class="json-box__title">响应</div>
                              <pre class="response-pre">{{ row.responseText }}</pre>
                            </div>
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column label="状态" width="120">
                        <template #default="{row}">
                          <el-tag v-if="row.status==='success'" type="success" size="small">成功 {{ row.lastTime }}ms</el-tag>
                          <el-tag v-else-if="row.status==='error'" type="danger" size="small">失败</el-tag>
                          <el-tag v-else size="small">未测试</el-tag>
                        </template>
                      </el-table-column>
                      <el-table-column label="方法" width="100">
                        <template #default="{row}">
                          <el-tag :type="methodTagType(row.method)" size="small">{{ row.method }}</el-tag>
                        </template>
                      </el-table-column>
                      <el-table-column prop="url" label="请求地址" width="360" />
                      <el-table-column prop="summary" label="说明" />
                      <el-table-column label="启用" width="90">
                        <template #default="{row}">
                          <el-switch v-model="row.enabled" size="small" />
                        </template>
                      </el-table-column>
                      <el-table-column label="操作" width="160">
                        <template #default="{row}">
                          <el-button size="small" type="primary" @click="sendEndpoint(row)" :loading="row.loading">发送</el-button>
                          <el-button size="small" text @click="toggleRow(row)">编辑</el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </template>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, SuccessFilled, CircleCloseFilled, Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 页签
const activeTab = ref('overview')

// 状态数据
const loading = ref(false)
const databaseMode = ref(true)
const lastCheckTime = ref('')
const overallStatus = ref('检查中...')

// 监控已移除，但概览中仍引用版本信息，这里提供最小 sysInfo
const sysInfo = ref<any>({ appVersion: 'v1.0.0' })

// 状态数据（默认不检查，待手动触发）
const dbStatus = ref({
  status: 'idle',
  message: '未检测',
  data: null
})

const orderStatus = ref({
  status: 'idle',
  message: '未检测',
  data: null
})

const cashierStatus = ref({
  status: 'idle',
  message: '未检测',
  data: null
})

// 通用超时包装
function withTimeout<T>(p: Promise<T>, ms = 3000): Promise<T> {
  return new Promise((resolve, reject) => {
    const t = setTimeout(() => reject(new Error('请求超时')), ms)
    p.then((v) => { clearTimeout(t); resolve(v) }).catch((e) => { clearTimeout(t); reject(e) })
  })
}

// 当前展开的行
const expandedRows = ref<string[]>([])

function toggleRow(row: any) {
  const rowId = row.id
  const index = expandedRows.value.indexOf(rowId)
  if (index > -1) {
    expandedRows.value.splice(index, 1)
  } else {
    expandedRows.value = [rowId] // 只展开当前行
  }
}

// 检查数据库状态
const checkDatabaseStatus = async () => {
  try {
    const response = await withTimeout(request({
      url: '/test/database-status',
      method: 'get'
    }))

    if (response.code === 200) {
      dbStatus.value = {
        status: 'success',
        message: '数据库连接正常',
        data: response.data
      }
    } else {
      dbStatus.value = {
        status: 'error',
        message: response.message || '数据库连接失败',
        data: null
      }
    }
  } catch (error: any) {
    dbStatus.value = {
      status: 'error',
      message: '数据库连接异常: ' + error.message,
      data: null
    }
  }
}

// 检查订单系统
const checkOrderSystem = async () => {
  try {
    const response = await withTimeout(request({
      url: '/test/create-order',
      method: 'post'
    }))

    if (response.code === 200) {
      orderStatus.value = {
        status: 'success',
        message: '订单系统正常',
        data: response.data
      }
    } else {
      orderStatus.value = {
        status: 'error',
        message: response.message || '订单系统异常',
        data: null
      }
    }
  } catch (error: any) {
    orderStatus.value = {
      status: 'error',
      message: '订单系统异常: ' + error.message,
      data: null
    }
  }
}

// 检查收银系统
const checkCashierSystem = async () => {
  try {
    const response = await withTimeout(request({
      url: '/cashier/health',
      method: 'get'
    }))

    if (response.code === 200) {
      cashierStatus.value = {
        status: 'success',
        message: '收银系统正常',
        data: response.stats
      }
    } else {
      cashierStatus.value = {
        status: 'error',
        message: response.message || '收银系统异常',
        data: null
      }
    }
  } catch (error: any) {
    cashierStatus.value = {
      status: 'error',
      message: '收银系统异常: ' + error.message,
      data: null
    }
  }
}

// 检查所有状态
const checkAllStatus = async () => {
  loading.value = true

  // 重置状态
  dbStatus.value = { status: 'loading', message: '检查中...', data: null }
  orderStatus.value = { status: 'loading', message: '检查中...', data: null }
  cashierStatus.value = { status: 'loading', message: '检查中...', data: null }

  try {
    // 并行检查，任何失败不阻塞其他
    await Promise.allSettled([
      checkDatabaseStatus(),
      checkOrderSystem(),
      checkCashierSystem()
    ])

    // 更新整体状态
    const allSuccess = [dbStatus.value, orderStatus.value, cashierStatus.value]
      .every(status => status.status === 'success')

    if (allSuccess) {
      overallStatus.value = '系统正常'
      ElMessage.success('系统状态检查完成，所有模块正常')
    } else {
      overallStatus.value = '部分异常'
      ElMessage.warning('系统状态检查完成，部分模块异常或超时')
    }

    lastCheckTime.value = new Date().toLocaleString()
  } catch (error) {
    overallStatus.value = '检查失败'
    ElMessage.error('系统状态检查失败')
  } finally {
    loading.value = false
  }
}

// 监控相关函数已移除

// API 文档/测试
const apiSource = ref<'preset'|'openapi'>('preset')
const openapiUrl = ref('/v3/api-docs')
const openapiDoc = ref<any>(null)
// 扁平化后的接口项，包含默认头和体，可直接发送
const apiItems = ref<any[]>([])
const presetApiItems = ref<any[]>([])
const keyword = ref('')
const batchLoading = ref(false)

const displayItems = computed(() => {
  const list = apiSource.value === 'preset' ? presetApiItems.value : apiItems.value
  if (!keyword.value) return list
  const k = keyword.value.toLowerCase()
  return list.filter((it: any) => (it.url || it.path || '').toLowerCase().includes(k) || (it.summary || '').toLowerCase().includes(k))
})

// 分组：优先使用 OpenAPI 的 tags；否则用路径第一段作为模块名
function groupKey(it: any): string {
  if (it.tags && it.tags.length > 0) return it.tags[0]
  const p = (it.path || it.url || '/').replace(/^\/+/, '')
  const seg = p.split('/')[0]
  return seg ? seg : 'root'
}

const openedGroups = ref<string[]>([])

const groupedDisplay = computed<Record<string, any[]>>(() => {
  const groups: Record<string, any[]> = {}
  for (const it of displayItems.value) {
    const g = groupKey(it)
    if (!groups[g]) groups[g] = []
    groups[g].push(it)
  }
  if (openedGroups.value.length === 0) {
    openedGroups.value = Object.keys(groups)
  }
  return groups
})

function expandAll(open: boolean) {
  openedGroups.value = open ? Object.keys(groupedDisplay.value) : []
}

async function batchTestGroup(group: string) {
  const rows = groupedDisplay.value[group] || []
  for (const it of rows) {
    if (it.enabled) {
      it.responseText = ''
      await sendEndpoint(it)
    }
  }
  ElMessage.success(`${group} 分组批量测试完成`)
}

function makePreset(method: string, url: string, summary: string, body?: any) {
  const headers = { 'Content-Type': 'application/json' }
  const id = `${method}:${url}`
  return {
    id,
    method,
    path: url,
    url,
    summary,
    headersText: JSON.stringify(headers, null, 2),
    bodyText: body ? JSON.stringify(body, null, 2) : '',
    enabled: true,
    status: 'idle',
    lastTime: null as null | number,
    loading: false,
    responseText: '',
    expand: false
  }
}

function initPresets() {
  const items: any[] = []
  // 认证
  items.push(makePreset('POST', '/auth/login', '用户登录', { username: 'admin', password: '123456' }))
  items.push(makePreset('GET', '/auth/info', '当前用户信息'))
  // 用户
  items.push(makePreset('GET', '/user/list', '用户列表分页', { page: 1, limit: 20 }))
  // 订单
  items.push(makePreset('GET', '/order/list', '订单列表分页', { page: 1, limit: 20 }))
  items.push(makePreset('GET', '/order/test', '订单服务健康'))
  items.push(makePreset('POST', '/order', '创建订单', { orderNo: 'TEST1001', cashier: 'Tom', items: [] }))
  // 商品/库存
  items.push(makePreset('GET', '/product/list', '商品列表'))
  items.push(makePreset('GET', '/inventory/summary', '库存概览'))
  // 其它
  items.push(makePreset('GET', '/test/health', '系统健康检查'))
  presetApiItems.value = items
}

async function loadOpenapi() {
  try {
    const res = await request({ url: openapiUrl.value, method: 'get' })
    if (res) {
      const first = typeof res === 'string' ? JSON.parse(res) : res
      // 兼容 swagger-config：含 urls 时，取第一个文档再请求一次
      if (first && Array.isArray(first.urls) && first.urls.length > 0 && first.urls[0].url) {
        const docUrl = first.urls[0].url as string
        const finalUrl = docUrl.startsWith('http') ? docUrl : (docUrl.startsWith('/') ? docUrl : `/${docUrl}`)
        console.debug('[API TEST] Detected swagger-config, fetching doc:', finalUrl)
        const res2 = await request({ url: finalUrl, method: 'get' })
        const doc2 = typeof res2 === 'string' ? JSON.parse(res2) : res2
        openapiDoc.value = doc2
        return parseOpenapiToItems(doc2)
      }

      const doc = first
      openapiDoc.value = doc
      return parseOpenapiToItems(doc)
    }
  } catch (e: any) {
    ElMessage.error(e.message || '加载失败')
  }
}

function parseOpenapiToItems(doc: any) {
  // 解析 paths，生成可测试列表
  const items: any[] = []
  const paths = doc?.paths || {}
  console.debug('[API TEST] openapi keys:', Object.keys(doc || {}))
  console.debug('[API TEST] paths keys:', Object.keys(paths || {}))
  Object.entries(paths).forEach(([p, item]: any) => {
    const methods = ['get', 'post', 'put', 'delete', 'patch'] as const
    methods.forEach((m) => {
      const op = item?.[m]
      if (!op) return
      const method = m.toUpperCase()
      const summary = op.summary || ''
      // 默认 headers/body
      const headers = { 'Content-Type': 'application/json' }
      const schema = op.requestBody?.content?.['application/json']?.schema
      const body = method === 'GET' ? undefined : buildSample(schema)
      const url = buildUrl(p, op)
const id = `${method}:${url}`
      items.push({
        id,
        method,
        path: p,
        url,
        summary,
        tags: Array.isArray(op.tags) ? op.tags : [],
        enabled: true,
        status: 'idle',
        lastTime: null as any,
        headersText: JSON.stringify(headers, null, 2),
        bodyText: body === undefined ? '' : JSON.stringify(body, null, 2),
        loading: false,
        responseText: '',
        expand: false
      })
    })
  })
  // 按路径排序，GET 优先
  items.sort((a, b) => a.path.localeCompare(b.path) || a.method.localeCompare(b.method))
  apiItems.value = items
  if (items.length === 0) {
    ElMessage.warning('文档已加载，但未解析到任何路径（paths）。请确认后端控制器已被 SpringDoc 扫描。')
  } else {
    ElMessage.success(`文档已加载，共 ${items.length} 个接口`)
  }
}

const methods = ['GET', 'POST', 'PUT', 'DELETE', 'PATCH']

function methodTagType(m: string) {
  const map: any = { GET: 'success', POST: 'warning', PUT: 'info', DELETE: 'danger', PATCH: '' }
  return map[m] || ''
}

// 构造示例数据
function buildSample(schema: any): any {
  if (!schema) return {}
  if (schema.example !== undefined) return schema.example
  const type = schema.type || (schema.$ref ? 'object' : undefined)
  if (schema.$ref && openapiDoc.value?.components?.schemas) {
    const refName = schema.$ref.split('/').pop()
    const target = openapiDoc.value.components.schemas[refName]
    return buildSample(target)
  }
  switch (type) {
    case 'string': return schema.enum ? schema.enum[0] : 'string'
    case 'integer':
    case 'number': return 1
    case 'boolean': return true
    case 'array': return [buildSample(schema.items)]
    case 'object':
      const obj: any = {}
      const props = schema.properties || {}
      Object.keys(props).forEach(k => { obj[k] = buildSample(props[k]) })
      return obj
    default: return {}
  }
}

// 根据 parameters 渲染 path 和 query 样例
function buildUrl(path: string, op: any): string {
  const params: any[] = op.parameters || []
  let url = path
  // path params
  params.filter(p => p.in === 'path').forEach(p => {
    const sample = buildParamSample(p)
    url = url.replace(new RegExp(`{${p.name}}`, 'g'), encodeURIComponent(String(sample)))
  })
  // query params
  const queries: string[] = []
  params.filter(p => p.in === 'query').forEach(p => {
    const sample = buildParamSample(p)
    if (sample !== undefined && sample !== null && sample !== '') {
      queries.push(`${encodeURIComponent(p.name)}=${encodeURIComponent(String(sample))}`)
    }
  })
  if (queries.length) url += (url.includes('?') ? '&' : '?') + queries.join('&')
  return url
}

function buildParamSample(p: any): any {
  const schema = p.schema || {}
  if (p.example !== undefined) return p.example
  if (schema.example !== undefined) return schema.example
  const type = schema.type
  switch (type) {
    case 'integer':
    case 'number': return 1
    case 'boolean': return true
    case 'string': return schema.enum ? schema.enum[0] : (p.name?.toLowerCase().includes('id') ? '1' : 'demo')
    default: return 'demo'
  }
}

// 发送单个接口
async function sendEndpoint(row: any) {
  try {
    row.loading = true
    let headers: any = {}
    let body: any = undefined
    try { headers = JSON.parse(row.headersText || '{}') } catch {}
    if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(row.method)) {
      try { body = JSON.parse(row.bodyText || '{}') } catch {}
    }
    const start = Date.now()
    const res = await request({
      url: row.url || row.path,
      method: row.method.toLowerCase() as any,
      headers,
      data: body,
      // 关键：不跳登录、返回完整响应
      skipAuthRedirect: true,
      bypassUnified: true,
      validateStatus: () => true
    } as any)
    row.lastTime = Date.now() - start
    // 展示完整响应（状态、头、体）
    const full = {
      status: res.status,
      statusText: res.statusText,
      headers: res.headers,
      data: res.data
    }
    row.responseText = JSON.stringify(full, null, 2)
    row.status = res.status >= 200 && res.status < 300 ? 'success' : 'error'
  } catch (e: any) {
    row.responseText = `接口异常，请检查后端服务。\n错误: ${e?.message || '请求失败'}`
    row.status = 'error'
  } finally {
    row.loading = false
  }
}

async function batchTest() {
  try {
    batchLoading.value = true
    const list = displayItems.value.filter((it: any) => it.enabled)
    for (const it of list) {
      // 顺序触发，避免压垮后端；如需并发可 Promise.all
      // 清空上次结果
      it.responseText = ''
      await sendEndpoint(it)
    }
    ElMessage.success('批量测试完成')
  } catch (e) {
    ElMessage.error('批量测试失败')
  } finally {
    batchLoading.value = false
  }
}

// 页面挂载时初始化，并在首次进入“运行概览”时自动刷新
const hasCheckedOnce = ref(false)
onMounted(() => {
  initPresets()
  if (activeTab.value === 'overview') {
    hasCheckedOnce.value = true
    checkAllStatus()
  }
})

// 当切换到“运行概览”页签时，若当前未在加载，则触发刷新（首进已处理）
watch(activeTab, (val) => {
  if (val === 'overview' && !loading.value) {
    // 若尚未执行过，或用户主动切回概览时，执行刷新
    if (!hasCheckedOnce.value) {
      hasCheckedOnce.value = true
    }
    checkAllStatus()
  }
})
</script>

<style scoped>
.system-status-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.status-card {
  transition: all 0.3s ease;
}

.status-card.success {
  border-left: 4px solid #67c23a;
}

.status-card.error {
  border-left: 4px solid #f56c6c;
}

.status-card.loading {
  border-left: 4px solid #409eff;
}

.status-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.status-icon {
  font-size: 32px;
  margin-right: 16px;
}

.status-card.success .status-icon {
  color: #67c23a;
}

.status-card.error .status-icon {
  color: #f56c6c;
}

.status-card.loading .status-icon {
  color: #409eff;
}

.status-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.status-info p {
  margin: 0 0 8px 0;
  color: #606266;
  font-size: 14px;
}

.status-details {
  display: flex;
  gap: 16px;
}

.status-details span {
  font-size: 12px;
  color: #909399;
  background: #f5f7fa;
  padding: 2px 8px;
  border-radius: 4px;
}

.system-info {
  margin-top: 20px;
}

.row-editor {
  background: #fafafa;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-top: none;
  margin-bottom: 12px;
}
</style>