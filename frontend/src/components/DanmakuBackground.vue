<template>
  <div v-if="items.length" class="danmaku-bg" aria-hidden="true">
    <div
      v-for="it in items"
      :key="it.id"
      class="danmaku-item"
      :style="{ top: it.top + '%', animationDuration: it.duration + 's', animationDelay: it.delay + 's' }"
    >{{ it.text }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProfile } from '../api/blog'

// 对齐参照站 DanmakuBackground：内容取自后端 site_profile.danmaku_list，
// 非写死；为空则不渲染。
const items = ref([])

onMounted(async () => {
  try {
    const p = await getProfile()
    const raw = p?.danmaku_list
    let list = []
    if (Array.isArray(raw)) list = raw
    else if (typeof raw === 'string' && raw.trim()) list = JSON.parse(raw)
    if (!list.length) return

    const count = 15
    const generated = []
    for (let i = 0; i < count; i++) {
      generated.push({
        id: i,
        text: list[Math.floor(Math.random() * list.length)],
        top: Math.random() * 80 + 10,
        duration: Math.random() * 20 + 25,
        delay: Math.random() * 20
      })
    }
    items.value = generated
  } catch (e) {
    items.value = []
  }
})
</script>

<style scoped>
.danmaku-bg { position: fixed; top: 112px; left: 0; right: 0; height: 30vh; overflow: hidden; pointer-events: none; z-index: 0; }
.danmaku-item {
  position: absolute; white-space: nowrap; font-weight: 700; font-size: 1.05rem; letter-spacing: .1em;
  color: color-mix(in srgb, var(--text) 22%, transparent);
  user-select: none; animation-name: float-left; animation-timing-function: linear; animation-iteration-count: infinite;
}
@keyframes float-left {
  0% { right: -100%; transform: translateX(100%); }
  100% { right: 100%; transform: translateX(-100%); }
}
@media (prefers-reduced-motion: reduce) { .danmaku-item { animation: none; display: none; } }
</style>
