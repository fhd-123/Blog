<template>
  <div class="search-bar reveal">
    <div class="sb-field">
      <span class="sb-icon">🔍</span>
      <input
        v-model="kw"
        type="text"
        placeholder="搜索文章 / 杂谈 / 项目 / 照片 / 音乐…"
        @input="onInput"
        @focus="open = true"
        @keyup.enter="doSearch"
        @keyup.esc="open = false"
      />
      <button v-if="kw" class="sb-clear" @click="clear" aria-label="清空">✕</button>
    </div>

    <div v-if="open && (result || searching || kw)" class="sb-panel">
      <div v-if="searching" class="sb-empty">检索中…</div>
      <template v-else-if="result">
        <div v-if="total === 0" class="sb-empty">没有找到与「{{ keyword }}」相关的内容</div>
        <template v-else>
          <div v-for="g in groups" :key="g.key" class="sb-group">
            <div class="sb-group-title">{{ g.label }}<span>{{ g.count }}</span></div>
            <div
              v-for="it in g.items"
              :key="g.key + it.id"
              class="sb-item"
              @click="go(g, it)"
            >
              <span class="sb-item-icon">{{ g.icon }}</span>
              <span class="sb-item-title">{{ it.display }}</span>
              <span class="sb-item-date">{{ it.date }}</span>
            </div>
          </div>
        </template>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { searchAll } from '../api/blog'

const router = useRouter()
const kw = ref('')
const open = ref(false)
const searching = ref(false)
const result = ref(null)
const keyword = ref('')

let timer = null
const onInput = () => {
  clearTimeout(timer)
  if (!kw.value.trim()) { result.value = null; open.value = false; return }
  timer = setTimeout(doSearch, 320)
}

const doSearch = async () => {
  const q = kw.value.trim()
  if (!q) { result.value = null; return }
  searching.value = true
  open.value = true
  try {
    keyword.value = q
    result.value = await searchAll(q)
  } catch (e) {
    result.value = { articles: [], essays: [], projects: [], photos: [], musics: [] }
  } finally {
    searching.value = false
  }
}

const clear = () => { kw.value = ''; result.value = null; open.value = false }

const cut = (s) => (s || '').replace(/<[^>]+>/g, '').slice(0, 60)

const groups = computed(() => {
  const r = result.value || {}
  const map = [
    { key: 'articles', label: '文章', icon: '📄', route: (it) => '/articles/' + it.id,
      items: (r.articles || []).map(a => ({ id: a.id, display: a.title, date: (a.createTime || '').slice(0, 10) })) },
    { key: 'essays', label: '杂谈', icon: '📝', route: () => '/chatter',
      items: (r.essays || []).map(e => ({ id: e.id, display: e.title, date: e.date })) },
    { key: 'projects', label: '项目', icon: '🛠', route: () => '/projects',
      items: (r.projects || []).map(p => ({ id: p.id, display: p.title, date: '' })) },
    { key: 'photos', label: '照片', icon: '📷', route: () => '/photowall',
      items: (r.photos || []).map(p => ({ id: p.id, display: cut(p.caption), date: p.date })) },
    { key: 'musics', label: '音乐', icon: '🎵', route: () => '/music',
      items: (r.musics || []).map(m => ({ id: m.id, display: m.name + ' — ' + m.artist, date: '' })) }
  ]
  return map.map(g => ({ ...g, count: g.items.length })).filter(g => g.count > 0)
})

const total = computed(() => groups.value.reduce((s, g) => s + g.count, 0))

const go = (g, it) => {
  open.value = false
  router.push(g.route(it))
}

// 点击外部关闭
if (typeof window !== 'undefined') {
  window.addEventListener('click', (e) => {
    if (!e.target.closest('.search-bar')) open.value = false
  })
}
watch(kw, (v) => { if (!v.trim()) open.value = false })
</script>

<style scoped>
.search-bar { position: relative; z-index: 60; }
.sb-field {
  display: flex; align-items: center; gap: 10px; padding: 0 16px; height: 52px;
  border-radius: 16px; border: 1px solid var(--border);
  background: color-mix(in srgb, var(--bg-elev) 75%, transparent);
  backdrop-filter: blur(14px) saturate(140%); box-shadow: var(--shadow);
  transition: border-color .25s var(--ease), box-shadow .25s var(--ease);
}
.sb-field:focus-within { border-color: var(--accent); box-shadow: 0 0 0 4px var(--accent-soft); }
.sb-icon { font-size: 1rem; opacity: .7; }
.sb-field input { flex: 1; border: none; outline: none; background: transparent; color: var(--text); font-size: .96rem; font-family: var(--sans); }
.sb-field input::placeholder { color: var(--text-soft); }
.sb-clear { border: none; background: transparent; color: var(--text-soft); cursor: pointer; font-size: .9rem; }
.sb-panel {
  position: absolute; top: 58px; left: 0; right: 0; max-height: 420px; overflow-y: auto;
  border-radius: 16px; border: 1px solid var(--border); background: var(--bg-elev);
  box-shadow: var(--shadow-hover); padding: 12px; animation: cardIn .25s var(--ease) both;
}
.sb-empty { text-align: center; color: var(--text-soft); font-size: .9rem; padding: 22px 0; }
.sb-group { margin-bottom: 10px; }
.sb-group-title { font-size: .74rem; font-weight: 700; color: var(--accent); letter-spacing: .12em; text-transform: uppercase; margin: 8px 6px 6px; display: flex; justify-content: space-between; }
.sb-group-title span { color: var(--text-soft); }
.sb-item { display: flex; align-items: center; gap: 10px; padding: 9px 10px; border-radius: 10px; cursor: pointer; transition: background .2s var(--ease), transform .2s var(--ease); }
.sb-item:hover { background: var(--accent-soft); transform: translateX(4px); }
.sb-item-icon { font-size: .95rem; }
.sb-item-title { flex: 1; font-size: .9rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.sb-item-date { font-size: .76rem; color: var(--text-soft); flex-shrink: 0; }
</style>
