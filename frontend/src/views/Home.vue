<template>
  <div class="home-wrap">
    <SearchBar class="reveal" />
    <div class="home-grid">
      <!-- 第一行：个人信息 + 播放器 -->
      <div class="col-7">
        <div class="bento profile-card reveal" @click="go('/about')">
          <div class="profile-top">
            <div class="avatar-box">
              <img v-if="profile.avatar_url" :src="profile.avatar_url" alt="avatar" />
              <div v-else class="avatar-initial">{{ (profile.resume_name || '傅').charAt(0) }}</div>
            </div>
            <div>
              <div class="profile-name">{{ profile.resume_name || '傅浩东' }}</div>
              <div class="profile-bio">{{ profile.about_intro || '在代码与系统之间，收集值得收藏的微光。' }}</div>
            </div>
          </div>
          <div>
            <div class="profile-stats">
              <div class="stat-item"><div class="stat-num">{{ stats.articleCount || 0 }}</div><div class="stat-label">文章</div></div>
              <div class="stat-divider"></div>
              <div class="stat-item"><div class="stat-num">{{ stats.chatterCount || 0 }}</div><div class="stat-label">杂谈</div></div>
              <div class="stat-divider"></div>
              <div class="stat-item"><div class="stat-num">{{ stats.photoCount || 0 }}</div><div class="stat-label">照片</div></div>
            </div>
            <div class="social-row">
              <a v-if="github" :href="github" target="_blank" rel="noopener" class="social-btn" @click.stop title="GitHub">⌨</a>
              <a v-if="profile.contact_email" :href="'mailto:' + profile.contact_email" class="social-btn" @click.stop title="邮箱">✉</a>
              <a v-if="profile.contact_wechat" class="social-btn" @click.stop title="微信">💬</a>
            </div>
          </div>
        </div>
      </div>

      <div class="col-5">
        <div class="bento cloud-player reveal" :class="{ playing: playerState.playing }" @click="go('/music')">
          <div v-if="!ready" class="cp-empty"><span class="spin"></span>CONNECTING…</div>
          <template v-else-if="current.name">
            <div class="cp-top">
              <div class="cp-disc">{{ current.emoji || '🎧' }}</div>
              <div>
                <div class="cp-title">{{ current.name }}</div>
                <div class="cp-artist">{{ current.artist }}</div>
              </div>
            </div>
            <div class="cp-lyric">{{ current.mood || '♪ 此刻的 BGM' }}</div>
            <div class="cp-bar"><span :style="{ width: playerState.playing ? '100%' : '0%' }"></span></div>
            <div class="cp-ctrl" @click.stop>
              <button @click="prev" aria-label="上一首">⏮</button>
              <button @click="toggle" :aria-label="playerState.playing ? '暂停' : '播放'">{{ playerState.playing ? '⏸' : '▶' }}</button>
              <button class="cp-mini" @click="next" aria-label="下一首">⏭</button>
            </div>
          </template>
          <div v-else class="cp-empty">暂无音乐</div>
        </div>
      </div>

      <!-- 歌词条 -->
      <div class="col-12">
        <div class="bento lyric-bar reveal">
          <span class="lb-dot"></span>
          <span class="lb-text">{{ current.mood ? ('♪ ' + current.name + ' — ' + current.mood) : '♪ 让声音成为代码的背景' }}</span>
        </div>
      </div>

      <!-- 第二行：文章轮播 + 照片墙 + 说说 + 主题块 -->
      <div class="col-4">
        <!-- 对齐参照站 LatestPostsCarousel：整卡封面图 + 底部叠字 + 右下角指示条 -->
        <div class="bento insight-card reveal" @click="goToArticle">
          <div class="ic-bg" :class="{ zoom: !!(cur.coverUrl && !coverFail[cur.id]) }" :style="coverStyle(cur)">
            <img v-if="cur.coverUrl && !coverFail[cur.id]" :src="cur.coverUrl" :alt="cur.title" />
            <span v-else class="cover-letter">{{ (cur.title || '').charAt(0) }}</span>
          </div>
          <div class="ic-scrim"></div>
          <div class="ic-text">
            <div class="ic-badges">
              <span class="ic-badge-main">Latest Insight</span>
              <span v-if="cur.createTime" class="ic-badge-date">{{ formatDateTime(cur.createTime) }}</span>
            </div>
            <h2>{{ cur.title }}</h2>
            <p>{{ cur.summary }}</p>
          </div>
          <div v-if="latest.length > 1" class="ic-dots">
            <button v-for="(a, i) in latest" :key="a.id"
                    :class="{ active: i === idx }"
                    @click.stop="idx = i"
                    :aria-label="`切换到第 ${i + 1} 篇文章`"></button>
          </div>
        </div>
      </div>

      <div class="col-8">
        <div class="home-right-col">
          <router-link to="/photowall" class="bento photo-poster reveal">
            <div class="pp-img" :style="posterStyle"></div>
            <div class="pp-mask"></div>
            <div class="pp-text">
              <h3>照片墙</h3>
              <p>{{ photos.length ? '定格那些值得回看的光影' : '查看摄影' }}</p>
            </div>
          </router-link>

          <div class="home-right-grid">
            <!-- 对齐参照站 LatestChatterCarousel：整卡封面 + 左侧渐变压暗 + 居中文本 -->
            <div class="bento records-card reveal" @click="go('/chatter')">
              <div class="rc-bg" :style="essayCover(curC)">
                <img v-if="curC.coverUrl && !coverFail['e' + curC.id]" :src="curC.coverUrl" alt="Chatter Cover" />
              </div>
              <div class="rc-scrim"></div>
              <div class="rc-text">
                <div class="rc-badges">
                  <span class="rc-badge-main">Records</span>
                  <span v-if="curC.date" class="rc-date">{{ curC.date }}</span>
                </div>
                <h3>{{ curC.title }}</h3>
                <p>{{ curC.summary }}</p>
              </div>
              <div v-if="chatter.length > 1" class="ic-dots">
                <button v-for="(c, i) in chatter" :key="c.id"
                        :class="{ active: i === idxC }"
                        @click.stop="idxC = i"
                        :aria-label="`切换到第 ${i + 1} 条杂谈`"></button>
              </div>
            </div>

            <div class="bento theme-block reveal" @click="toggleTheme">
              <div class="tb-icon">{{ themeIcon }}</div>
              <div class="tb-label">{{ isDark ? '流萤飞舞的深空' : '日间模式' }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 数据面板 -->
      <div class="col-12">
        <div class="bento dashboard reveal">
          <div class="dash-head">
            <span class="eyebrow" style="margin:0;">Site Dashboard</span>
            <span class="dash-uptime">系统已稳定运行：<b>{{ uptimeText }}</b></span>
          </div>
          <div class="dash-grid">
            <div class="dash-item"><div class="dash-num">{{ stats.articleCount || 0 }}</div><div class="dash-label">文章</div></div>
            <div class="dash-item"><div class="dash-num">{{ stats.categoryCount || 0 }}</div><div class="dash-label">分类</div></div>
            <div class="dash-item"><div class="dash-num">{{ stats.tagCount || 0 }}</div><div class="dash-label">标签</div></div>
            <div class="dash-item"><div class="dash-num">{{ stats.chatterCount || 0 }}</div><div class="dash-label">杂谈</div></div>
            <div class="dash-item"><div class="dash-num">{{ stats.photoCount || 0 }}</div><div class="dash-label">照片</div></div>
            <div class="dash-item"><div class="dash-num">{{ stats.friendCount || 0 }}</div><div class="dash-label">友链</div></div>
            <div class="dash-item"><div class="dash-num">{{ stats.commentCount || 0 }}</div><div class="dash-label">评论</div></div>
          </div>
          <div class="tech-badges" style="margin-top:16px;">
            <span v-for="t in techBadges" :key="t">{{ t }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getLatestArticles, getStats, getProfile, getPhotos, getEssays } from '../api/blog'
import SearchBar from '../components/SearchBar.vue'
import { useReveal } from '../composables/useReveal'
import { usePlayer } from '../composables/player'

useReveal()
const router = useRouter()
const go = (p) => router.push(p)

const profile = ref({})
const stats = ref({})
const latest = ref([])
const photos = ref([])
const chatter = ref([])

const { state: playerState, toggle, next, prev, init, current: playerCurrent } = usePlayer()
const current = computed(() => playerCurrent())
const ready = ref(false)

const themeIcon = ref('☀️')
const isDark = ref(false)
const syncTheme = () => {
  const t = document.documentElement.getAttribute('data-theme')
  themeIcon.value = t === 'dark' ? '☀️' : '🌙'
  isDark.value = t === 'dark'
}
const toggleTheme = () => {
  const next = isDark.value ? 'light' : 'dark'
  document.documentElement.setAttribute('data-theme', next)
  localStorage.setItem('theme', next)
  syncTheme()
}

const formatDate = (d) => (d ? d.slice(0, 10) : '')
// 参照站 formattedDate：YYYY.MM.DD HH:mm
const formatDateTime = (d) => {
  if (!d) return ''
  const s = String(d).slice(0, 16)
  return s.replace(/-/g, '.').replace(' ', ' ')
}

// 封面：优先后端 cover_url，加载失败回退渐变+首字
const coverFail = ref({})
const markFail = (id) => { coverFail.value[id] = true }
const coverStyle = (a) => (a.coverUrl && !coverFail.value[a.id])
  ? {}
  : { background: 'linear-gradient(135deg, var(--accent), var(--accent-2))' }
const essayCover = (c) => (c.coverUrl && !coverFail.value['e' + c.id])
  ? {}
  : { background: 'linear-gradient(135deg, var(--gem-violet), var(--accent))' }

// 技术栈徽章由后端 site_profile.footer_badges 驱动
const techBadges = ref([])
const parseList = (raw, fallback) => {
  try {
    let arr = []
    if (Array.isArray(raw)) arr = raw
    else if (typeof raw === 'string' && raw.trim()) {
      const t = raw.trim()
      arr = t.startsWith('[') ? JSON.parse(t) : t.split(/[,，]/).map(s => s.trim()).filter(Boolean)
    }
    return arr.length ? arr : fallback
  } catch (e) { return fallback }
}
const github = 'https://github.com/fhd-123'
// 照片墙海报：优先使用首张照片的真实图片，其次回退渐变（对齐参照站的首图海报）
const posterStyle = computed(() => {
  const p = photos.value.find(x => x.imageUrl) || photos.value[0]
  if (!p) return { background: 'linear-gradient(135deg, var(--accent), var(--accent-2))' }
  return p.imageUrl
    ? { backgroundImage: `url("${p.imageUrl}")` }
    : { background: p.gradient || 'linear-gradient(135deg, var(--accent), var(--accent-2))' }
})

// 文章轮播（对齐参照站：5s 自动切换）
const idx = ref(0)
const cur = computed(() => latest.value[idx.value] || { title: '', summary: '', coverUrl: '', id: null })
const goToArticle = () => { if (cur.value.id) go('/articles/' + cur.value.id) }
let timer = null
const startAuto = () => { stopAuto(); if (latest.value.length > 1) timer = setInterval(() => { if (latest.value.length) idx.value = (idx.value + 1) % latest.value.length }, 5000) }
const stopAuto = () => { if (timer) clearInterval(timer) }
// 杂谈轮播（对齐参照站：6s 自动切换）
const idxC = ref(0)
const curC = computed(() => chatter.value[idxC.value] || { title: '', summary: '', coverUrl: '', id: null, date: '' })
let timerC = null
const startAutoC = () => { stopAutoC(); if (chatter.value.length > 1) timerC = setInterval(() => { if (chatter.value.length) idxC.value = (idxC.value + 1) % chatter.value.length }, 6000) }
const stopAutoC = () => { if (timerC) clearInterval(timerC) }

onMounted(async () => {
  syncTheme()
  try {
    const [p, s, l, ph, es] = await Promise.all([
      getProfile().catch(() => ({})), getStats().catch(() => ({})),
      getLatestArticles(6).catch(() => []), getPhotos().catch(() => []), getEssays().catch(() => [])
    ])
    profile.value = p
    techBadges.value = parseList(p?.footer_badges, ['Spring Boot', 'Vue 3', 'Element Plus', 'MySQL', 'MyBatis-Plus', '前后端分离'])
    stats.value = s
    latest.value = l
    photos.value = ph
    chatter.value = es
  } catch (e) {}
  init().then(() => { ready.value = true }).catch(() => { ready.value = true })
  if (latest.value.length) startAuto()
  if (chatter.value.length) startAutoC()
  onUptime()
})
onUnmounted(() => { stopAuto(); stopAutoC(); clearInterval(uptimeTimer) })

// 运行时间（模拟参照站 uptime，起点为首次访问日期）
const uptimeText = ref('00:00:00')
let uptimeTimer = null
const onUptime = () => {
  const KEY = 'site_boot'
  let boot = localStorage.getItem(KEY)
  if (!boot) { boot = String(Date.now()); localStorage.setItem(KEY, boot) }
  const start = Number(boot)
  const tick = () => {
    const s = Math.floor((Date.now() - start) / 1000)
    const p = (n) => String(n).padStart(2, '0')
    uptimeText.value = `${p(Math.floor(s / 3600))}:${p(Math.floor((s % 3600) / 60))}:${p(s % 60)}`
  }
  tick()
  uptimeTimer = setInterval(tick, 1000)
}
</script>

<style scoped>
.home-right-col { display: flex; flex-direction: column; gap: 20px; }
.home-right-grid { display: grid; grid-template-columns: 2fr 1fr; gap: 20px; }
@media (max-width: 920px) { .home-right-grid { grid-template-columns: 1fr; } }

/* ===== Latest Insight 整卡封面 ===== */
.insight-card { position: relative; min-height: 420px; display: flex; flex-direction: column; justify-content: flex-end; overflow: hidden; cursor: pointer; }
.ic-bg { position: absolute; inset: 0; z-index: 0; transition: transform 1s var(--ease-out); }
.insight-card:hover .ic-bg { transform: scale(1.05); }
.ic-bg img { width: 100%; height: 100%; object-fit: cover; opacity: .9; display: block; }
.ic-bg .cover-letter { position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; font-family: var(--serif); font-size: 5rem; font-weight: 900; color: #fff; opacity: .25; }
.ic-scrim { position: absolute; inset: 0; z-index: 1; background: linear-gradient(to top, rgba(0,0,0,.9), rgba(0,0,0,.3) 50%, transparent); }
.ic-text { position: relative; z-index: 2; padding: 24px 22px 44px; color: #fff; }
.ic-badges { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; flex-wrap: wrap; }
.ic-badge-main { padding: 4px 12px; border-radius: 999px; background: color-mix(in srgb, var(--accent) 80%, transparent); backdrop-filter: blur(8px); font-size: .62rem; font-weight: 900; letter-spacing: .12em; text-transform: uppercase; color: #fff; box-shadow: var(--shadow); }
.ic-badge-date { padding: 3px 10px; border-radius: 999px; background: rgba(0,0,0,.4); border: 1px solid rgba(255,255,255,.2); font-size: .68rem; color: rgba(255,255,255,.9); font-variant-numeric: tabular-nums; }
.ic-text h2 { font-family: var(--serif); font-size: 1.45rem; font-weight: 700; color: #fff; margin-bottom: 8px; text-shadow: 0 2px 8px rgba(0,0,0,.5); transition: transform .35s var(--ease); }
.insight-card:hover .ic-text h2 { transform: translateY(-4px); }
.ic-text p { font-size: .85rem; color: rgba(255,255,255,.8); line-height: 1.6; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; text-shadow: 0 1px 4px rgba(0,0,0,.5); }
.ic-dots { position: absolute; bottom: 16px; right: 22px; z-index: 3; display: flex; gap: 6px; align-items: center; }
.ic-dots button { height: 6px; border: none; border-radius: 999px; cursor: pointer; padding: 0; transition: width .5s var(--ease), background .3s var(--ease); width: 8px; background: rgba(255,255,255,.4); }
.ic-dots button:hover { background: rgba(255,255,255,.8); }
.ic-dots button.active { width: 24px; background: color-mix(in srgb, var(--accent-2) 85%, transparent); }
@media (max-width: 768px) { .insight-card { min-height: 340px; } }

/* ===== Records 杂谈整卡封面 ===== */
.records-card { position: relative; min-height: 220px; display: flex; flex-direction: column; justify-content: center; overflow: hidden; cursor: pointer; }
.rc-bg { position: absolute; inset: 0; z-index: 0; transition: transform 1s var(--ease-out); }
.records-card:hover .rc-bg { transform: scale(1.05); }
.rc-bg img { width: 100%; height: 100%; object-fit: cover; opacity: .8; display: block; }
[data-theme="dark"] .rc-bg img { opacity: .6; }
.rc-scrim { position: absolute; inset: 0; z-index: 1; background: linear-gradient(to right, rgba(0,0,0,.8), rgba(0,0,0,.4) 50%, rgba(0,0,0,.1)); }
.rc-text { position: relative; z-index: 2; padding: 22px 26px; color: #fff; width: 100%; }
.rc-badges { display: flex; align-items: flex-end; gap: 8px; margin-bottom: 8px; }
.rc-badge-main { padding: 3px 8px; border-radius: 6px; font-size: .6rem; font-weight: 900; letter-spacing: .12em; text-transform: uppercase; color: var(--accent-2); background: rgba(0,0,0,.3); border: 1px solid rgba(255,255,255,.12); backdrop-filter: blur(4px); }
.rc-date { font-size: .7rem; color: rgba(255,255,255,.72); font-variant-numeric: tabular-nums; text-shadow: 0 1px 4px rgba(0,0,0,.5); }
.rc-text h3 { font-family: var(--serif); font-size: 1.35rem; font-weight: 700; margin-bottom: 8px; color: #fff; text-shadow: 0 2px 8px rgba(0,0,0,.5); transition: color .3s var(--ease); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.records-card:hover .rc-text h3 { color: var(--accent-2); }
.rc-text p { font-size: .85rem; color: rgba(255,255,255,.82); font-weight: 500; line-height: 1.65; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; text-shadow: 0 1px 4px rgba(0,0,0,.5); }
@media (max-width: 768px) { .records-card { min-height: 200px; } .rc-text { padding: 18px 20px; } }
</style>
