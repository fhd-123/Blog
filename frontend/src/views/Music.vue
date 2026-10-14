<template>
  <div class="app-container">
    <div class="archive-hero">
      <router-link to="/" class="back-link">← 返回上一级</router-link>
      <span class="eyebrow">Music · 音乐</span>
      <h1>音乐</h1>
      <p>写代码时循环的那几首。</p>
    </div>

    <div class="music-list" v-loading="loading">
      <div class="music-row reveal" v-for="(m, i) in songs" :key="m.id"
           :class="{ active: i === playerState.index, playing: i === playerState.index && playerState.playing }"
           @click="play(i)">
        <span class="music-idx">{{ String(i + 1).padStart(2, '0') }}</span>
        <span class="music-emoji">{{ m.emoji || '🎧' }}</span>
        <div class="music-meta">
          <div class="music-name">{{ m.name }}</div>
          <div class="music-artist">{{ m.artist }}</div>
        </div>
        <span class="music-tag">{{ m.mood }}</span>
        <span v-if="i === playerState.index && playerState.playing" class="music-wave">
          <i></i><i></i><i></i>
        </span>
      </div>
    </div>
    <el-empty v-if="!loading && songs.length === 0" description="暂无音乐" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getMusics } from '../api/blog'
import { useReveal } from '../composables/useReveal'
import { usePlayer } from '../composables/player'

useReveal()
const { select, init, state: playerState } = usePlayer()
const songs = ref([])
const loading = ref(false)
onMounted(async () => {
  loading.value = true
  try { songs.value = await getMusics() } catch (e) {}
  finally { loading.value = false }
  init().catch(() => {})
})
const play = (i) => select(i)
</script>

<style scoped>
.music-row { display: flex; align-items: center; gap: 16px; position: relative; }
.music-row.active { border-color: var(--accent); background: var(--accent-soft); }
.music-row.active .music-name { color: var(--accent); }
.music-wave { display: flex; align-items: flex-end; gap: 3px; height: 16px; flex-shrink: 0; }
.music-wave i { width: 3px; background: var(--accent); border-radius: 2px; animation: musicWave .9s ease-in-out infinite; }
.music-wave i:nth-child(1) { height: 40%; animation-delay: 0s; }
.music-wave i:nth-child(2) { height: 100%; animation-delay: .15s; }
.music-wave i:nth-child(3) { height: 60%; animation-delay: .3s; }
@keyframes musicWave { 0%, 100% { transform: scaleY(.35); } 50% { transform: scaleY(1); } }
.music-wave i { transform-origin: bottom; }
@media (prefers-reduced-motion: reduce) { .music-wave i { animation: none; height: 60%; } }
</style>
