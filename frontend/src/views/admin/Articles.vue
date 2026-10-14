<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h2 style="font-family:var(--serif);">文章管理</h2>
      <el-button type="primary" @click="goCreate">+ 新建文章</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="220" />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '发布' : '草稿' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="views" label="阅读" width="80" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="goEdit(row.id)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="del(row.id)">
            <template #reference><el-button size="small" type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-if="total > pageSize" layout="prev, pager, next" :total="total" :page-size="pageSize"
      :current-page="page" @current-change="(p) => { page = p; load() }"
      background style="margin-top:16px;" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminGetArticles, adminDeleteArticle } from '../../api/blog'

const router = useRouter()
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const loading = ref(false)

const load = async () => {
  loading.value = true
  try {
    const res = await adminGetArticles({ page: page.value, pageSize: pageSize.value })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e) {} finally { loading.value = false }
}
const goCreate = () => router.push('/admin/articles/create')
const goEdit = (id) => router.push('/admin/articles/edit/' + id)
const del = async (id) => {
  try { await adminDeleteArticle(id); ElMessage.success('已删除'); load() } catch (e) {}
}

onMounted(load)
</script>
