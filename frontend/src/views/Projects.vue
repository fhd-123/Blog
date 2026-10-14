<template>
  <div class="app-container">
    <!-- 对齐参照站 ProjectsBoard：返回键 + 左对齐大标题 + 居中搜索胶囊 -->
    <div class="pb-head">
      <div class="pb-back"><router-link to="/" class="back-link">← 返回上一级</router-link></div>
      <div class="pb-title">
        <h1>Projects Matrix</h1>
        <p>开源项目、科研代码与实验室折腾记录。</p>
      </div>
    </div>

    <div class="pb-search">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
      <input v-model="q" type="text" placeholder="搜索项目名称、描述或技术栈..." />
    </div>

    <div class="projects-grid-x" v-loading="loading">
      <a v-for="pr in filtered" :key="pr.id" class="project-card-x reveal" :href="pr.url" target="_blank" rel="noopener">
        <div class="pc-glow"></div>
        <div class="pc-head">
          <div class="pc-head-l">
            <span class="pc-icon">{{ pr.icon || '🚀' }}</span>
            <h3>{{ pr.title }}</h3>
          </div>
          <svg class="pc-gh" viewBox="0 0 24 24" fill="currentColor"><path fill-rule="evenodd" clip-rule="evenodd" d="M12 2C6.477 2 2 6.477 2 12c0 4.42 2.865 8.166 6.839 9.489.5.092.682-.217.682-.482 0-.237-.008-.866-.013-1.7-2.782.603-3.369-1.34-3.369-1.34-.454-1.156-1.11-1.462-1.11-1.462-.908-.62.069-.608.069-.608 1.003.07 1.531 1.03 1.531 1.03.892 1.529 2.341 1.087 2.91.831.092-.646.35-1.086.636-1.336-2.22-.253-4.555-1.11-4.555-4.943 0-1.091.39-1.984 1.029-2.683-.103-.253-.446-1.27.098-2.647 0 0 .84-.269 2.75 1.025A9.578 9.578 0 0112 6.836c.85.004 1.705.114 2.504.336 1.909-1.294 2.747-1.025 2.747-1.025.546 1.379.203 2.394.1 2.647.64.699 1.028 1.592 1.028 2.683 0 3.842-2.339 4.687-4.566 4.935.359.309.678.919.678 1.852 0 1.336-.012 2.415-.012 2.743 0 .267.18.578.688.48C19.138 20.161 22 16.418 22 12c0-5.523-4.477-10-10-10z" /></svg>
        </div>
        <p class="pc-desc">{{ pr.description }}</p>
        <div class="pc-tags">
          <span v-for="t in splitTags(pr.tags)" :key="t">{{ t }}</span>
        </div>
      </a>
    </div>
    <div v-if="!loading && filtered.length === 0" class="pb-empty">
      云端尚未建立代号为 [{{ q }}] 的档案...
    </div>
    <el-empty v-if="!loading && projects.length === 0" description="暂无项目" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getProjects } from '../api/blog'
import { useReveal } from '../composables/useReveal'

useReveal()
const projects = ref([])
const loading = ref(false)

// 对齐参照站 ProjectsBoard：本地实时过滤（名称/描述/技术栈）
const q = ref('')
const splitTags = (v) => (v || '').split(/[,，]/).map(s => s.trim()).filter(Boolean)
const filtered = computed(() => {
  const kw = q.value.trim().toLowerCase()
  if (!kw) return projects.value
  return projects.value.filter(p =>
    (p.title || '').toLowerCase().includes(kw) ||
    (p.description || '').toLowerCase().includes(kw) ||
    splitTags(p.tags).some(t => t.toLowerCase().includes(kw))
  )
})

onMounted(async () => { loading.value = true; try { projects.value = await getProjects() } catch (e) {} finally { loading.value = false } })
</script>

<style scoped>
/* 头部：返回键独占一行，标题桌面左对齐 / 手机居中 */
.pb-head { margin-bottom: 32px; }
.pb-back { display: flex; justify-content: flex-start; margin-bottom: 20px; }
.pb-back .back-link { margin-bottom: 0; }
.pb-title { text-align: center; }
.pb-title h1 {
  font-family: var(--serif); font-size: clamp(1.9rem, 4vw, 2.5rem); font-weight: 900;
  letter-spacing: .1em; text-transform: uppercase; margin-bottom: 12px; line-height: 1.2;
}
.pb-title p { color: var(--text-soft); font-family: var(--serif); }
@media (min-width: 769px) { .pb-title { text-align: left; } }

/* 居中搜索胶囊 */
.pb-search { position: relative; max-width: 512px; margin: 0 auto 44px; }
.pb-search svg { position: absolute; left: 16px; top: 50%; transform: translateY(-50%); width: 20px; height: 20px; color: var(--text-soft); }
.pb-search input {
  width: 100%; height: 46px; border-radius: 999px; padding: 0 20px 0 46px;
  border: 1px solid var(--border); background: color-mix(in srgb, var(--bg-elev) 50%, transparent);
  backdrop-filter: blur(12px); box-shadow: var(--shadow); color: var(--text);
  font-family: var(--serif); font-size: .95rem; outline: none; transition: box-shadow .25s var(--ease), border-color .25s var(--ease);
}
.pb-search input::placeholder { color: var(--text-soft); }
.pb-search input:focus { border-color: var(--accent); box-shadow: 0 0 0 3px var(--accent-soft); }

/* 两列卡片矩阵 */
.projects-grid-x { display: grid; grid-template-columns: repeat(2, 1fr); gap: 22px; }
@media (max-width: 768px) { .projects-grid-x { grid-template-columns: 1fr; } }
.project-card-x { display: flex; flex-direction: column; padding: 26px 30px; }
.project-card-x:hover { transform: translateY(-4px); box-shadow: var(--shadow-hover); }
.pc-glow { position: absolute; top: -40px; right: -40px; width: 128px; height: 128px; border-radius: 50%; filter: blur(40px); background: color-mix(in srgb, var(--accent) 12%, transparent); transition: background .7s var(--ease); }
.project-card-x:hover .pc-glow { background: color-mix(in srgb, var(--accent) 25%, transparent); }
.pc-head { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; margin-bottom: 16px; position: relative; z-index: 1; }
.pc-head-l { display: flex; align-items: center; gap: 14px; min-width: 0; }
.pc-icon { font-size: 2.1rem; flex-shrink: 0; }
.pc-head h3 { font-family: var(--serif); font-size: 1.3rem; font-weight: 700; transition: color .3s var(--ease); }
.project-card-x:hover .pc-head h3 { color: var(--accent); }
.pc-gh { width: 26px; height: 26px; color: var(--text-soft); flex-shrink: 0; opacity: .5; transition: opacity .3s var(--ease), color .3s var(--ease); }
.project-card-x:hover .pc-gh { opacity: 1; color: var(--text); }
.pc-desc {
  font-family: var(--serif); font-size: .9rem; color: var(--text-soft); line-height: 1.75;
  margin-bottom: 22px; position: relative; z-index: 1; min-height: 60px;
  display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden;
}
.pc-tags { display: flex; flex-wrap: wrap; gap: 8px; position: relative; z-index: 1; margin-top: auto; }
.pc-tags span {
  font-size: .63rem; font-weight: 800; letter-spacing: .1em; text-transform: uppercase;
  color: var(--accent); background: var(--accent-soft); border: 1px solid color-mix(in srgb, var(--accent) 22%, transparent);
  padding: 4px 10px; border-radius: 6px;
}
.pb-empty { text-align: center; padding: 60px 0; color: var(--text-soft); font-family: var(--serif); }
</style>
