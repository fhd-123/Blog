<template>
  <div class="app-container">
    <div class="archive-hero">
      <span class="eyebrow">Chatter · 杂谈</span>
      <h1>杂谈</h1>
      <p>技术之外，随手写的随笔。</p>
    </div>

    <div class="chatter-list" v-loading="loading">
      <article v-for="c in essays" :key="c.id" class="chatter-card reveal">
        <div class="chatter-date">{{ c.date }}</div>
        <h3>{{ c.title }}</h3>
        <p>{{ c.summary }}</p>
        <div class="tags"><span v-for="t in (c.tags || '').split(',').filter(Boolean)" :key="t">#{{ t }}</span></div>
      </article>
    </div>
    <el-empty v-if="!loading && essays.length === 0" description="暂无随笔" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getEssays } from '../api/blog'
import { useReveal } from '../composables/useReveal'

useReveal()
const essays = ref([])
const loading = ref(false)
onMounted(async () => { loading.value = true; try { essays.value = await getEssays() } catch (e) {} finally { loading.value = false } })
</script>
