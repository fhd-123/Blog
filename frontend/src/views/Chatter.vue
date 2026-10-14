<template>
  <div class="app-container">
    <div class="archive-hero">
      <h1>杂谈</h1>
      <p>技术之外，随手写的随笔。</p>
    </div>

    <div class="chatter-list" v-loading="loading">
      <router-link v-for="c in essays" :key="c.id" to="/archive" class="chatter-card">
        <div class="chatter-date">{{ c.date }}</div>
        <h3>{{ c.title }}</h3>
        <p>{{ c.summary }}</p>
        <div class="tags"><span v-for="t in (c.tags || '').split(',')" :key="t" v-if="t">#{{ t }}</span></div>
      </router-link>
    </div>
    <el-empty v-if="!loading && essays.length === 0" description="暂无随笔" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getEssays } from '../api/blog'

const essays = ref([])
const loading = ref(false)
onMounted(async () => { loading.value = true; try { essays.value = await getEssays() } catch (e) {} finally { loading.value = false } })
</script>
