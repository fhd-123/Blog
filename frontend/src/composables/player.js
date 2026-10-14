import { reactive, ref } from 'vue'
import { getMusics } from '../api/blog'

// 全局单例播放器状态（模块级，所有组件共享）
const state = reactive({
  list: [],
  index: 0,
  playing: false,
  ready: false
})
const audio = ref(null)
let initialized = false

function ensureAudio() {
  if (!audio.value) audio.value = new Audio()
  return audio.value
}

async function init() {
  if (initialized) return
  initialized = true
  try {
    const list = await getMusics()
    state.list = list || []
    state.ready = true
    const a = ensureAudio()
    a.addEventListener('ended', () => next(true))
    a.addEventListener('play', () => { state.playing = true })
    a.addEventListener('pause', () => { state.playing = false })
    if (state.list.length) load(state.index, false)
  } catch (e) { state.ready = true }
}

function load(i, autoplay = true) {
  if (!state.list.length) return
  state.index = (i + state.list.length) % state.list.length
  const m = state.list[state.index]
  const a = ensureAudio()
  a.src = m.url || ''
  a.loop = !m.url // 无真实音频时循环静音占位（不发声）
  if (autoplay) try { a.play().catch(() => {}) } catch (e) {}
}

function play() {
  if (!state.list.length) return
  const m = state.list[state.index]
  if (!m.url) { state.playing = !state.playing; return } // 无音频：仅切换 UI 状态
  const a = ensureAudio()
  if (a.paused) try { a.play().catch(() => {}) } catch (e) {}
  else a.pause()
}
function toggle() {
  if (!state.ready) { init().then(play); return }
  play()
}
function select(i) { init().then(() => load(i, true)) }
function next(auto = false) {
  if (!state.list.length) return
  const a = ensureAudio()
  const wasPlaying = state.playing || auto
  load(state.index + 1, wasPlaying)
}
function prev() {
  if (!state.list.length) return
  const wasPlaying = state.playing
  load(state.index - 1, wasPlaying)
}

export function usePlayer() {
  return { state, toggle, select, next, prev, init, current: () => state.list[state.index] || {} }
}
