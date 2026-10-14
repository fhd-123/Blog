<template>
  <div class="app-root">
    <div class="cursor-dot" ref="cursor"></div>

    <!-- 参照站 layout.tsx 的全局背景层：流动渐变 + 两枚模糊光晕 -->
    <div class="bg-stack" aria-hidden="true">
      <div class="bg-veil"></div>
      <div class="bg-gradient"></div>
      <div class="bg-orb bg-orb-tl"></div>
      <div class="bg-orb bg-orb-br"></div>
    </div>

    <canvas v-if="isDark" class="fireflies" ref="fireflies"></canvas>
    <ClickEffect v-if="!isAdmin && !isMobile" />
    <DanmakuBackground v-if="!isAdmin && !isMobile" />

      <!-- ① PC 端：水平导航（隐藏时整体上移） -->
      <div class="app-navbar pc-only" :class="{ hide: navHidden }">
        <div class="nav-inner">
          <router-link to="/" class="brand">
            <span class="brand-mark">✦</span>{{ brand || '傅浩东' }}<span class="brand-dot">の</span>{{ siteSuffix || '代码宝藏' }}
          </router-link>
          <nav class="nav-links nav-center">
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
          </div>
        </div>
      </div>

      <!-- ② 手机端：右侧可竖向拖拽的胶囊触发条 -->
      <div class="mobile-trigger"
           :class="{ 'is-hidden': wheelOpen }"
           :style="{ transform: 'translateY(' + dragY + 'px)' }"
           @pointerdown="startDrag" @pointermove="onDrag" @pointerup="endDrag"
           @click="openWheel" role="button" aria-label="打开菜单">
        <span></span><span></span><span></span>
      </div>

      <!-- ③ 手机端：圆盘转轴菜单 -->
      <transition name="wheel">
        <div v-if="wheelOpen" class="wheel-layer">
          <div class="wheel-mask" @click="wheelOpen = false"></div>
          <div class="wheel-wrap">
            <div class="wheel" :style="{ transform: 'rotate(' + wheelRot + 'deg)' }"
                 @pointerdown="startWheel" @pointermove="onWheel" @pointerup="endWheel">
              <div class="wheel-hub">
                <button @click="wheelOpen = false" aria-label="关闭">✕</button>
              </div>
              <div v-for="(link, i) in mobileLinks" :key="link.to" class="wheel-node"
                   :style="nodeStyle(i)">
                <router-link :to="link.to" :style="{ transform: 'rotate(' + (-wheelRot) + 'deg)' }"
                             :class="{ active: isActive(link.to) }" @click="wheelOpen = false">
                  {{ link.name }}
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </transition>

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
import ClickEffect from './components/ClickEffect.vue'
import DanmakuBackground from './components/DanmakuBackground.vue'

const route = useRoute()
const isAdmin = computed(() => route.path.startsWith('/admin'))
const isHome = computed(() => route.path === '/')
// 参照站：重负载特效（粒子/弹幕/流萤）仅在桌面端渲染，手机端跳过以省电省算力
const isMobile = ref(false)
const syncViewport = () => { isMobile.value = window.innerWidth < 768 }

const { state: playerState, toggle: playerToggle, next: playerNext, prev: playerPrev, init: playerInit, current: playerCurrent } = usePlayer()
const current = computed(() => playerCurrent())
const onToggle = () => playerToggle()
const onNext = () => playerNext()
const onPrev = () => playerPrev()

const brand = ref('')
const footerTag = ref('')
const slogan = ref('')
const siteSuffix = ref('代码宝藏')

const showBackTop = ref(false)
const navHidden = ref(false)
let lastY = 0

// ---- 手机端圆盘菜单 ----
const wheelOpen = ref(false)
const wheelRot = ref(0)
const dragY = ref(0)

// 参照站：手机端过滤掉「灵境」，保证圆盘均匀排布
const allLinks = [
  { name: '首页', to: '/' }, { name: '项目', to: '/projects' },
  { name: '归档', to: '/timeline' }, { name: '照片墙', to: '/photowall' },
  { name: '音乐', to: '/music' }, { name: '灵境', to: '/tree' },
  { name: '说说', to: '/moments' }, { name: '杂谈', to: '/chatter' },
  { name: '友链', to: '/friends' }, { name: '关于', to: '/about' }
]
const mobileLinks = allLinks.filter(l => l.to !== '/tree')
const nodeStyle = (i) => {
  const a = i * (360 / mobileLinks.length)
  return { transform: `rotate(${a}deg) translateY(-115px) rotate(${-a}deg)` }
}
const isActive = (to) => route.path === to

