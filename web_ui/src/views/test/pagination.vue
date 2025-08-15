<template>
  <div class="test-pagination">
    <h2>分页功能测试</h2>
    
    <!-- 模拟数据表格 -->
    <el-table :data="tableData" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="value" label="值" />
    </el-table>
    

  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'



// 模拟数据
const tableData = Array.from({ length: 100 }, (_, i) => ({
  id: i + 1,
  name: `项目 ${i + 1}`,
  value: `值 ${i + 1}`
}))


</script>

<style scoped>
.test-pagination {
  padding: 20px;
}


</style>