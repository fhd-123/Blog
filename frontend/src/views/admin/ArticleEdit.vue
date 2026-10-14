<template>
  <div>
    <h2 style="font-family:var(--serif);margin-bottom:16px;">{{ isEdit ? '编辑文章' : '新建文章' }}</h2>
    <el-card>
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" placeholder="文章标题" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" placeholder="列表展示用的摘要" /></el-form-item>
        <el-form-item label="封面"><el-input v-model="form.coverUrl" placeholder="封面图 URL（可选）" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width:240px;">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="form.tagIds" multiple placeholder="选择标签" style="width:100%;">
            <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">发布</el-radio>
            <el-radio :label="0">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="12" placeholder="支持 HTML 片段，例如 <p>段落</p>" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">保存</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  adminGetArticle, adminCreateArticle, adminUpdateArticle,
  adminGetCategories, adminGetTags
} from '../../api/blog'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const categories = ref([])
const tags = ref([])
const form = ref({ title: '', summary: '', coverUrl: '', categoryId: null, tagIds: [], status: 1, content: '' })

const goBack = () => router.push('/admin/articles')

const loadEdit = async () => {
  // 直接走管理端详情接口，可靠拿到完整文章（含内容/标签）
  const found = await adminGetArticle(route.params.id)
  if (found) {
    form.value = {
      title: found.title, summary: found.summary, coverUrl: found.coverUrl,
      categoryId: found.categoryId, tagIds: (found.tags || []).map(t => t.id),
      status: found.status, content: found.content
    }
  }
}

const save = async () => {
  if (!form.value.title || !form.value.content) { ElMessage.warning('标题与内容必填'); return }
  const payload = { ...form.value }
  if (isEdit.value) payload.id = Number(route.params.id)
  try {
    if (isEdit.value) await adminUpdateArticle(payload)
    else await adminCreateArticle(payload)
    ElMessage.success('保存成功')
    router.push('/admin/articles')
  } catch (e) {}
}

onMounted(async () => {
  categories.value = await adminGetCategories().catch(() => [])
  tags.value = await adminGetTags().catch(() => [])
  if (isEdit.value) loadEdit()
})
</script>
