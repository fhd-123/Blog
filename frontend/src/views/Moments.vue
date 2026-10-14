<template>
  <div class="app-container">
    <div class="archive-hero">
      <span class="eyebrow">Moments · 说说</span>
      <h1>说说</h1>
      <p>随手记下的此刻。</p>
    </div>

    <div class="moments-feeds" v-loading="loading">
      <div class="moment-item reveal" v-for="m in moments" :key="m.id">
        <div class="moment-avatar">{{ (m.author || '傅').charAt(0) }}</div>
        <div class="moment-body">
          <div class="moment-head">
            <span class="moment-name">{{ m.author || '傅浩东' }}</span>
            <span class="moment-time">{{ formatTime(m.time) }}</span>
          </div>
          <p class="moment-text">{{ m.content }}</p>
          <div class="moment-foot">
            <span>♡ {{ m.likes || 0 }}</span><span>💬 {{ m.comments || 0 }}</span>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-if="!loading && moments.length === 0" description="暂无动态" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getMoments } from '../api/blog'
import { useReveal } from '../composables/useReveal'

useReveal()
const moments = ref([])
const loading = ref(false)
const formatTime = (t) => (t ? String(t).replace('T', ' ').slice(0, 16) : '')
onMounted(async () => { loading.value = true; try { moments.value = await getMoments() } catch (e) {} finally { loading.value = false } })
</script>
