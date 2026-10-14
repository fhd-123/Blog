<template>
  <div class="app-container">
    <div class="archive-hero">
      <router-link to="/" class="back-link">← 返回上一级</router-link>
      <span class="eyebrow">Photowall · 照片墙</span>
      <h1>照片墙</h1>
      <p>把值得收藏的光影，收进同一面墙。</p>
    </div>

    <div class="photowall-grid" v-loading="loading">
      <div class="photo-card reveal" v-for="ph in photos" :key="ph.id">
        <div class="photo-thumb" :style="thumbStyle(ph)">
          <img v-if="ph.imageUrl" :src="ph.imageUrl" :alt="ph.caption" loading="lazy"
               @error="fail(ph.id)" v-show="!failed[ph.id]" />
          <template v-if="!ph.imageUrl || failed[ph.id]">
            <span class="photo-emoji">{{ ph.emoji || '🌄' }}</span>
          </template>
        </div>
        <div class="photo-cap"><b>{{ ph.caption }}</b><span>{{ ph.date }}</span></div>
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
// 图片加载失败时回退到 emoji/渐变，避免碎图
const failed = ref({})
const fail = (id) => { failed.value[id] = true }
const thumbStyle = (ph) => (ph.imageUrl && !failed.value[ph.id])
  ? {}
  : { background: ph.gradient || 'linear-gradient(135deg, var(--accent), var(--accent-2))' }
onMounted(async () => { loading.value = true; try { photos.value = await getPhotos() } catch (e) {} finally { loading.value = false } })
</script>

<style scoped>
.photo-thumb { position: relative; }
.photo-thumb img { width: 100%; height: 100%; object-fit: cover; position: absolute; inset: 0; }
.photo-emoji { font-size: 2.8rem; }
.photo-cap { flex-direction: row !important; justify-content: space-between; align-items: center; gap: 10px; }
</style>