// 触发条竖向拖拽
let dragStart = null
let dragMoved = 0
const clampY = (v) => {
  const vh = window.innerHeight
  return Math.max(-(vh / 2) + 80, Math.min(vh / 2 - 80, v))
}
const startDrag = (e) => { dragStart = e.clientY; dragMoved = 0 }
const onDrag = (e) => {
  if (dragStart == null) return
  const d = e.clientY - dragStart
  dragMoved = Math.abs(d)
  dragY.value = clampY(d)
}
const endDrag = () => { dragStart = null }
const openWheel = () => {
  if (dragMoved > 10) return   // 拖动过就不当作点击
  wheelRot.value = 0
  wheelOpen.value = true
}

// 圆盘转动
let wlCenter = null
let wlPrevAngle = 0
const startWheel = (e) => {
  const el = e.currentTarget
  const r = el.getBoundingClientRect()
  wlCenter = { x: r.left + r.width / 2, y: r.top + r.height / 2 }
  wlPrevAngle = Math.atan2(e.clientY - wlCenter.y, e.clientX - wlCenter.x) * 180 / Math.PI
}
const onWheel = (e) => {
  if (!wlCenter) return
  const cur = Math.atan2(e.clientY - wlCenter.y, e.clientX - wlCenter.x) * 180 / Math.PI
  let d = cur - wlPrevAngle
  if (d > 180) d -= 360
  if (d < -180) d += 360
  wheelRot.value += d
  wlPrevAngle = cur
}
const endWheel = () => { wlCenter = null }

watch(wheelOpen, (v) => { document.body.style.overflow = v ? 'hidden' : '' })

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
  if (y > lastY && y > 240 && !wheelOpen.value) navHidden.value = true
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
  syncViewport()
  window.addEventListener('scroll', onScroll, { passive: true })
  if (window.matchMedia('(pointer:fine)').matches) {
    window.addEventListener('mousemove', onMove)
  }
  window.addEventListener('resize', onScroll)
  window.addEventListener('resize', syncViewport)
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
  window.removeEventListener('resize', syncViewport)
  if (timer) clearInterval(timer)
  if (ffRaf) cancelAnimationFrame(ffRaf)
})
</script>

<style>
.app-root { min-height: 100vh; position: relative; z-index: 1; }

/* ========== 全局背景层（对齐参照站 layout.tsx） ========== */
.bg-stack { position: fixed; inset: 0; z-index: -1; pointer-events: none; overflow: hidden; }
.bg-veil { position: absolute; inset: 0; z-index: -9; background: color-mix(in srgb, var(--bg-elev) 30%, transparent); backdrop-filter: blur(12px); transition: background .6s var(--ease); }
.bg-gradient {
  position: absolute; inset: 0; z-index: -8; opacity: .5;
  background: linear-gradient(-45deg, var(--accent), var(--accent-2), var(--gem-sky), var(--gem-violet));
  background-size: 400% 400%;
  animation: gradientMove 15s ease infinite;
  mix-blend-mode: color;
}
[data-theme="dark"] .bg-gradient { opacity: .22; }
@keyframes gradientMove { 0% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } 100% { background-position: 0% 50%; } }
.bg-orb { position: absolute; width: 40%; height: 40%; border-radius: 50%; filter: blur(100px); z-index: -7; }
.bg-orb-tl { top: -10%; left: -10%; background: color-mix(in srgb, var(--gem-sky) 35%, transparent); }
.bg-orb-br { bottom: -10%; right: -10%; background: color-mix(in srgb, var(--accent-2) 40%, transparent); }
@media (max-width: 768px) { .bg-gradient { opacity: .3; } .bg-orb { filter: blur(70px); } }
@media (prefers-reduced-motion: reduce) { .bg-gradient { animation: none; } }

