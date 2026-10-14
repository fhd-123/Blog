<template>
  <div class="app-container">
    <SearchBar class="reveal" />
    <div class="archive-hero">
      <router-link to="/" class="back-link">← 返回上一级</router-link>
      <span class="eyebrow">Archive · 中枢链路矩阵</span>
      <h1>归档与探索</h1>
      <p>总计 {{ total }} 篇技术记录</p>
    </div>

    <div class="archive-tags">
      <span class="archive-tag" :class="{ active: !activeCat }" @click="setCat(null)">全部</span>
      <span v-for="c in categories" :key="c.id" class="archive-tag"
            :class="{ active: activeCat === c.name }" @click="setCat(c.name)">
        {{ c.name }} {{ c.count }}
      </span>
    </div>

    <div class="archive-tags">
      <span class="archive-tag" :class="{ active: !activeTag }" @click="setTag(null)">全部标签</span>
      <span v-for="t in tags" :key="t.id" class="archive-tag"
            :class="{ active: activeTag === t.name }" @click="setTag(t.name)">
        {{ t.name }} {{ t.count }}
      </span>
    </div>

    <el-skeleton :loading="loading" :rows="6" animated>
      <div>
        <div class="archive-grid">
          <router-link v-for="a in list" :key="a.id" :to="'/articles/' + a.id" class="archive-card reveal" @mousemove="spot">
            <div class="archive-cover">{{ a.title.charAt(0) }}</div>
            <div class="archive-body">
              <div class="archive-date">{{ formatDate(a.createTime) }} · {{ a.categoryName || '未分类' }}</div>
              <h3>{{ a.title }}</h3>
              <div class="tags"><span v-for="t in a.tags" :key="t.id">#{{ t.name }}</span></div>
            </div>
          </router-link>
        </div>
        <el-empty v-if="!loading && list.length === 0" description="暂无文章" />
        <el-pagination v-if="total > pageSize" layout="prev, pager, next" :total="total"
          :page-size="pageSize" :current-page="page" @current-change="(p) => { page = p; load() }"
          background style="margin-top: 24px; justify-content: center;" />
      </div>
    </el-skeleton>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getArticles, getTags, getCategories } from '../api/blog'
import { useReveal } from '../composables/useReveal'
import SearchBar from '../components/SearchBar.vue'

useReveal()
const route = useRoute()
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(12)
const loading = ref(false)
const tags = ref([])
const categories = ref([])
const activeTag = ref(route.query.tag || null)
const activeCat = ref(route.query.category || null)

const formatDate = (d) => (d ? d.slice(0, 10) : '')
const spot = (e) => { const el = e.currentTarget; const r = el.getBoundingClientRect(); el.style.setProperty('--mx', `${e.clientX - r.left}px`); el.style.setProperty('--my', `${e.clientY - r.top}px`) }
const setTag = (name) => { activeTag.value = name; page.value = 1; load() }
const setCat = (name) => { activeCat.value = name; page.value = 1; load() }

const load = async () => {
  loading.value = true
  try {
    const res = await getArticles({
      page: page.value, pageSize: pageSize.value,
      tag: activeTag.value || undefined,
      category: activeCat.value || undefined
    })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e) {} finally { loading.value = false }
}

// 支持从首页侧边栏带参数直达（/archive?tag=xxx 或 ?category=xxx）
watch(() => route.query, (q) => {
  activeTag.value = q.tag || null
  activeCat.value = q.category || null
  page.value = 1
  load()
})

onMounted(async () => {
  ;[tags.value, categories.value] = await Promise.all([
    getTags().catch(() => []),
    getCategories().catch(() => [])
  ])
  load()
})
</script>
