<template>
  <div>
    <section class="hero">
      <div class="hero-stars"></div>
      <div class="hero-glow"></div>
      <div class="hero-content">
        <div class="avatar">{{ (profile.resume_name || '傅').charAt(0) }}</div>
        <h1 class="hero-title">{{ profile.site_name || '傅浩东の代码宝藏' }}</h1>
        <p class="hero-sub">{{ profile.slogan || 'Java 后端开发 · 全栈实践者' }}</p>
        <p class="hero-desc">这里存放我的后端开发实践、踩坑复盘与学习思考——关于 Spring、MySQL、Redis、安全与前后端协作的一切。</p>
        <div class="hero-actions">
          <router-link to="/archive" class="btn-primary">翻阅宝藏文章</router-link>
          <router-link to="/about" class="btn-ghost">关于我</router-link>
        </div>
        <div class="hero-stats">
          <div class="stat"><span class="stat-num">{{ stats.articleCount || 0 }}</span><span class="stat-label">篇文章</span></div>
          <div class="stat"><span class="stat-num">{{ stats.categoryCount || 0 }}</span><span class="stat-label">个分类</span></div>
          <div class="stat"><span class="stat-num">{{ stats.tagCount || 0 }}</span><span class="stat-label">个标签</span></div>
        </div>
      </div>
    </section>

    <div class="app-container">
      <div class="layout">
        <div class="content">
          <h2 class="section-title"><span class="title-bar"></span>Latest Insight · 最新文章</h2>
          <div class="archive-grid">
            <router-link v-for="a in latest" :key="a.id" :to="'/articles/' + a.id" class="archive-card">
              <div class="archive-cover">{{ a.title.charAt(0) }}</div>
              <div class="archive-body">
                <div class="archive-date">{{ formatDate(a.createTime) }} · {{ a.categoryName || '未分类' }}</div>
                <h3>{{ a.title }}</h3>
                <p style="color:var(--text-soft);font-size:.88rem;">{{ a.summary }}</p>
                <div class="tags">
                  <span v-for="t in a.tags" :key="t.id">{{ t.name }}</span>
                </div>
              </div>
            </router-link>
          </div>
        </div>

        <aside class="sidebar">
          <div class="widget">
            <h3 class="widget-title">关于我</h3>
            <p class="widget-text">傅浩东，Java 后端开发工程师，专注 Spring 生态、数据库优化与全栈实践。软件工程本科在读，在代码里收集属于自己的宝藏。</p>
            <router-link to="/about" class="widget-link">查看完整简历 →</router-link>
          </div>
          <div class="widget">
            <h3 class="widget-title">分类</h3>
            <ul class="cat-list">
              <li v-for="c in categories" :key="c.id" @click="goCategory(c.id)">
                <span class="name">{{ c.name }}</span><span class="count">{{ c.count }}</span>
              </li>
            </ul>
          </div>
          <div class="widget">
            <h3 class="widget-title">标签</h3>
            <div class="tag-cloud">
              <span v-for="t in tags" :key="t.id" @click="goTag(t.name)">{{ t.name }}</span>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getLatestArticles, getCategories, getTags, getStats, getProfile } from '../api/blog'

const router = useRouter()
const latest = ref([])
const categories = ref([])
const tags = ref([])
const stats = ref({})
const profile = ref({})

const formatDate = (d) => (d ? d.slice(0, 10) : '')
const goCategory = (name) => router.push({ path: '/archive', query: { category: name } })
const goTag = (name) => router.push({ path: '/archive', query: { tag: name } })

onMounted(async () => {
  try {
    profile.value = await getProfile().catch(() => ({}))
    latest.value = await getLatestArticles(8)
    categories.value = await getCategories()
    tags.value = await getTags()
    stats.value = await getStats()
  } catch (e) {}
})
</script>
