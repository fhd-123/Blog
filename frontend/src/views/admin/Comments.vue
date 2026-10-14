<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h2 style="font-family:var(--serif);">评论管理</h2>
      <el-radio-group v-model="status" @change="load">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="1">已通过</el-radio-button>
        <el-radio-button :label="0">待审核</el-radio-button>
      </el-radio-group>
    </div>
    <el-table :data="list" border v-loading="loading">
      <el-table-column prop="articleId" label="文章ID" width="80" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="content" label="内容" min-width="220" />
      <el-table-column prop="createTime" label="时间" width="170" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'warning'">{{ row.status === 1 ? '通过' : '待审' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status !== 1" size="small" type="success" @click="setStatus(row.id, 1)">通过</el-button>
          <el-button v-if="row.status === 1" size="small" @click="setStatus(row.id, 0)">隐藏</el-button>
          <el-popconfirm title="确定删除？" @confirm="del(row.id)">
            <template #reference><el-button size="small" type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-if="total > pageSize" layout="prev, pager, next" :total="total" :page-size="pageSize"
      :current-page="page" @current-change="(p) => { page = p; load() }" background style="margin-top:16px;" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminGetComments, adminSetCommentStatus, adminDeleteComment } from '../../api/blog'

const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const status = ref(null)

const load = async () => {
  loading.value = true
  try {
    const res = await adminGetComments({ page: page.value, pageSize: pageSize.value, status: status.value })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e) {} finally { loading.value = false }
}
const setStatus = async (id, s) => { try { await adminSetCommentStatus(id, s); ElMessage.success('已更新'); load() } catch (e) {} }
const del = async (id) => { try { await adminDeleteComment(id); ElMessage.success('已删除'); load() } catch (e) {} }

onMounted(load)
</script>
