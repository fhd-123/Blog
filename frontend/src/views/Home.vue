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
        <div class="bento carousel-card reveal">
          <div class="carousel-head"><h3>Latest Insight</h3><span style="font-size:.78rem;color:var(--text-soft);">{{ latest.length }} 篇</span></div>
          <div class="carousel-body" @mouseenter="stopAuto" @mouseleave="startAuto">
            <div v-for="(a, i) in latest" :key="a.id" class="slide" :class="{ active: i === idx }"
                 @click="go('/articles/' + a.id)">
              <div class="slide-inner">
                <div class="slide-cover" :style="coverStyle(a)">
                  <img v-if="a.coverUrl && !coverFail[a.id]" :src="a.coverUrl" :alt="a.title"
                       loading="lazy" @error="markFail(a.id)" />
                  <span v-else class="cover-letter">{{ a.title.charAt(0) }}</span>
                </div>
                <div class="slide-meta">
                  <div class="s-date">{{ formatDate(a.createTime) }}</div>
                  <div class="s-title">{{ a.title }}</div>
                  <div class="s-desc">{{ a.summary }}</div>
                </div>
              </div>
            </div>
          </div>
          <div class="carousel-dots">
            <span v-for="(a, i) in latest" :key="a.id" :class="{ active: i === idx }" @click="idx = i"></span>
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
            <div class="bento carousel-card reveal">
              <div class="carousel-head"><h3>Records</h3></div>
              <div class="carousel-body" @mouseenter="stopAutoC" @mouseleave="startAutoC">
                <div v-for="(c, i) in chatter" :key="c.id" class="slide" :class="{ active: i === idxC }" @click="go('/chatter')">
                  <div class="slide-inner">
                    <div class="slide-cover" style="width:90px;" :style="essayCover(c)">
                      <img v-if="c.coverUrl && !coverFail['e' + c.id]" :src="c.coverUrl" :alt="c.title"
                           loading="lazy" @error="markFail('e' + c.id)" />
                      <span v-else class="cover-letter">📝</span>
                    </div>
                    <div class="slide-meta">
                      <div class="s-date">{{ c.date }}</div>
                      <div class="s-title">{{ c.title }}</div>
                      <div class="s-desc">{{ c.summary }}</div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="carousel-dots">
                <span v-for="(c, i) in chatter" :key="c.id" :class="{ active: i === idxC }" @click="idxC = i"></span>
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
            <span>Spring Boot</span><span>Vue 3</span><span>Element Plus</span>
            <span>MySQL</span><span>MyBatis-Plus</span><span>前后端分离</span>
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

// 封面：优先后端 cover_url，加载失败回退渐变+首字
const coverFail = ref({})
const markFail = (id) => { coverFail.value[id] = true }
const coverStyle = (a) => (a.coverUrl && !coverFail.value[a.id])
  ? {}
  : { background: 'linear-gradient(135deg, var(--accent), var(--accent-2))' }
const essayCover = (c) => (c.coverUrl && !coverFail.value['e' + c.id])
  ? {}
  : { background: 'linear-gradient(135deg, var(--gem-violet), var(--accent))' }
const github = 'https://github.com/fhd-123'
// 照片墙海报：优先使用首张照片的真实图片，其次回退渐变（对齐参照站的首图海报）
const posterStyle = computed(() => {
  const p = photos.value.find(x => x.imageUrl) || photos.value[0]
  if (!p) return { background: 'linear-gradient(135deg, var(--accent), var(--accent-2))' }
  return p.imageUrl
    ? { backgroundImage: `url("${p.imageUrl}")` }
    : { background: p.gradient || 'linear-gradient(135deg, var(--accent), var(--accent-2))' }
})

// 文章轮播
const idx = ref(0)
let timer = null
const startAuto = () => { stopAuto(); timer = setInterval(() => { if (latest.value.length) idx.value = (idx.value + 1) % latest.value.length }, 4000) }
const stopAuto = () => { if (timer) clearInterval(timer) }
// 杂谈轮播
const idxC = ref(0)
let timerC = null
const startAutoC = () => { stopAutoC(); timerC = setInterval(() => { if (chatter.value.length) idxC.value = (idxC.value + 1) % chatter.value.length }, 4000) }
const stopAutoC = () => { if (timerC) clearInterval(timerC) }

onMounted(async () => {
  syncTheme()
  try {
    const [p, s, l, ph, es] = await Promise.all([
      getProfile().catch(() => ({})), getStats().catch(() => ({})),
      getLatestArticles(6).catch(() => []), getPhotos().catch(() => []), getEssays().catch(() => [])
    ])
    profile.value = p
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
</style>