/* ========== 手机端触发条 + 圆盘菜单 ========== */
.mobile-trigger { display: none; }
.wheel-layer { position: fixed; inset: 0; z-index: 70; }
.wheel-mask { position: absolute; inset: 0; background: color-mix(in srgb, var(--bg) 60%, #0b1020cc); backdrop-filter: blur(12px); }
.wheel-wrap { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 320px; height: 320px; pointer-events: none; }
.wheel {
  width: 100%; height: 100%; border-radius: 50%; position: relative; pointer-events: auto;
  border: 1px solid var(--border);
  background: color-mix(in srgb, var(--bg-elev) 45%, transparent);
  backdrop-filter: blur(20px) saturate(140%);
  box-shadow: 0 0 50px rgba(0,0,0,.3);
  cursor: grab; touch-action: none; transition: transform .12s linear;
}
.wheel:active { cursor: grabbing; }
.wheel-hub {
  position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
  width: 80px; height: 80px; border-radius: 50%; background: var(--bg-soft);
  border: 4px solid var(--border); display: flex; align-items: center; justify-content: center; z-index: 10;
}
.wheel-hub button {
  width: 48px; height: 48px; border-radius: 50%; border: none; cursor: pointer;
  background: var(--accent); color: #fff; font-size: 1.1rem; font-weight: 900;
  box-shadow: 0 6px 18px var(--accent-soft); transition: transform .3s var(--ease), background .3s var(--ease);
}
.wheel-hub button:hover { background: var(--gem-rose); transform: rotate(90deg); }
.wheel-node { position: absolute; top: 50%; left: 50%; width: 56px; height: 56px; margin: -28px 0 0 -28px; z-index: 5; }
.wheel-node a {
  display: flex; align-items: center; justify-content: center; width: 100%; height: 100%;
  border-radius: 50%; text-decoration: none; font-size: .7rem; font-weight: 800;
  background: var(--bg-elev); color: var(--text); border: 1px solid var(--border);
  box-shadow: var(--shadow); transition: transform .3s var(--ease), background .3s var(--ease);
}
.wheel-node a.active { background: linear-gradient(135deg, var(--accent), var(--accent-2)); color: #fff; box-shadow: 0 0 15px var(--accent-soft); transform: scale(1.1); }
.wheel-enter-active .wheel-wrap, .wheel-leave-active .wheel-wrap { transition: transform .35s cubic-bezier(.34,1.56,.64,1), opacity .3s var(--ease); }
.wheel-enter-from .wheel-wrap, .wheel-leave-to .wheel-wrap { transform: translate(-50%, -50%) scale(.5) rotate(-90deg); opacity: 0; }
.wheel-enter-active .wheel-mask, .wheel-leave-active .wheel-mask { transition: opacity .3s var(--ease); }
.wheel-enter-from .wheel-mask, .wheel-leave-to .wheel-mask { opacity: 0; }

@media (max-width: 768px) {
  .pc-only { display: none !important; }
  .mobile-trigger {
    display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 6px;
    position: fixed; top: 50%; right: 0; margin-top: -56px; width: 48px; height: 112px;
    border-radius: 999px 0 0 999px; z-index: 60; touch-action: none; cursor: grab;
    background: color-mix(in srgb, var(--accent) 80%, transparent);
    backdrop-filter: blur(12px); box-shadow: -5px 0 20px var(--accent-soft);
    border: 1px solid color-mix(in srgb, #fff 30%, transparent); border-right: none;
    transition: transform .5s var(--ease), opacity .5s var(--ease); padding-right: 6px;
  }
  .mobile-trigger span { width: 6px; height: 6px; border-radius: 50%; background: #fff; opacity: .9; }
  .mobile-trigger.is-hidden { transform: translateX(100%) !important; opacity: 0; pointer-events: none; }
}
@media (prefers-reduced-motion: reduce) { .wheel { transition: none; } }
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
/* 参照站：悬浮播放器在手机端隐藏（内部 rescale 到右下小尺寸） */
@media (max-width: 768px) { .floating-player { right: 12px; bottom: 74px; left: auto; padding: 5px 10px 5px 5px; } .floating-player .fp-info { width: 84px; } .floating-player .fp-disc { width: 34px; height: 34px; font-size: .9rem; } }
</style>
