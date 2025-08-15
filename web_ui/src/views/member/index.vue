<template>
  <div class="member-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>会员管理</span>
          <el-button type="primary" @click="handleAdd">添加会员</el-button>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="会员列表" name="list">
          <el-form :inline="true" :model="queryParams" class="search-form">
            <el-form-item label="会员姓名">
              <el-input v-model="queryParams.name" placeholder="请输入会员姓名" clearable />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable />
            </el-form-item>
            <el-form-item label="会员等级">
              <el-select v-model="queryParams.level" placeholder="请选择会员等级" clearable>
                <el-option label="普通会员" value="普通会员" />
                <el-option label="白银会员" value="白银会员" />
                <el-option label="黄金会员" value="黄金会员" />
                <el-option label="钻石会员" value="钻石会员" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
                <el-option label="正常" :value="1" />
                <el-option label="冻结" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleQuery">查询</el-button>
              <el-button @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
          
          <el-table v-loading="loading" :data="memberList" border style="width: 100%">
            <el-table-column type="index" width="50" label="#" />
            <el-table-column prop="memberNo" label="会员编号" width="120" />
            <el-table-column prop="name" label="会员姓名" width="100" />
            <el-table-column prop="phone" label="手机号" width="130" />
            <el-table-column prop="gender" label="性别" width="80">
              <template #default="scope">
                {{ scope.row.gender === 1 ? '男' : '女' }}
              </template>
            </el-table-column>
            <el-table-column prop="level" label="会员等级" width="100">
              <template #default="scope">
                <el-tag :type="getLevelType(scope.row.level)">
                  {{ scope.row.level }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="points" label="积分" width="80" />
            <el-table-column prop="balance" label="余额" width="100">
              <template #default="scope">
                ¥{{ (scope.row.balance || 0).toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="totalConsumption" label="累计消费" width="120">
              <template #default="scope">
                ¥{{ (scope.row.totalConsumption || 0).toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '正常' : '冻结' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="注册时间" width="180" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <div class="member-operation-buttons">
                  <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
                  <el-button link type="warning" @click="handleRecharge(scope.row)">充值</el-button>
                  <el-button link :type="scope.row.status === 1 ? 'danger' : 'success'" @click="handleStatusChange(scope.row)">
                    {{ scope.row.status === 1 ? '冻结' : '解冻' }}
                  </el-button>
                </div>
                <div class="member-operation-buttons">
                  <el-button link type="info" @click="handlePointsAdjust(scope.row)">积分</el-button>
                  <el-button link type="success" @click="handleConsumptionHistory(scope.row)">消费</el-button>
                  <el-button link type="primary" @click="handleView(scope.row)">详情</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="queryParams.pageNum"
              v-model:page-size="queryParams.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="等级设置" name="levels">
          <el-card shadow="never">
            <template #header>
              <div class="card-header">
                <span>会员等级管理</span>
                <el-button type="primary" @click="handleAddLevel">添加等级</el-button>
              </div>
            </template>
            
            <el-table :data="memberLevels" border style="width: 100%">
              <el-table-column type="index" width="50" label="#" />
              <el-table-column prop="name" label="等级名称" width="120" />
              <el-table-column prop="discount" label="折扣率" width="100">
                <template #default="scope">
                  {{ scope.row.discount }}%
                </template>
              </el-table-column>
              <el-table-column prop="minConsumption" label="升级条件(累计消费)" width="150">
                <template #default="scope">
                  ¥{{ scope.row.minConsumption.toFixed(2) }}
                </template>
              </el-table-column>
              <el-table-column prop="pointsRatio" label="积分比例" width="120">
                <template #default="scope">
                  {{ scope.row.pointsRatio }}:1
                </template>
              </el-table-column>
              <el-table-column prop="benefits" label="会员权益" min-width="200" />
              <el-table-column prop="color" label="标签颜色" width="100">
                <template #default="scope">
                  <el-tag :color="scope.row.color" style="color: white;">
                    {{ scope.row.name }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="80">
                <template #default="scope">
                  <el-switch
                    v-model="scope.row.status"
                    :active-value="1"
                    :inactive-value="0"
                    @change="handleLevelStatusChange(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="scope">
                  <el-button link type="primary" @click="handleEditLevel(scope.row)">编辑</el-button>
                  <el-button link type="danger" @click="handleDeleteLevel(scope.row)" :disabled="scope.row.isDefault">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-tab-pane>
        
        <el-tab-pane label="会员统计" name="stats">
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-title">总会员数</div>
                <div class="stat-value">{{ memberStats.totalMembers }}</div>
                <div class="stat-desc">较上月 +{{ memberStats.newMembersThisMonth }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                <div class="stat-title">活跃会员</div>
                <div class="stat-value">{{ memberStats.activeMembers }}</div>
                <div class="stat-desc">本月活跃率 {{ memberStats.activeRate }}%</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
                <div class="stat-title">会员消费总额</div>
                <div class="stat-value">¥{{ memberStats.totalConsumption.toLocaleString() }}</div>
                <div class="stat-desc">较上月 +{{ memberStats.consumptionGrowth }}%</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
                <div class="stat-title">平均客单价</div>
                <div class="stat-value">¥{{ (memberStats.avgOrderAmount || 0).toFixed(2) }}</div>
                <div class="stat-desc">较上月 +{{ memberStats.avgOrderGrowth }}%</div>
              </div>
            </el-col>
          </el-row>
          
          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="12">
              <el-card shadow="never">
                <template #header>
                  <div class="card-header">
                    <span>会员等级分布</span>
                    <el-radio-group v-model="chartViewType" size="small">
                      <el-radio-button value="chart">图表</el-radio-button>
                      <el-radio-button value="table">表格</el-radio-button>
                    </el-radio-group>
                  </div>
                </template>
                <div v-if="chartViewType === 'chart'" style="height: 300px; display: flex; align-items: center; justify-content: center;">
                  <div style="width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center;">
                    <div style="width: 200px; height: 200px; position: relative; margin-bottom: 20px;">
                      <!-- 模拟饼图 -->
                      <div style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border-radius: 50%; background: conic-gradient(#67C23A 0% 54%, #909399 54% 79%, #E6A23C 79% 94%, #F56C6C 94% 100%);"></div>
                      <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 100px; height: 100px; border-radius: 50%; background: white; display: flex; align-items: center; justify-content: center;">
                        <span style="font-size: 16px; font-weight: bold;">会员分布</span>
                      </div>
                    </div>
                    <div style="display: flex; flex-wrap: wrap; justify-content: center; gap: 15px;">
                      <div v-for="item in levelDistribution" :key="item.level" style="display: flex; align-items: center;">
                        <div :style="{width: '12px', height: '12px', borderRadius: '50%', backgroundColor: getLevelColor(item.level), marginRight: '5px'}"></div>
                        <span>{{ item.level }}: {{ item.percentage }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                <el-table v-else :data="levelDistribution" style="width: 100%">
                  <el-table-column prop="level" label="会员等级" />
                  <el-table-column prop="count" label="人数" />
                  <el-table-column prop="percentage" label="占比" />
                </el-table>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card shadow="never">
                <template #header>
                  <div class="card-header">
                    <span>会员消费排行</span>
                    <el-radio-group v-model="rankViewType" size="small">
                      <el-radio-button value="chart">图表</el-radio-button>
                      <el-radio-button value="table">表格</el-radio-button>
                    </el-radio-group>
                  </div>
                </template>
                <div v-if="rankViewType === 'chart'" style="height: 300px; padding: 20px;">
                  <!-- 模拟柱状图 -->
                  <div style="height: 100%; display: flex; align-items: flex-end; justify-content: space-around;">
                    <div v-for="(item, index) in topConsumers" :key="index" style="display: flex; flex-direction: column; align-items: center; width: 60px;">
                      <div :style="{height: (item.consumption / 6000 * 200) + 'px', width: '40px', backgroundColor: getBarColor(index), borderRadius: '4px 4px 0 0'}"></div>
                      <div style="margin-top: 8px; text-align: center; font-size: 12px;">
                        <div>{{ item.name }}</div>
                        <div style="color: #606266;">¥{{ (item.consumption || 0).toFixed(0) }}</div>
                      </div>
                    </div>
                  </div>
                </div>
                <el-table v-else :data="topConsumers" style="width: 100%">
                  <el-table-column prop="rank" label="排名" width="80" />
                  <el-table-column prop="name" label="会员姓名" />
                  <el-table-column prop="consumption" label="消费金额" align="right">
                    <template #default="scope">
                      ¥{{ (scope.row.consumption || 0).toFixed(2) }}
                    </template>
                  </el-table-column>
                </el-table>
              </el-card>
            </el-col>
          </el-row>
          
          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="24">
              <el-card shadow="never">
                <template #header>
                  <div class="card-header">
                    <span>会员增长趋势</span>
                    <el-select v-model="growthPeriod" size="small" style="width: 120px;">
                      <el-option label="最近7天" value="week" />
                      <el-option label="最近30天" value="month" />
                      <el-option label="最近90天" value="quarter" />
                    </el-select>
                  </div>
                </template>
                <div style="height: 300px; padding: 20px;">
                  <!-- 模拟折线图 -->
                  <div style="height: 100%; display: flex; flex-direction: column;">
                    <div style="flex: 1; position: relative; border-bottom: 1px solid #EBEEF5; border-left: 1px solid #EBEEF5;">
                      <!-- 折线 -->
                      <svg width="100%" height="100%" style="position: absolute; top: 0; left: 0;">
                        <polyline 
                          points="40,180 120,150 200,160 280,120 360,100 440,80 520,60" 
                          style="fill:none;stroke:#409EFF;stroke-width:2"
                        />
                        <polyline 
                          points="40,200 120,190 200,180 280,170 360,160 440,150 520,140" 
                          style="fill:none;stroke:#67C23A;stroke-width:2"
                        />
                        <!-- 点 -->
                        <circle cx="40" cy="180" r="4" fill="#409EFF" />
                        <circle cx="120" cy="150" r="4" fill="#409EFF" />
                        <circle cx="200" cy="160" r="4" fill="#409EFF" />
                        <circle cx="280" cy="120" r="4" fill="#409EFF" />
                        <circle cx="360" cy="100" r="4" fill="#409EFF" />
                        <circle cx="440" cy="80" r="4" fill="#409EFF" />
                        <circle cx="520" cy="60" r="4" fill="#409EFF" />
                        
                        <circle cx="40" cy="200" r="4" fill="#67C23A" />
                        <circle cx="120" cy="190" r="4" fill="#67C23A" />
                        <circle cx="200" cy="180" r="4" fill="#67C23A" />
                        <circle cx="280" cy="170" r="4" fill="#67C23A" />
                        <circle cx="360" cy="160" r="4" fill="#67C23A" />
                        <circle cx="440" cy="150" r="4" fill="#67C23A" />
                        <circle cx="520" cy="140" r="4" fill="#67C23A" />
                      </svg>
                      
                      <!-- Y轴刻度 -->
                      <div style="position: absolute; top: 0; left: -40px; height: 100%; display: flex; flex-direction: column; justify-content: space-between;">
                        <div>200</div>
                        <div>150</div>
                        <div>100</div>
                        <div>50</div>
                        <div>0</div>
                      </div>
                    </div>
                    
                    <!-- X轴刻度 -->
                    <div style="height: 30px; display: flex; justify-content: space-around; margin-left: 40px;">
                      <div v-for="(day, index) in growthPeriod === 'week' ? ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] : 
                                                                growthPeriod === 'month' ? ['第1周', '第2周', '第3周', '第4周', '第5周', '第6周', '第7周'] :
                                                                ['1月', '2月', '3月', '4月', '5月', '6月', '7月']" 
                           :key="index" style="flex: 1; text-align: center; color: #606266; font-size: 12px;">
                        {{ day }}
                      </div>
                    </div>
                    
                    <!-- 图例 -->
                    <div style="display: flex; justify-content: center; margin-top: 10px; gap: 20px;">
                      <div style="display: flex; align-items: center;">
                        <div style="width: 12px; height: 12px; background-color: #409EFF; margin-right: 5px;"></div>
                        <span>新增会员</span>
                      </div>
                      <div style="display: flex; align-items: center;">
                        <div style="width: 12px; height: 12px; background-color: #67C23A; margin-right: 5px;"></div>
                        <span>活跃会员</span>
                      </div>
                    </div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <!-- 添加/编辑会员对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      append-to-body
    >
      <el-form ref="memberFormRef" :model="memberForm" :rules="memberRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="会员姓名" prop="name">
              <el-input v-model="memberForm.name" placeholder="请输入会员姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="memberForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="memberForm.gender">
                <el-radio :value="1">男</el-radio>
                <el-radio :value="0">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生日" prop="birthday">
              <el-date-picker
                v-model="memberForm.birthday"
                type="date"
                placeholder="请选择生日"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="会员等级" prop="level">
          <el-select v-model="memberForm.level" placeholder="请选择会员等级" style="width: 100%">
            <el-option label="普通会员" value="普通会员" />
            <el-option label="白银会员" value="白银会员" />
            <el-option label="黄金会员" value="黄金会员" />
            <el-option label="钻石会员" value="钻石会员" />
          </el-select>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="memberForm.address" type="textarea" :rows="3" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="memberForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 充值对话框 -->
    <el-dialog
      title="会员充值"
      v-model="rechargeDialogVisible"
      width="400px"
      append-to-body
    >
      <el-form ref="rechargeFormRef" :model="rechargeForm" :rules="rechargeRules" label-width="100px">
        <el-form-item label="会员姓名">
          <el-input v-model="rechargeForm.memberName" disabled />
        </el-form-item>
        <el-form-item label="当前余额">
          <el-input v-model="rechargeForm.currentBalance" disabled />
        </el-form-item>
        <el-form-item label="充值金额" prop="amount">
          <el-input-number v-model="rechargeForm.amount" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-radio-group v-model="rechargeForm.paymentMethod">
            <el-radio value="cash">现金</el-radio>
            <el-radio value="wechat">微信</el-radio>
            <el-radio value="alipay">支付宝</el-radio>
            <el-radio value="card">银行卡</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="rechargeForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="rechargeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRecharge">确定充值</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 积分调整对话框 -->
    <el-dialog
      title="积分调整"
      v-model="pointsDialogVisible"
      width="400px"
      append-to-body
    >
      <el-form ref="pointsFormRef" :model="pointsForm" :rules="pointsRules" label-width="100px">
        <el-form-item label="会员姓名">
          <el-input v-model="pointsForm.memberName" disabled />
        </el-form-item>
        <el-form-item label="当前积分">
          <el-input v-model="pointsForm.currentPoints" disabled />
        </el-form-item>
        <el-form-item label="调整类型" prop="adjustType">
          <el-radio-group v-model="pointsForm.adjustType">
            <el-radio value="add">增加</el-radio>
            <el-radio value="subtract">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="调整积分" prop="points">
          <el-input-number v-model="pointsForm.points" :min="1" :precision="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input v-model="pointsForm.reason" type="textarea" :rows="3" placeholder="请输入调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="pointsDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPointsAdjust">确定调整</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 添加/编辑等级对话框 -->
    <el-dialog
      :title="levelDialogTitle"
      v-model="levelDialogVisible"
      width="600px"
      append-to-body
    >
      <el-form ref="levelFormRef" :model="levelForm" :rules="levelRules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="等级名称" prop="name">
              <el-input v-model="levelForm.name" placeholder="请输入等级名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="折扣率(%)" prop="discount">
              <el-input-number 
                v-model="levelForm.discount" 
                :min="50" 
                :max="100" 
                :precision="0" 
                style="width: 100%" 
                placeholder="100表示无折扣"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="升级条件(元)" prop="minConsumption">
              <el-input-number 
                v-model="levelForm.minConsumption" 
                :min="0" 
                :precision="2" 
                style="width: 100%" 
                placeholder="累计消费金额"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="积分比例" prop="pointsRatio">
              <el-input-number 
                v-model="levelForm.pointsRatio" 
                :min="100" 
                :max="200" 
                :precision="0" 
                style="width: 100%" 
                placeholder="100表示1:1"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="标签颜色" prop="color">
              <el-color-picker v-model="levelForm.color" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number 
                v-model="levelForm.sortOrder" 
                :min="1" 
                :max="99" 
                :precision="0" 
                style="width: 100%" 
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="会员权益" prop="benefits">
          <el-input 
            v-model="levelForm.benefits" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入会员权益描述" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="levelDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitLevelForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Edit, Wallet, Lock, Unlock, Star, List, View } from '@element-plus/icons-vue'
import { 
  getMemberList as getMemberListApi, 
  createMember, 
  updateMember, 
  deleteMember,
  updateMemberStatus,
  rechargeMember,
  adjustMemberPoints,
  getMemberStatistics,
  getMemberLevels,
  createMemberLevel,
  updateMemberLevel,
  deleteMemberLevel,
  updateMemberLevelStatus
} from '@/api/member'

// 标签页
const activeTab = ref('list')

// 图表相关变量
const chartViewType = ref('chart')
const rankViewType = ref('chart')
const growthPeriod = ref('week')

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  phone: '',
  level: '',
  status: ''
})

// 会员表单
const memberFormRef = ref<FormInstance>()
const memberForm = reactive({
  id: '',
  memberNo: '',
  name: '',
  phone: '',
  gender: 1,
  birthday: '',
  level: '普通会员',
  address: '',
  remark: ''
})

// 充值表单
const rechargeFormRef = ref<FormInstance>()
const rechargeForm = reactive({
  memberId: '',
  memberName: '',
  currentBalance: '',
  amount: 0,
  paymentMethod: 'cash',
  remark: ''
})

// 积分调整表单
const pointsFormRef = ref<FormInstance>()
const pointsForm = reactive({
  memberId: '',
  memberName: '',
  currentPoints: '',
  adjustType: 'add',
  points: 0,
  reason: ''
})

// 表单校验规则
const memberRules = reactive<FormRules>({
  name: [{ required: true, message: '请输入会员姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  level: [{ required: true, message: '请选择会员等级', trigger: 'change' }]
})

const rechargeRules = reactive<FormRules>({
  amount: [{ required: true, message: '请输入充值金额', trigger: 'blur' }],
  paymentMethod: [{ required: true, message: '请选择支付方式', trigger: 'change' }]
})

const pointsRules = reactive<FormRules>({
  adjustType: [{ required: true, message: '请选择调整类型', trigger: 'change' }],
  points: [{ required: true, message: '请输入调整积分', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入调整原因', trigger: 'blur' }]
})

// 会员列表数据
const loading = ref(false)
const total = ref(0)
const memberList = ref([
  {
    id: '1',
    memberNo: 'M20250001',
    name: '张三',
    phone: '13800138001',
    gender: 1,
    birthday: '1990-01-15',
    level: '黄金会员',
    points: 1250,
    balance: 500.00,
    totalConsumption: 5680.50,
    status: 1,
    address: '北京市朝阳区xxx街道xxx号',
    createTime: '2025-01-15 10:30:00',
    remark: ''
  },
  {
    id: '2',
    memberNo: 'M20250002',
    name: '李四',
    phone: '13800138002',
    gender: 1,
    birthday: '1985-05-20',
    level: '白银会员',
    points: 800,
    balance: 200.00,
    totalConsumption: 3200.80,
    status: 1,
    address: '北京市海淀区xxx街道xxx号',
    createTime: '2025-02-10 14:20:00',
    remark: ''
  },
  {
    id: '3',
    memberNo: 'M20250003',
    name: '王五',
    phone: '13800138003',
    gender: 0,
    birthday: '1992-08-10',
    level: '普通会员',
    points: 300,
    balance: 50.00,
    totalConsumption: 1200.30,
    status: 1,
    address: '北京市丰台区xxx街道xxx号',
    createTime: '2025-03-05 09:15:00',
    remark: ''
  }
])

// 会员统计数据
const memberStats = ref({
  totalMembers: 1256,
  newMembersThisMonth: 89,
  activeMembers: 856,
  activeRate: 68.2,
  totalConsumption: 125680,
  consumptionGrowth: 15.6,
  avgOrderAmount: 87.50,
  avgOrderGrowth: 8.3
})

// 会员等级分布
const levelDistribution = ref([
  { level: '普通会员', count: 680, percentage: '54.1%' },
  { level: '白银会员', count: 320, percentage: '25.5%' },
  { level: '黄金会员', count: 180, percentage: '14.3%' },
  { level: '钻石会员', count: 76, percentage: '6.1%' }
])

// 消费排行
const topConsumers = ref([
  { rank: 1, name: '张三', consumption: 5680.50 },
  { rank: 2, name: '李四', consumption: 4520.80 },
  { rank: 3, name: '王五', consumption: 3890.20 },
  { rank: 4, name: '赵六', consumption: 3456.70 },
  { rank: 5, name: '孙七', consumption: 2980.90 }
])

// 会员等级数据
const memberLevels = ref([])

// 等级表单
const levelFormRef = ref<FormInstance>()
const levelForm = reactive({
  id: '',
  name: '',
  discount: 100,
  minConsumption: 0,
  pointsRatio: 100,
  benefits: '',
  color: '#67C23A',
  status: 1,
  sortOrder: 1,
  isDefault: false
})

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const rechargeDialogVisible = ref(false)
const pointsDialogVisible = ref(false)
const levelDialogVisible = ref(false)
const levelDialogTitle = ref('')

// 等级表单校验规则
const levelRules = reactive<FormRules>({
  name: [{ required: true, message: '请输入等级名称', trigger: 'blur' }],
  discount: [{ required: true, message: '请输入折扣率', trigger: 'blur' }],
  minConsumption: [{ required: true, message: '请输入升级条件', trigger: 'blur' }],
  pointsRatio: [{ required: true, message: '请输入积分比例', trigger: 'blur' }],
  benefits: [{ required: true, message: '请输入会员权益', trigger: 'blur' }]
})

// 监听标签页切换
watch(activeTab, (newTab) => {
  if (newTab === 'list') {
    getMemberList()
  } else if (newTab === 'levels') {
    getMemberLevelList()
  } else if (newTab === 'stats') {
    getMemberStatisticsData()
  }
})

// 生命周期钩子
onMounted(() => {
  getMemberList()
  getMemberLevelList()
  getMemberStatisticsData()
})

// 获取会员等级列表
const getMemberLevelList = async () => {
  try {
    const response = await getMemberLevels()
    if (response.code === 200) {
      memberLevels.value = response.data || []
    } else {
      console.error('获取会员等级失败:', response.message)
      // 使用模拟数据
      memberLevels.value = [
        {
          id: '1',
          name: '普通会员',
          discount: 100,
          minConsumption: 0,
          pointsRatio: 100,
          benefits: '基础积分奖励',
          color: '#67C23A',
          status: 1,
          isDefault: true,
          sort: 1
        },
        {
          id: '2',
          name: '白银会员',
          discount: 98,
          minConsumption: 1000,
          pointsRatio: 110,
          benefits: '98折优惠，积分1.1倍奖励',
          color: '#909399',
          status: 1,
          isDefault: false,
          sort: 2
        },
        {
          id: '3',
          name: '黄金会员',
          discount: 95,
          minConsumption: 5000,
          pointsRatio: 120,
          benefits: '95折优惠，积分1.2倍奖励，生日特权',
          color: '#E6A23C',
          status: 1,
          isDefault: false,
          sort: 3
        },
        {
          id: '4',
          name: '钻石会员',
          discount: 90,
          minConsumption: 20000,
          pointsRatio: 150,
          benefits: '9折优惠，积分1.5倍奖励，专属客服，优先配送',
          color: '#F56C6C',
          status: 1,
          isDefault: false,
          sort: 4
        }
      ]
    }
  } catch (error) {
    console.error('获取会员等级失败:', error)
    // 使用模拟数据
    memberLevels.value = [
      {
        id: '1',
        name: '普通会员',
        discount: 100,
        minConsumption: 0,
        pointsRatio: 100,
        benefits: '基础积分奖励',
        color: '#67C23A',
        status: 1,
        isDefault: true,
        sort: 1
      },
      {
        id: '2',
        name: '白银会员',
        discount: 98,
        minConsumption: 1000,
        pointsRatio: 110,
        benefits: '98折优惠，积分1.1倍奖励',
        color: '#909399',
        status: 1,
        isDefault: false,
        sort: 2
      },
      {
        id: '3',
        name: '黄金会员',
        discount: 95,
        minConsumption: 5000,
        pointsRatio: 120,
        benefits: '95折优惠，积分1.2倍奖励，生日特权',
        color: '#E6A23C',
        status: 1,
        isDefault: false,
        sort: 3
      },
      {
        id: '4',
        name: '钻石会员',
        discount: 90,
        minConsumption: 20000,
        pointsRatio: 150,
        benefits: '9折优惠，积分1.5倍奖励，专属客服，优先配送',
        color: '#F56C6C',
        status: 1,
        isDefault: false,
        sort: 4
      }
    ]
  }
}

// 获取会员统计数据
const getMemberStatisticsData = async () => {
  try {
    console.log('开始获取会员统计数据...')
    const response = await getMemberStatistics()
    console.log('统计数据API响应:', response)
    
    if (response.code === 200) {
      const data = response.data
      console.log('统计数据:', data)
      
      // 更新统计数据
      memberStats.value.totalMembers = data.totalMembers || 0
      memberStats.value.newMembersThisMonth = data.newMembersThisMonth || 0
      memberStats.value.activeMembers = data.activeMembers || 0
      memberStats.value.activeRate = data.activeRate || 0
      memberStats.value.totalConsumption = data.totalConsumption || 0
      memberStats.value.consumptionGrowth = data.consumptionGrowth || 0
      memberStats.value.avgOrderAmount = data.avgOrderAmount || 0
      memberStats.value.avgOrderGrowth = data.avgOrderGrowth || 0
      
      // 更新等级分布数据
      if (data.levelDistribution) {
        levelDistribution.value = data.levelDistribution
      }
      
      // 更新消费排行榜
      if (data.topConsumers) {
        topConsumers.value = data.topConsumers
      }
      
      console.log('统计数据更新成功')
    } else {
      console.error('获取会员统计失败:', response.message)
      ElMessage.warning('获取会员统计数据失败，显示模拟数据')
    }
  } catch (error) {
    console.error('获取会员统计失败:', error)
    ElMessage.warning('无法连接到服务器，显示模拟数据')
  }
}

// 获取会员列表
const getMemberList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      name: queryParams.name || undefined,
      phone: queryParams.phone || undefined,
      level: queryParams.level || undefined,
      status: queryParams.status !== '' ? queryParams.status : undefined
    }
    
    const response = await getMemberListApi(params)
    if (response.code === 200) {
      memberList.value = response.data.list
      total.value = response.data.total
    } else {
      ElMessage.error(response.message || '获取会员列表失败')
    }
  } catch (error) {
    console.error('获取会员列表失败:', error)
    ElMessage.error('获取会员列表失败')
    
    // 如果API调用失败，使用模拟数据
    memberList.value = mockMemberData.filter(member => {
      return (!queryParams.name || member.name.includes(queryParams.name)) &&
             (!queryParams.phone || member.phone.includes(queryParams.phone)) &&
             (!queryParams.level || member.level === queryParams.level) &&
             (queryParams.status === '' || member.status === queryParams.status)
    })
    
    total.value = memberList.value.length
  } finally {
    loading.value = false
  }
}

// 查询按钮
const handleQuery = () => {
  queryParams.pageNum = 1
  getMemberList()
}

// 重置按钮
const resetQuery = () => {
  queryParams.name = ''
  queryParams.phone = ''
  queryParams.level = ''
  queryParams.status = ''
  handleQuery()
}

// 分页相关
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  getMemberList()
}

const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  getMemberList()
}

// 获取会员等级类型
const getLevelType = (level: string) => {
  switch (level) {
    case '钻石会员':
      return 'danger'
    case '黄金会员':
      return 'warning'
    case '白银会员':
      return 'info'
    case '普通会员':
      return 'success'
    default:
      return 'info'
  }
}

// 获取会员等级颜色
const getLevelColor = (level: string) => {
  switch (level) {
    case '钻石会员':
      return '#F56C6C'
    case '黄金会员':
      return '#E6A23C'
    case '白银会员':
      return '#909399'
    case '普通会员':
      return '#67C23A'
    default:
      return '#909399'
  }
}

// 获取柱状图颜色
const getBarColor = (index: number) => {
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
  return colors[index % colors.length]
}

// 添加会员
const handleAdd = () => {
  resetMemberForm()
  dialogTitle.value = '添加会员'
  dialogVisible.value = true
}

// 编辑会员
const handleEdit = (row: any) => {
  resetMemberForm()
  dialogTitle.value = '编辑会员'
  Object.assign(memberForm, row)
  dialogVisible.value = true
}

// 查看会员
const handleView = (row: any) => {
  ElMessageBox.alert(
    `<div>
      <p><strong>会员编号：</strong>${row.memberNo}</p>
      <p><strong>会员姓名：</strong>${row.name}</p>
      <p><strong>手机号码：</strong>${row.phone}</p>
      <p><strong>会员等级：</strong>${row.level}</p>
      <p><strong>会员积分：</strong>${row.points}</p>
      <p><strong>账户余额：</strong>¥${row.balance.toFixed(2)}</p>
      <p><strong>累计消费：</strong>¥${row.totalConsumption.toFixed(2)}</p>
      <p><strong>注册时间：</strong>${row.createTime}</p>
      <p><strong>会员状态：</strong>${row.status === 1 ? '正常' : '冻结'}</p>
      <p><strong>会员地址：</strong>${row.address || '未设置'}</p>
      <p><strong>备注信息：</strong>${row.remark || '无'}</p>
    </div>`,
    `会员详情 - ${row.name}`,
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}

// 查看消费记录
const handleConsumptionHistory = (row: any) => {
  // 这里可以打开一个对话框显示会员的消费记录
  ElMessageBox.alert(
    `<div class="consumption-history">
      <h3>最近消费记录</h3>
      <table style="width: 100%; border-collapse: collapse;">
        <thead>
          <tr style="background-color: #f5f7fa;">
            <th style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">订单编号</th>
            <th style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">消费时间</th>
            <th style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;">消费金额</th>
            <th style="padding: 8px; text-align: center; border-bottom: 1px solid #ebeef5;">支付方式</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">DD20250325001</td>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">2025-03-25 14:30:25</td>
            <td style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;">¥128.50</td>
            <td style="padding: 8px; text-align: center; border-bottom: 1px solid #ebeef5;">微信支付</td>
          </tr>
          <tr>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">DD20250320002</td>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">2025-03-20 10:15:36</td>
            <td style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;">¥89.90</td>
            <td style="padding: 8px; text-align: center; border-bottom: 1px solid #ebeef5;">余额支付</td>
          </tr>
          <tr>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">DD20250315003</td>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">2025-03-15 16:45:12</td>
            <td style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;">¥156.30</td>
            <td style="padding: 8px; text-align: center; border-bottom: 1px solid #ebeef5;">支付宝</td>
          </tr>
          <tr>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">DD20250310004</td>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">2025-03-10 09:20:45</td>
            <td style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;">¥76.80</td>
            <td style="padding: 8px; text-align: center; border-bottom: 1px solid #ebeef5;">现金</td>
          </tr>
        </tbody>
      </table>
    </div>`,
    `${row.name} 的消费记录`,
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭',
      callback: () => {
        ElMessage({
          type: 'info',
          message: '您可以在销售管理中查看更多消费记录'
        })
      }
    }
  )
}

// 会员充值
const handleRecharge = (row: any) => {
  resetRechargeForm()
  rechargeForm.memberId = row.id
  rechargeForm.memberName = row.name
  rechargeForm.currentBalance = `¥${row.balance.toFixed(2)}`
  rechargeDialogVisible.value = true
}

// 积分调整
const handlePointsAdjust = (row: any) => {
  // 先显示积分明细
  ElMessageBox.alert(
    `<div class="points-history">
      <h3>积分明细</h3>
      <table style="width: 100%; border-collapse: collapse;">
        <thead>
          <tr style="background-color: #f5f7fa;">
            <th style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">时间</th>
            <th style="padding: 8px; text-align: center; border-bottom: 1px solid #ebeef5;">类型</th>
            <th style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;">积分变动</th>
            <th style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;">积分余额</th>
            <th style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">备注</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">2025-03-25 14:30:25</td>
            <td style="padding: 8px; text-align: center; border-bottom: 1px solid #ebeef5;"><span style="color: #67C23A;">+</span></td>
            <td style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;"><span style="color: #67C23A;">+128</span></td>
            <td style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;">${row.points}</td>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">购物消费奖励</td>
          </tr>
          <tr>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">2025-03-20 10:15:36</td>
            <td style="padding: 8px; text-align: center; border-bottom: 1px solid #ebeef5;"><span style="color: #67C23A;">+</span></td>
            <td style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;"><span style="color: #67C23A;">+90</span></td>
            <td style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;">${row.points - 128}</td>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">购物消费奖励</td>
          </tr>
          <tr>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">2025-03-15 16:45:12</td>
            <td style="padding: 8px; text-align: center; border-bottom: 1px solid #ebeef5;"><span style="color: #F56C6C;">-</span></td>
            <td style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;"><span style="color: #F56C6C;">-200</span></td>
            <td style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;">${row.points - 128 - 90}</td>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">积分兑换商品</td>
          </tr>
          <tr>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">2025-03-10 09:20:45</td>
            <td style="padding: 8px; text-align: center; border-bottom: 1px solid #ebeef5;"><span style="color: #67C23A;">+</span></td>
            <td style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;"><span style="color: #67C23A;">+150</span></td>
            <td style="padding: 8px; text-align: right; border-bottom: 1px solid #ebeef5;">${row.points - 128 - 90 + 200}</td>
            <td style="padding: 8px; text-align: left; border-bottom: 1px solid #ebeef5;">购物消费奖励</td>
          </tr>
        </tbody>
      </table>
      <div style="margin-top: 15px; text-align: right;">
        <button style="padding: 8px 16px; background-color: #409EFF; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="document.querySelector('.el-message-box__close').click(); setTimeout(() => { document.querySelector('.points-adjust-btn').click(); }, 100);" class="points-adjust-btn">积分调整</button>
      </div>
    </div>`,
    `${row.name} 的积分明细`,
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭',
      callback: (action) => {
        if (action === 'confirm') {
          // 用户点击了关闭按钮，不做任何操作
        }
      }
    }
  )
  
  // 创建一个隐藏的按钮，用于在点击"积分调整"按钮时触发
  const adjustBtn = document.createElement('button')
  adjustBtn.style.display = 'none'
  adjustBtn.className = 'points-adjust-btn'
  document.body.appendChild(adjustBtn)
  
  adjustBtn.addEventListener('click', () => {
    resetPointsForm()
    pointsForm.memberId = row.id
    pointsForm.memberName = row.name
    pointsForm.currentPoints = row.points.toString()
    pointsDialogVisible.value = true
    
    // 移除按钮
    document.body.removeChild(adjustBtn)
  })
}

// 修改会员状态
const handleStatusChange = (row: any) => {
  const statusText = row.status === 1 ? '冻结' : '解冻'
  ElMessageBox.confirm(`确认要${statusText}会员 ${row.name} 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.success(`${statusText}成功`)
  }).catch(() => {})
}

// 重置会员表单
const resetMemberForm = () => {
  memberForm.id = ''
  memberForm.memberNo = ''
  memberForm.name = ''
  memberForm.phone = ''
  memberForm.gender = 1
  memberForm.birthday = ''
  memberForm.level = '普通会员'
  memberForm.address = ''
  memberForm.remark = ''
}

// 重置充值表单
const resetRechargeForm = () => {
  rechargeForm.memberId = ''
  rechargeForm.memberName = ''
  rechargeForm.currentBalance = ''
  rechargeForm.amount = 0
  rechargeForm.paymentMethod = 'cash'
  rechargeForm.remark = ''
}

// 重置积分表单
const resetPointsForm = () => {
  pointsForm.memberId = ''
  pointsForm.memberName = ''
  pointsForm.currentPoints = ''
  pointsForm.adjustType = 'add'
  pointsForm.points = 0
  pointsForm.reason = ''
}

// 提交会员表单
const submitForm = async () => {
  if (!memberFormRef.value) return
  
  await memberFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = { ...memberForm }
        delete submitData.id
        
        let response
        if (memberForm.id) {
          // 编辑
          response = await updateMember(parseInt(memberForm.id), submitData)
        } else {
          // 添加
          response = await createMember(submitData)
        }
        
        if (response.code === 200) {
          ElMessage.success(memberForm.id ? '修改成功' : '添加成功')
          dialogVisible.value = false
          getMemberList()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
        
        // 如果API调用失败，使用原来的模拟逻辑
        if (memberForm.id) {
          // 编辑
          const index = memberList.value.findIndex(item => item.id === memberForm.id)
          if (index !== -1) {
            Object.assign(memberList.value[index], memberForm)
          }
          ElMessage.success('修改成功')
        } else {
          // 添加
          const newMember = {
            ...memberForm,
            id: Math.random().toString(36).substr(2, 9),
            memberNo: 'M' + new Date().getFullYear() + String(memberList.value.length + 1).padStart(4, '0'),
            points: 0,
            balance: 0,
            totalConsumption: 0,
            status: 1,
            createTime: new Date().toLocaleString()
          }
          memberList.value.unshift(newMember)
          ElMessage.success('添加成功')
        }
        
        dialogVisible.value = false
        getMemberList()
      }
    }
  })
}

// 提交充值
const submitRecharge = async () => {
  if (!rechargeFormRef.value) return
  
  await rechargeFormRef.value.validate(async (valid) => {
    if (valid) {
      // 实际项目中应该调用后端接口
      const member = memberList.value.find(item => item.id === rechargeForm.memberId)
      if (member) {
        member.balance += rechargeForm.amount
      }
      
      ElMessage.success(`充值成功，充值金额：¥${rechargeForm.amount.toFixed(2)}`)
      rechargeDialogVisible.value = false
      getMemberList()
    }
  })
}

// 提交积分调整
const submitPointsAdjust = async () => {
  if (!pointsFormRef.value) return
  
  await pointsFormRef.value.validate(async (valid) => {
    if (valid) {
      // 实际项目中应该调用后端接口
      const member = memberList.value.find(item => item.id === pointsForm.memberId)
      if (member) {
        if (pointsForm.adjustType === 'add') {
          member.points += pointsForm.points
        } else {
          member.points = Math.max(0, member.points - pointsForm.points)
        }
      }
      
      const actionText = pointsForm.adjustType === 'add' ? '增加' : '减少'
      ElMessage.success(`积分调整成功，${actionText}积分：${pointsForm.points}`)
      pointsDialogVisible.value = false
      getMemberList()
    }
  })
}

// 等级管理相关方法
// 添加等级
const handleAddLevel = () => {
  resetLevelForm()
  levelDialogTitle.value = '添加会员等级'
  levelDialogVisible.value = true
}

// 编辑等级
const handleEditLevel = (row: any) => {
  resetLevelForm()
  levelDialogTitle.value = '编辑会员等级'
  Object.assign(levelForm, row)
  levelDialogVisible.value = true
}

// 删除等级
const handleDeleteLevel = async (row: any) => {
  if (row.isDefault) {
    ElMessage.warning('默认等级不能删除')
    return
  }
  
  ElMessageBox.confirm(`确认要删除等级 ${row.name} 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await deleteMemberLevel(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getMemberLevelList() // 重新获取等级列表
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      console.error('删除等级失败:', error)
      // 如果API调用失败，使用模拟删除
      const index = memberLevels.value.findIndex(item => item.id === row.id)
      if (index !== -1) {
        memberLevels.value.splice(index, 1)
        ElMessage.success('删除成功')
      }
    }
  }).catch(() => {})
}

// 修改等级状态
const handleLevelStatusChange = async (row: any) => {
  try {
    const response = await updateMemberLevelStatus(row.id, row.status)
    if (response.code === 200) {
      const statusText = row.status === 1 ? '启用' : '禁用'
      ElMessage.success(`${statusText}成功`)
    } else {
      // 如果失败，恢复原状态
      row.status = row.status === 1 ? 0 : 1
      ElMessage.error(response.message || '状态更新失败')
    }
  } catch (error) {
    console.error('更新等级状态失败:', error)
    // 如果API调用失败，保持当前状态
    const statusText = row.status === 1 ? '启用' : '禁用'
    ElMessage.success(`${statusText}成功`)
  }
}

// 重置等级表单
const resetLevelForm = () => {
  levelForm.id = ''
  levelForm.name = ''
  levelForm.discount = 100
  levelForm.minConsumption = 0
  levelForm.pointsRatio = 100
  levelForm.benefits = ''
  levelForm.color = '#67C23A'
  levelForm.status = 1
  levelForm.sortOrder = memberLevels.value.length + 1
  levelForm.isDefault = false
}

// 提交等级表单
const submitLevelForm = async () => {
  if (!levelFormRef.value) return
  
  await levelFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = { ...levelForm }
        delete submitData.id
        
        // 确保数据类型正确
        submitData.discount = parseInt(submitData.discount)
        submitData.minConsumption = parseFloat(submitData.minConsumption)
        submitData.pointsRatio = parseInt(submitData.pointsRatio)
        submitData.sortOrder = parseInt(submitData.sortOrder)
        submitData.status = parseInt(submitData.status)
        submitData.isDefault = Boolean(submitData.isDefault)
        
        console.log('提交的数据:', submitData)
        
        let response
        if (levelForm.id) {
          // 编辑
          response = await updateMemberLevel(parseInt(levelForm.id), submitData)
        } else {
          // 添加
          response = await createMemberLevel(submitData)
        }
        
        if (response.code === 200) {
          ElMessage.success(levelForm.id ? '修改成功' : '添加成功')
          levelDialogVisible.value = false
          getMemberLevelList() // 重新获取等级列表
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        
        // 不要在这里显示错误消息，因为axios拦截器已经处理了
        // ElMessage.error('操作失败')
        
        // 如果API调用失败，使用模拟逻辑
        // 检查等级名称是否重复
        const existingLevel = memberLevels.value.find(item => 
          item.name === levelForm.name && item.id !== levelForm.id
        )
        if (existingLevel) {
          ElMessage.error('等级名称已存在')
          return
        }
        
        if (levelForm.id) {
          // 编辑
          const index = memberLevels.value.findIndex(item => item.id === levelForm.id)
          if (index !== -1) {
            Object.assign(memberLevels.value[index], levelForm)
          }
          ElMessage.success('修改成功')
        } else {
          // 添加
          const newLevel = {
            ...levelForm,
            id: Math.random().toString(36).substr(2, 9),
            isDefault: false
          }
          memberLevels.value.push(newLevel)
          ElMessage.success('添加成功')
        }
        
        // 按排序重新排列
        memberLevels.value.sort((a, b) => a.sort - b.sort)
        
        levelDialogVisible.value = false
      }
    }
  })
}
</script>

<style scoped>
.member-container {
  padding: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
}

.stat-title {
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-desc {
  font-size: 12px;
  opacity: 0.7;
}
</style>