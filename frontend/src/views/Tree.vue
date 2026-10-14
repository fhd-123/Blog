<template>
  <div class="app-container">
    <div class="archive-hero">
      <h1>灵境</h1>
      <p>一些零散的想法，长成了树。</p>
    </div>

    <div class="tree-wrap" v-loading="loading">
      <div class="tree-node tree-root">
        <span class="tree-emoji">🌳</span>
        <div>
          <div class="tree-title">我的知识森林</div>
          <div class="tree-sub">把学习轨迹按主题分杈</div>
        </div>
      </div>
      <div class="tree-branch" v-for="b in branches" :key="b.id">
        <span class="tree-dot"></span>
        <div class="tree-card">
          <div class="tree-title">{{ b.title }}</div>
          <p class="tree-sub">{{ b.description }}</p>
          <div class="tags"><span v-for="t in (b.tags || '').split(',').filter(Boolean)" :key="t">#{{ t }}</span></div>
        </div>
      </div>
    </div>
    <el-empty v-if="!loading && branches.length === 0" description="暂无内容" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getTree } from '../api/blog'

const branches = ref([])
const loading = ref(false)
onMounted(async () => { loading.value = true; try { branches.value = await getTree() } catch (e) {} finally { loading.value = false } })
</script>
