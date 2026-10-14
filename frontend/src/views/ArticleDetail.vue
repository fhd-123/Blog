<template>
  <div class="app-container">
    <div v-if="article">
      <router-link to="/timeline" class="widget-link back">← 返回文章列表</router-link>
      <article class="article-detail reveal">
        <span class="eyebrow">{{ article.categoryName || '未分类' }}</span>
        <h1>{{ article.title }}</h1>
        <div class="article-meta">
          {{ formatDate(article.createTime) }} · 阅读 {{ article.views }}
          <span v-if="article.tags && article.tags.length" class="tags" style="display:inline-flex;margin-left:12px;">
            <span v-for="t in article.tags" :key="t.id">{{ t.name }}</span>
          </span>
        </div>
        <div class="article-body" v-html="article.content"></div>
      </article>

      <div class="comment-box">
        <h3 class="section-title"><span class="title-bar"></span>评论 ({{ comments.length }})</h3>
        <el-card class="comment-form">
          <el-form :model="form" label-width="60px">
            <el-form-item label="昵称"><el-input v-model="form.nickname" placeholder="你的昵称" /></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="form.email" placeholder="选填" /></el-form-item>
            <el-form-item label="评论"><el-input v-model="form.content" type="textarea" :rows="3" placeholder="说点什么吧～" /></el-form-item>
            <el-button type="primary" :loading="submitting" @click="submit">发表评论</el-button>
          </el-form>
        </el-card>
        <div v-for="c in comments" :key="c.id" class="comment-item">
          <span class="nick">{{ c.nickname }}</span>
          <span class="time">{{ formatDate(c.createTime) }}</span>
          <p>{{ c.content }}</p>
        </div>
        <el-empty v-if="comments.length === 0" description="还没有评论，来抢沙发～" />
      </div>
    </div>
    <el-skeleton v-else :rows="10" animated />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getArticleDetail, getComments, addComment } from '../api/blog'

const route = useRoute()
const article = ref(null)
const comments = ref([])
const form = ref({ nickname: '', email: '', content: '' })

const formatDate = (d) => (d ? d.slice(0, 10) : '')

const loadComments = async () => {
  try {
    const res = await getComments(route.params.id, 1, 50)
    comments.value = res.list || []
  } catch (e) {}
}
const submitting = ref(false)
const submit = async () => {
  if (submitting.value) return
  if (!form.value.nickname || !form.value.content) {
    ElMessage.warning('请填写昵称和评论内容')
    return
  }
  submitting.value = true
  try {
    await addComment({ articleId: Number(route.params.id), ...form.value })
    ElMessage.success('评论成功')
    form.value.content = ''
    await loadComments()
  } catch (e) {} finally {
    submitting.value = false
  }
}

onMounted(async () => {
  try { article.value = await getArticleDetail(route.params.id) } catch (e) {}
  loadComments()
})
</script>
