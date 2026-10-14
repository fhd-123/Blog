<template>
  <div class="app-root">
    <div class="cursor-dot" ref="cursor"></div>
    <canvas v-if="isDark" class="fireflies" ref="fireflies"></canvas>

    <header v-if="!isAdmin" class="app-navbar" :class="{ hide: navHidden }">
      <div class="nav-inner">
        <router-link to="/" class="brand">
          <span class="brand-mark">✦</span>{{ brand || '傅浩东' }}<span class="brand-dot">の</span>{{ siteSuffix || '代码宝藏' }}
        </router-link>
        <nav class="nav-links nav-center" :class="{ open: menuOpen }" @click="menuOpen = false">
          <router-link to="/">首页</router-link>
          <router-link to="/projects">项目</router-link>
          <router-link to="/timeline">归档</router-link>
          <router-link to="/photowall">照片墙</router-link>
          <router-link to="/music">音乐</router-link>
          <router-link to="/tree">灵境</router-link>
          <router-link to="/moments">说说</router-link>
          <router-link to="/chatter">杂谈</router-link>
          <router-link to="/friends">友链</router-link>
          <router-link to="/about">关于</router-link>
        </nav>
        <div class="nav-right">
          <button class="theme-toggle" @click="toggleTheme" aria-label="切换主题">{{ themeIcon }}</button>
          <button class="menu-toggle" @click="menuOpen = !menuOpen" aria-label="菜单">☰</button>
        </div>
      </div>
    </header>

    <router-view v-slot="{ Component, route }">
      <transition name="page" mode="out-in">
        <component :is="Component" :key="route.path" />
      </transition>
    </router-view>

    <footer v-if="!isAdmin" class="footer">
      <div class="footer-deco">🌸 ✦ 🐟 ✨ 🌟</div>
      <p class="uptime">本站已稳定运行：<b>{{ uptimeText }}</b></p>
      <div class="marquee"><div>
        <span>收集值得收藏的微光</span><span>·</span><span>Spring Boot</span><span>·</span><span>Vue3</span><span>·</span><span>MySQL</span><span>·</span><span>Redis</span><span>·</span><span>全栈实践</span><span>·</span>
        <span>收集值得收藏的微光</span><span>·</span><span>Spring Boot</span><span>·</span><span>Vue3</span><span>·</span><span>MySQL</span><span>·</span><span>Redis</span><span>·</span><span>全栈实践</span><span>·</span>
      </div></div>
      <div class="tech-badges">
        <span>Spring Boot</span><span>Vue3</span><span>Element Plus</span>
        <span>MySQL</span><span>MyBatis-Plus</span><span>前后端分离</span>
      </div>
      <p>© 2026 {{ brand || '傅浩东' }} · {{ footerTag || '傅浩东の代码宝藏' }}</p>
      <p class="footer-sub">{{ slogan || '在代码与系统之间，收集值得收藏的微光' }} · <router-link to="/friends">交换友链 ✉</router-link></p>
    </footer>

    <button class="back-top" :class="{ show: showBackTop }" @click="scrollTop" aria-label="回到顶部">↑</button>

    <!-- 全局悬浮音乐播放器 -->
    <div v-if="!isAdmin && playerState.list.length" class="floating-player" :class="{ playing: playerState.playing, hidden: isHome }">
      <div class="fp-disc" @click="onToggle" role="button" :aria-label="playerState.playing ? '暂停' : '播放'">{{ current.emoji || '🎧' }}</div>
      <div class="fp-info">
        <div class="fp-name">{{ current.name || '未选择' }}</div>
        <div class="fp-lyric">{{ current.mood || current.artist || '♪' }}</div>
      </div>
      <div class="fp-ctrl">
        <button @click="onToggle" aria-label="播放/暂停">{{ playerState.playing ? '⏸' : '▶' }}</button>
        <button class="fp-next" @click="onNext" aria-label="下一首">⏭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getProfile } from './api/blog'
import { usePlayer } from './composables/player'

const route = useRoute()
const isAdmin = computed(() => route.path.startsWith('/admin'))
const isHome = computed(() => route.path === '/')

const { state: playerState, toggle: playerToggle, next: playerNext, prev: playerPrev, init: playerInit, current: playerCurrent } = usePlayer()
const current = computed(() => playerCurrent())
const onToggle = () => playerToggle()
const onNext = () => playerNext()
const onPrev = () => playerPrev()

const brand = ref('')
const footerTag = ref('')
const slogan = ref('')
const siteSuffix = ref('代码宝藏')

const menuOpen = ref(false)
const showBackTop = ref(false)
const navHidden = ref(false)
let lastY = 0

const themeIcon = ref('☀️')
const isDark = ref(false)
const syncThemeIcon = () => {
  const t = document.documentElement.getAttribute('data-theme')
  themeIcon.value = t === 'dark' ? '☀️' : '🌙'
  isDark.value = t === 'dark'
}
const toggleTheme = () => {
  const cur = document.documentElement.getAttribute('data-theme')
  const next = cur === 'dark' ? 'light' : 'dark'
  document.documentElement.setAttribute('data-theme', next)
  localStorage.setItem('theme', next)
  syncThemeIcon()
}

