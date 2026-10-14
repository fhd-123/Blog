<template>
  <div class="app-container">
    <div class="projects-hero">
      <router-link to="/" class="back-link">← 返回上一级</router-link>
      <span class="eyebrow">Projects · 项目矩阵</span>
      <h1>Projects Matrix</h1>
      <p>开源项目、毕设与课程实践的技术折腾记录。</p>
    </div>

    <div class="projects-grid-x" v-loading="loading">
      <a v-for="pr in projects" :key="pr.id" class="project-card-x reveal" :href="pr.url" target="_blank" rel="noopener">
        <div class="project-icon">{{ pr.icon || '🚀' }}</div>
        <h3>{{ pr.title }}</h3>
        <p>{{ pr.description }}</p>
        <div class="tags" style="margin-bottom:14px;">
          <span v-for="t in (pr.tags || '').split(',').filter(Boolean)" :key="t">{{ t }}</span>
        </div>
        <div class="project-link">查看详情 →</div>
      </a>
    </div>
    <el-empty v-if="!loading && projects.length === 0" description="暂无项目" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getProjects } from '../api/blog'
import { useReveal } from '../composables/useReveal'

useReveal()
const projects = ref([])
const loading = ref(false)
onMounted(async () => { loading.value = true; try { projects.value = await getProjects() } catch (e) {} finally { loading.value = false } })
</script>
