<template>
  <div>
    <section class="hero">
      <canvas ref="canvas" class="hero-canvas"></canvas>
      <div class="hero-glow"></div>
      <div class="hero-content">
        <div class="avatar reveal">{{ (profile.resume_name || '傅').charAt(0) }}</div>
        <h1 class="hero-title reveal">{{ profile.site_name || '傅浩东の代码宝藏' }}</h1>
        <p class="hero-sub reveal">{{ profile.slogan || 'Java 后端开发 · 全栈实践者' }}</p>
        <p class="hero-desc reveal">这里存放我的后端开发实践、踩坑复盘与学习思考——关于 Spring、MySQL、Redis、安全与前后端协作的一切。</p>
        <div class="hero-actions reveal">
          <router-link to="/archive" class="btn-primary" ref="cta" @mousemove="magnet" @mouseleave="magnetLeave">翻阅宝藏文章</router-link>
          <router-link to="/about" class="btn-ghost">关于我</router-link>
        </div>
        <div class="hero-stats reveal">
          <div class="stat"><span class="stat-num">{{ stats.articleCount || 0 }}</span><span class="stat-label">篇文章</span></div>
          <div class="stat"><span class="stat-num">{{ stats.categoryCount || 0 }}</span><span class="stat-label">个分类</span></div>
          <div class="stat"><span class="stat-num">{{ stats.tagCount || 0 }}</span><span class="stat-label">个标签</span></div>
        </div>
      </div>
      <div class="scroll-hint"><span class="mouse"></span>SCROLL</div>
    </section>

    <div class="app-container">
      <div class="layout">
        <div class="content">
          <h2 class="section-title"><span class="title-bar"></span>Latest Insight · 最新文章</h2>
          <div class="archive-grid">
            <router-link v-for="a in latest" :key="a.id" :to="'/articles/' + a.id" class="archive-card" @mousemove="spotlight">
              <div class="archive-cover">{{ a.title.charAt(0) }}</div>
              <div class="archive-body">
                <div class="archive-date"><span class="dot"></span>{{ formatDate(a.createTime) }} · {{ a.categoryName || '未分类' }}</div>
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
          <div class="widget reveal">
            <h3 class="widget-title">关于我</h3>
            <p class="widget-text">{{ profile.about_intro || '傅浩东，Java 后端开发工程师，专注 Spring 生态与全栈实践。' }}</p>
            <router-link to="/about" class="widget-link">查看完整简历 →</router-link>
          </div>
          <div class="widget reveal">
            <h3 class="widget-title">分类</h3>
            <ul class="cat-list">
              <li v-for="c in categories" :key="c.id" @click="goCategory(c.name)">
                <span class="name">{{ c.name }}</span><span class="count">{{ c.count }}</span>
              </li>
            </ul>
          </div>
          <div class="widget reveal">
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
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getLatestArticles, getCategories, getTags, getStats, getProfile } from '../api/blog'

const router = useRouter()
const latest = ref([])
const categories = ref([])
const tags = ref([])
const stats = ref({})
const profile = ref({})
const canvas = ref(null)
let raf = null

const formatDate = (d) => (d ? d.slice(0, 10) : '')
const goCategory = (name) => router.push({ path: '/archive', query: { category: name } })
const goTag = (name) => router.push({ path: '/archive', query: { tag: name } })

// 卡片光标聚光
const spotlight = (e) => {
  const el = e.currentTarget
  const r = el.getBoundingClientRect()
  el.style.setProperty('--mx', `${e.clientX - r.left}px`)
  el.style.setProperty('--my', `${e.clientY - r.top}px`)
}
// 磁吸按钮
const cta = ref(null)
const magnet = (e) => {
  const el = e.currentTarget
  const r = el.getBoundingClientRect()
  el.style.transform = `translate(${(e.clientX - r.left - r.width / 2) * 0.25}px, ${(e.clientY - r.top - r.height / 2) * 0.35}px)`
}
const magnetLeave = (e) => { e.currentTarget.style.transform = '' }

// 星空 canvas
const drawStars = () => {
  const cv = canvas.value
  if (!cv) return
  const ctx = cv.getContext('2d')
  const dpr = window.devicePixelRatio || 1
  const w = cv.clientWidth, h = cv.clientHeight
  cv.width = w * dpr; cv.height = h * dpr
  ctx.scale(dpr, dpr)
  const stars = Array.from({ length: 80 }, () => ({
    x: Math.random() * w, y: Math.random() * h, r: Math.random() * 1.6 + 0.4,
    a: Math.random(), tw: Math.random() * 0.02 + 0.005, vy: Math.random() * 0.12 + 0.02
  }))
  let t = 0
  const accent = getComputedStyle(document.documentElement).getPropertyValue('--accent').trim() || '#e8c46a'
  const loop = () => {
    t += 1
    ctx.clearRect(0, 0, w, h)
    stars.forEach(s => {
      s.a += s.tw; if (s.a > 1 || s.a < 0.1) s.tw *= -1
      s.y += s.vy; if (s.y > h) s.y = 0
      ctx.beginPath(); ctx.arc(s.x, s.y, s.r, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(232,196,106,${s.a.toFixed(2)})`
      ctx.fill()
    })
    raf = requestAnimationFrame(loop)
  }
  loop()
}

onMounted(async () => {
  try {
    profile.value = await getProfile().catch(() => ({}))
    ;[latest.value, categories.value, tags.value, stats.value] = await Promise.all([
      getLatestArticles(8), getCategories(), getTags(), getStats()
    ])
  } catch (e) {}
  if (window.matchMedia('(prefers-reduced-motion: no-preference)').matches) drawStars()
})
onUnmounted(() => { if (raf) cancelAnimationFrame(raf) })
</script>
