<template>
  <div class="app-container">
    <div class="archive-hero">
      <span class="eyebrow">Photowall · 照片墙</span>
      <h1>照片墙</h1>
      <p>把值得收藏的光影，收进同一面墙。</p>
    </div>

    <div class="photowall-grid" v-loading="loading">
      <div class="photo-card reveal" v-for="ph in photos" :key="ph.id">
        <div class="photo-thumb" :style="{ background: ph.gradient }">{{ ph.emoji || '🌄' }}</div>
        <div class="photo-cap">{{ ph.caption }} · {{ ph.date }}</div>
      </div>
    </div>
    <el-empty v-if="!loading && photos.length === 0" description="暂无照片" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getPhotos } from '../api/blog'
import { useReveal } from '../composables/useReveal'

useReveal()
const photos = ref([])
const loading = ref(false)
onMounted(async () => { loading.value = true; try { photos.value = await getPhotos() } catch (e) {} finally { loading.value = false } })
</script>