const onScroll = () => {
  const y = window.scrollY
  showBackTop.value = y > 400
  if (y > lastY && y > 240 && !menuOpen.value) navHidden.value = true
  else navHidden.value = false
  lastY = y
}
const scrollTop = () => window.scrollTo({ top: 0, behavior: 'smooth' })

// 自定义光标（仅精确指针设备）
const cursor = ref(null)
const onMove = (e) => {
  if (cursor.value) {
    cursor.value.style.transform = `translate(${e.clientX}px, ${e.clientY}px)`
  }
}

// 夜间流萤（fireflies）
const fireflies = ref(null)
let ffRaf = null
const drawFireflies = () => {
  const cv = fireflies.value
  if (!cv) return
  const ctx = cv.getContext('2d')
  const dpr = window.devicePixelRatio || 1
  const w = window.innerWidth, h = window.innerHeight
  cv.width = w * dpr; cv.height = h * dpr
  ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
  const N = Math.min(46, Math.round(w / 32))
  const ffs = Array.from({ length: N }, () => ({
    x: Math.random() * w, y: Math.random() * h,
    r: Math.random() * 1.8 + 0.8, a: Math.random(), tw: Math.random() * 0.02 + 0.006,
    vx: (Math.random() - 0.5) * 0.25, vy: (Math.random() - 0.5) * 0.25
  }))
  const loop = () => {
    ctx.clearRect(0, 0, w, h)
    ffs.forEach(s => {
      s.a += s.tw; if (s.a > 1 || s.a < 0.15) s.tw *= -1
      s.x += s.vx; s.y += s.vy
      if (s.x < 0) s.x = w; if (s.x > w) s.x = 0
      if (s.y < 0) s.y = h; if (s.y > h) s.y = 0
      ctx.beginPath(); ctx.arc(s.x, s.y, s.r, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(200,255,200,${(s.a * 0.9).toFixed(2)})`
      ctx.shadowBlur = 10; ctx.shadowColor = 'rgba(100,255,150,0.8)'
      ctx.fill(); ctx.shadowBlur = 0
    })
    ffRaf = requestAnimationFrame(loop)
  }
  loop()
}
watch(isDark, (v) => {
  if (ffRaf) { cancelAnimationFrame(ffRaf); ffRaf = null }
  if (v && window.matchMedia('(prefers-reduced-motion: no-preference)').matches) {
    setTimeout(drawFireflies, 60)
  }
})

// 运行时计数
const LAUNCH = new Date('2026-03-01T00:00:00')
const uptimeText = ref('连接中…')
let timer = null
const tick = () => {
  const diff = Date.now() - LAUNCH.getTime()
  const d = Math.floor(diff / 86400000)
  const h = Math.floor((diff % 86400000) / 3600000)
  const m = Math.floor((diff % 3600000) / 60000)
  const s = Math.floor((diff % 60000) / 1000)
  uptimeText.value = `${d} 天 ${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

onMounted(() => {
  const t = localStorage.getItem('theme') || 'dark'
  document.documentElement.setAttribute('data-theme', t)
  syncThemeIcon()
  window.addEventListener('scroll', onScroll, { passive: true })
  if (window.matchMedia('(pointer:fine)').matches) {
    window.addEventListener('mousemove', onMove)
  }
  window.addEventListener('resize', onScroll)
  tick(); timer = setInterval(tick, 1000)
  getProfile().then(p => {
    brand.value = p.resume_name || ''
    footerTag.value = p.site_name || ''
    slogan.value = p.slogan || ''
  }).catch(() => {})
  playerInit().catch(() => {})
  if (isDark.value && window.matchMedia('(prefers-reduced-motion: no-preference)').matches) {
    setTimeout(drawFireflies, 60)
  }
})
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  window.removeEventListener('mousemove', onMove)
  window.removeEventListener('resize', onScroll)
  if (timer) clearInterval(timer)
  if (ffRaf) cancelAnimationFrame(ffRaf)
})
</script>

<style>
.app-root { min-height: 100vh; position: relative; z-index: 1; }
.cursor-dot {
  position: fixed; top: 0; left: 0; width: 9px; height: 9px; border-radius: 50%;
  background: var(--accent); pointer-events: none; z-index: 9999; mix-blend-mode: difference;
  transform: translate(-50%, -50%); transition: width .2s, height .2s, opacity .2s;
  opacity: 0;
}
@media (pointer: fine) { .cursor-dot { opacity: .85; } }
@media (hover: none) { .cursor-dot { display: none; } }

/* 路由切换 */
.page-enter-active, .page-leave-active { transition: opacity .35s var(--ease), transform .35s var(--ease); }
.page-enter-from { opacity: 0; transform: translateY(20px); }
.page-leave-to { opacity: 0; transform: translateY(-14px); }

/* 夜间流萤 */
.fireflies { position: fixed; inset: 0; z-index: 0; pointer-events: none; }

/* 悬浮音乐播放器（首页隐藏） */
@media (max-width: 768px) { .floating-player { right: 16px; bottom: 16px; left: auto; padding: 6px 12px 6px 6px; } .floating-player .fp-info { width: 92px; } }
</style>
