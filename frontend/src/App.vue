<template>
  <div class="app-root">
    <!-- 公共导航（后台不显示，后台有独立布局） -->
    <header v-if="!isAdmin" class="app-navbar">
      <div class="nav-inner">
        <router-link to="/" class="brand">
          <span class="brand-mark">✦</span>{{ brand || '傅浩东' }}<span class="brand-dot">.</span>
        </router-link>
        <nav class="nav-links" :class="{ open: menuOpen }">
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

    <!-- 公共页脚 -->
    <footer v-if="!isAdmin" class="footer">
      <div class="footer-deco">🌸 ✦ 🐟 ✨ 🌟</div>
      <p class="uptime">本站已稳定运行：<b>{{ uptimeText }}</b></p>
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

const onScroll = () => { showBackTop.value = window.scrollY > 400 }
const scrollTop = () => window.scrollTo({ top: 0, behavior: 'smooth' })

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
  window.addEventListener('scroll', onScroll)
  tick(); timer = setInterval(tick, 1000)
  getProfile().then(p => {
    brand.value = p.resume_name || ''
    footerTag.value = p.site_name || ''
    slogan.value = p.slogan || ''
  }).catch(() => {})
})
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  if (timer) clearInterval(timer)
})
</script>

<style>
.app-root { min-height: 100vh; }

/* 路由切换：柔和淡入上滑，out-in 避免叠影 */
.page-enter-active,
.page-leave-active {
  transition: opacity .35s cubic-bezier(.22, .61, .36, 1),
              transform .35s cubic-bezier(.22, .61, .36, 1);
}
.page-enter-from { opacity: 0; transform: translateY(18px); }
.page-leave-to { opacity: 0; transform: translateY(-12px); }
</style>
