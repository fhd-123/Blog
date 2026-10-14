<template>
  <div class="app-container">
    <div class="archive-hero">
      <router-link to="/" class="back-link">← 返回上一级</router-link>
      <span class="eyebrow">Music · 音乐</span>
      <h1>音乐</h1>
      <p>写代码时循环的那几首。</p>
    </div>

    <div class="music-list" v-loading="loading">
      <div class="music-row reveal" v-for="(m, i) in songs" :key="m.id" @click="play(i)">
        <span class="music-idx">{{ String(i + 1).padStart(2, '0') }}</span>
        <span class="music-emoji">{{ m.emoji || '🎧' }}</span>
        <div class="music-meta">
          <div class="music-name">{{ m.name }}</div>
          <div class="music-artist">{{ m.artist }}</div>
        </div>
        <span class="music-tag">{{ m.mood }}</span>
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
const { select, init } = usePlayer()
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
