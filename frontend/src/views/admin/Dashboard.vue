<template>
  <div>
    <h2 style="font-family:var(--serif);margin-bottom:20px;">仪表盘</h2>
    <el-row :gutter="16">
      <el-col :span="6" v-for="c in cards" :key="c.label">
        <el-card class="stat-card"><div class="num">{{ c.value }}</div><div class="lbl">{{ c.label }}</div></el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top:20px;">
      <p>欢迎，<strong>{{ username }}</strong>！在这里管理你的博客文章、分类、友链与评论。</p>
      <p style="color:var(--text-soft);margin-top:8px;">前端运行在 <code>5173</code>，后端接口在 <code>8080/api</code>。数据来自 MySQL（blog 库）。</p>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { adminGetStats } from '../../api/blog'

const username = localStorage.getItem('username') || 'admin'
const stats = ref({})
const cards = ref([])

onMounted(async () => {
  try { stats.value = await adminGetStats() } catch (e) {}
  cards.value = [
    { label: '文章', value: stats.value.articleCount || 0 },
    { label: '评论', value: stats.value.commentCount || 0 },
    { label: '友链', value: stats.value.friendCount || 0 }
  ]
})
</script>

<style scoped>
.stat-card { text-align: center; }
.stat-card .num { font-size: 2rem; font-weight: 800; color: var(--accent); font-family: var(--serif); }
.stat-card .lbl { color: var(--text-soft); margin-top: 4px; }
</style>
