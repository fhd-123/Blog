<template>
  <div class="app-root">
    <div class="cursor-dot" ref="cursor"></div>

    <header v-if="!isAdmin" class="app-navbar" :class="{ hide: navHidden }">
      <div class="nav-inner">
        <router-link to="/" class="brand">
          <span class="brand-mark">✦</span>{{ brand || '傅浩东' }}<span class="brand-dot">.</span>
        </router-link>
        <nav class="nav-links" :class="{ open: menuOpen }" @click="menuOpen = false">
          <router-link to="/">首页</router-link>
          <router-link to="/projects">项目</router-link>
          <router-link to="/archive">归档</router-link>
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
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getProfile } from './api/blog'

const route = useRoute()
const isAdmin = computed(() => route.path.startsWith('/admin'))

const brand = ref('')
const footerTag = ref('')
const slogan = ref('')

const menuOpen = ref(false)
const showBackTop = ref(false)
const navHidden = ref(false)
let lastY = 0

const themeIcon = ref('☀️')
const syncThemeIcon = () => {
  themeIcon.value = document.documentElement.getAttribute('data-theme') === 'dark' ? '☀️' : '🌙'
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
})
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  window.removeEventListener('mousemove', onMove)
  window.removeEventListener('resize', onScroll)
  if (timer) clearInterval(timer)
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
</style>
