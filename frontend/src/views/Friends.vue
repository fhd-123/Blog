<template>
  <div class="app-container">
    <!-- 对齐参照站 FriendsBoard：返回键 + 左对齐大写标题 -->
    <div class="fb-head">
      <div class="fb-back"><router-link to="/" class="back-link">← 返回上一级</router-link></div>
      <div class="fb-title">
        <h1>云端引力</h1>
        <p>那些散落在赛博宇宙各处的有趣灵魂与神经节点。</p>
      </div>
    </div>

    <div class="friends-grid">
      <a class="friend-card-x reveal" v-for="f in friends" :key="f.id" :href="f.url" target="_blank" rel="noopener">
        <div class="fc-glow"></div>
        <div class="fc-top">
          <div class="fc-avatar">
            <img v-if="f.avatar" :src="f.avatar" :alt="f.name" />
            <span v-else>{{ f.name ? f.name.charAt(0) : '友' }}</span>
          </div>
          <div class="fc-name-wrap">
            <h3>{{ f.name }}</h3>
            <div class="fc-online"><i></i>Online</div>
          </div>
        </div>
        <p class="fc-desc">{{ f.description }}</p>
      </a>
    </div>
    <el-empty v-if="friends.length === 0" description="暂无友链" />

    <!-- 申请友链区：文案与格式均由后端 site_profile 驱动 -->
    <div class="fb-apply reveal">
      <h2>✨ 建立神经连接</h2>
      <p class="fb-apply-tip">欢迎各位大佬交换友链！请一键复制下方格式，并通过下方联系方式发送给我：</p>
      <div class="fb-code">
        <pre>{{ applyFormat }}</pre>
        <button @click="copyFormat" :title="copied ? '已复制' : '一键复制'">
          <span v-if="copied">✓</span><span v-else>⧉</span>
        </button>
      </div>
      <div class="fb-contacts">
        <span v-if="profile.contact_email">✉️ {{ profile.contact_email }}</span>
        <span v-if="profile.contact_wechat">💬 微信 {{ profile.contact_wechat }}</span>
        <span v-if="profile.contact_phone">📞 {{ profile.contact_phone }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getFriends, getProfile } from '../api/blog'
import { useReveal } from '../composables/useReveal'

useReveal()
const friends = ref([])
const profile = ref({})

// 申请友链格式：取自后端 site_profile.friend_apply_format，避免前端写死
const applyFormat = ref('')
const fallbackFormat = () => [
  '站点名称：',
  '站点地址：',
  '站点描述：',
  '头像链接：'
].join('\n')

const copied = ref(false)
const copyFormat = async () => {
  try {
    await navigator.clipboard.writeText(applyFormat.value)
    copied.value = true
    setTimeout(() => { copied.value = false }, 2000)
  } catch (e) {}
}

onMounted(async () => {
  friends.value = await getFriends().catch(() => [])
  try {
    const p = await getProfile()
    profile.value = p || {}
    applyFormat.value = (p && p.friend_apply_format) || fallbackFormat()
  } catch (e) {
    applyFormat.value = fallbackFormat()
  }
})
</script>

<style scoped>
.fb-head { margin-bottom: 32px; }
.fb-back { display: flex; justify-content: flex-start; margin-bottom: 20px; }
.fb-back .back-link { margin-bottom: 0; }
.fb-title { text-align: center; }
.fb-title h1 {
  font-family: var(--serif); font-size: clamp(1.8rem, 4vw, 2.5rem); font-weight: 900;
  letter-spacing: .1em; text-transform: uppercase; margin-bottom: 10px; line-height: 1.2;
}
.fb-title p { color: var(--text-soft); font-family: var(--serif); font-size: .98rem; }
@media (min-width: 769px) { .fb-title { text-align: left; } }

/* 参照站：手机 2 列，桌面 3 列 */
.friends-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 14px; }
@media (min-width: 1024px) { .friends-grid { grid-template-columns: repeat(3, 1fr); gap: 22px; } }

