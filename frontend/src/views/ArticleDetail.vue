<template>
  <div class="app-container">
    <!-- 阅读进度条 -->
    <div class="read-progress"><span :style="{ width: progress + '%' }"></span></div>

    <div v-if="article" class="detail-wrap">
      <router-link to="/timeline" class="widget-link back">← 返回文章列表</router-link>

      <div class="detail-layout">
        <article class="article-detail reveal">
          <span class="eyebrow">{{ article.categoryName || '未分类' }}</span>
          <h1>{{ article.title }}</h1>
          <div class="article-meta">
            {{ formatDate(article.createTime) }} · 阅读 {{ article.views }}
            <span v-if="article.tags && article.tags.length" class="tags" style="display:inline-flex;margin-left:12px;">
              <span v-for="t in article.tags" :key="t.id">{{ t.name }}</span>
            </span>
          </div>
          <div class="article-body" ref="bodyEl" v-html="article.content"></div>
        </article>

        <!-- 目录 -->
        <aside v-if="toc.length" class="toc">
          <div class="toc-title">目录</div>
          <a
            v-for="(t, i) in toc"
            :key="i"
            class="toc-item"
            :class="['toc-lv' + t.level, { active: activeId === t.id }]"
            :href="'#' + t.id"
            @click.prevent="scrollTo(t.id)"
          >{{ t.text }}</a>
        </aside>
      </div>

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
import { onMounted, onUnmounted, nextTick, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getArticleDetail, getComments, addComment } from '../api/blog'

const route = useRoute()
const article = ref(null)
const comments = ref([])
const form = ref({ nickname: '', email: '', content: '' })

// ---- 目录 & 阅读进度 ----
const bodyEl = ref(null)
const toc = ref([])
const activeId = ref('')
const progress = ref(0)

const buildToc = () => {
  const root = bodyEl.value
  if (!root) return
  const heads = root.querySelectorAll('h2, h3')
  toc.value = Array.from(heads).map((el, i) => {
    const id = el.id || ('sec-' + i)
    el.id = id
    return { id, text: el.textContent.trim(), level: el.tagName === 'H2' ? 2 : 3 }
  })
}

const scrollTo = (id) => {
  const el = document.getElementById(id)
  if (!el) return
  const top = el.getBoundingClientRect().top + window.scrollY - (Number(getComputedStyle(document.documentElement).getPropertyValue('--nav-h').replace('px','')) + 20)
  window.scrollTo({ top, behavior: 'smooth' })
  activeId.value = id
}

const onScroll = () => {
  // 阅读进度
  const doc = document.documentElement
  const h = doc.scrollHeight - window.innerHeight
  progress.value = h > 0 ? Math.min(100, Math.round((window.scrollY / h) * 100)) : 0
  // 高亮当前小节
  let cur = ''
  for (const t of toc.value) {
    const el = document.getElementById(t.id)
    if (el && el.getBoundingClientRect().top <= 140) cur = t.id
  }
  activeId.value = cur || (toc.value[0] ? toc.value[0].id : '')
}

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
  window.addEventListener('scroll', onScroll, { passive: true })
  try { article.value = await getArticleDetail(route.params.id) } catch (e) {}
  await nextTick()
  buildToc()
  onScroll()
  loadComments()
})
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.detail-wrap { max-width: 1100px; margin: 0 auto; }
.detail-layout { display: grid; grid-template-columns: minmax(0, 1fr) 240px; gap: 32px; align-items: start; }
.article-detail { max-width: none; }

/* 阅读进度 */
.read-progress { position: fixed; top: var(--nav-h); left: 0; right: 0; height: 3px; z-index: 99; pointer-events: none; }
.read-progress span { display: block; height: 100%; background: linear-gradient(90deg, var(--accent), var(--accent-2)); transition: width .15s linear; }

/* 目录 */
.toc { position: sticky; top: calc(var(--nav-h) + 24px); background: var(--bg-elev); border: 1px solid var(--border); border-radius: var(--radius); padding: 18px 16px; box-shadow: var(--shadow); max-height: calc(100vh - var(--nav-h) - 60px); overflow-y: auto; }
.toc-title { font-family: var(--serif); font-size: .95rem; font-weight: 800; margin-bottom: 12px; padding-left: 10px; border-left: 3px solid var(--accent); }
.toc-item { display: block; font-size: .82rem; color: var(--text-soft); padding: 6px 10px; border-radius: 8px; text-decoration: none; transition: color .2s var(--ease), background .2s var(--ease); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.toc-item:hover { color: var(--accent); background: var(--accent-soft); }
.toc-item.toc-lv3 { padding-left: 22px; font-size: .78rem; }
.toc-item.active { color: var(--accent); background: var(--accent-soft); font-weight: 700; }

@media (max-width: 1024px) {
  .detail-layout { grid-template-columns: 1fr; }
  .toc { display: none; }
}
</style>