.friend-card-x { flex-direction: column; align-items: stretch; gap: 0; padding: 16px; }
@media (min-width: 769px) { .friend-card-x { padding: 24px; } }
.friend-card-x:hover { transform: translateY(-4px) scale(1.02); }
.fc-glow { position: absolute; bottom: -40px; right: -40px; width: 96px; height: 96px; border-radius: 50%; filter: blur(40px); background: color-mix(in srgb, var(--accent) 30%, transparent); opacity: 0; transition: opacity .7s var(--ease); }
.friend-card-x:hover .fc-glow { opacity: 1; }
.fc-top { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; position: relative; z-index: 1; }
@media (min-width: 769px) { .fc-top { gap: 18px; margin-bottom: 14px; } }
.fc-avatar {
  width: 40px; height: 40px; border-radius: 50%; flex-shrink: 0; overflow: hidden; padding: 2px;
  background: linear-gradient(to top right, var(--accent), var(--accent-2));
  transition: transform 1s var(--ease-in-out, cubic-bezier(.4,0,.2,1));
}
@media (min-width: 769px) { .fc-avatar { width: 64px; height: 64px; padding: 4px; } }
/* 参照站：hover 头像旋转一圈 */
.friend-card-x:hover .fc-avatar { transform: rotate(360deg); }
.fc-avatar img { width: 100%; height: 100%; border-radius: 50%; object-fit: cover; background: #fff; }
.fc-avatar span { display: flex; align-items: center; justify-content: center; width: 100%; height: 100%; border-radius: 50%; background: #fff; color: var(--accent); font-family: var(--serif); font-weight: 800; font-size: 1rem; }
.fc-name-wrap { min-width: 0; flex: 1; }
.fc-name-wrap h3 { font-family: var(--serif); font-size: .88rem; font-weight: 700; transition: color .3s var(--ease); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
@media (min-width: 769px) { .fc-name-wrap h3 { font-size: 1.18rem; } }
.friend-card-x:hover .fc-name-wrap h3 { color: var(--accent); }
.fc-online { display: flex; align-items: center; gap: 4px; font-size: .56rem; font-weight: 800; letter-spacing: .12em; text-transform: uppercase; color: var(--accent); opacity: .75; margin-top: 3px; }
@media (min-width: 769px) { .fc-online { font-size: .7rem; } }
.fc-online i { width: 6px; height: 6px; border-radius: 50%; background: var(--accent); animation: pulse 2s ease-in-out infinite; }
.fc-desc { font-family: var(--serif); font-size: .72rem; color: var(--text-soft); line-height: 1.6; position: relative; z-index: 1; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
@media (min-width: 769px) { .fc-desc { font-size: .88rem; line-height: 1.75; -webkit-line-clamp: 3; } }

/* 申请区 */
.fb-apply { margin-top: 56px; max-width: 768px; margin-left: auto; margin-right: auto; text-align: center; background: color-mix(in srgb, var(--bg-elev) 45%, transparent); border: 1px solid var(--border); backdrop-filter: blur(16px); border-radius: var(--radius-lg); padding: 28px 24px; box-shadow: var(--shadow); }
@media (min-width: 769px) { .fb-apply { padding: 34px 40px; } }
.fb-apply h2 { font-family: var(--serif); font-size: 1.15rem; font-weight: 900; letter-spacing: .08em; margin-bottom: 12px; }
@media (min-width: 769px) { .fb-apply h2 { font-size: 1.6rem; } }
.fb-apply-tip { font-family: var(--serif); font-size: .85rem; color: var(--text-soft); margin-bottom: 18px; }
@media (min-width: 769px) { .fb-apply-tip { font-size: .98rem; } }
.fb-code { position: relative; max-width: 448px; margin: 0 auto; background: var(--bg-soft); border: 1px solid var(--border); border-radius: 14px; padding: 16px 18px; text-align: left; overflow: hidden; }
.fb-code pre { font-family: Consolas, monospace; font-size: .74rem; color: var(--text); white-space: pre-wrap; word-break: break-all; padding-right: 26px; }
@media (min-width: 769px) { .fb-code pre { font-size: .86rem; } }
.fb-code button { position: absolute; top: 10px; right: 10px; width: 28px; height: 28px; border-radius: 8px; border: none; cursor: pointer; background: var(--bg-elev); color: var(--text-soft); box-shadow: var(--shadow); transition: background .3s var(--ease), color .3s var(--ease); }
.fb-code button:hover { background: var(--accent); color: #fff; }
.fb-contacts { margin-top: 22px; display: flex; flex-wrap: wrap; gap: 10px 20px; justify-content: center; font-size: .82rem; color: var(--text-soft); }
@media (prefers-reduced-motion: reduce) { .fc-avatar { transition: none; } }
</style>
