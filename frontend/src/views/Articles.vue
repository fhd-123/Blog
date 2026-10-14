<template>
  <div class="app-container">
    <div class="page-hero">
      <h1>文章</h1>
      <p>记录 Java 后端、数据库、安全与全栈实践的点滴</p>
    </div>

    <div class="layout">
      <div class="content">
        <div class="filter-bar">
          <button class="filter-btn" :class="{ active: !activeCat }" @click="setCategory(null)">全部</button>
          <button v-for="c in categories" :key="c.id" class="filter-btn"
                  :class="{ active: activeCat === c.id }" @click="setCategory(c.id)">
            {{ c.name }} ({{ c.count }})
          </button>
        </div>
        <p class="filter-info" v-if="activeTag">标签筛选：{{ activeTag }} · <a @click="setTag(null)">清除</a></p>

        <el-skeleton :loading="loading" :rows="6" animated>
          <div>
            <router-link v-for="a in list" :key="a.id" :to="'/articles/' + a.id" class="post-card">
              <div class="post-meta">
                <span class="post-cat">{{ a.categoryName || '未分类' }}</span>
                <span class="post-date">{{ formatDate(a.createTime) }}</span>
              </div>
              <h3>{{ a.title }}</h3>
              <p>{{ a.summary }}</p>
              <div class="tags"><span v-for="t in a.tags" :key="t.id">{{ t.name }}</span></div>
            </router-link>
            <el-empty v-if="!loading && list.length === 0" description="暂无文章" />
            <el-pagination
              v-if="total > pageSize"
              layout="prev, pager, next"
              :total="total" :page-size="pageSize" :current-page="page"
              @current-change="handlePage" background style="margin-top: 20px; justify-content: center;" />
          </div>
        </el-skeleton>
      </div>

      <aside class="sidebar">
        <div class="widget">
          <h3 class="widget-title">标签</h3>
          <div class="tag-cloud">
            <span v-for="t in tags" :key="t.id" @click="setTag(t.name)">{{ t.name }} ({{ t.count }})</span>
          </div>
        </div>
        <div class="widget">
          <h3 class="widget-title">分类</h3>
          <ul class="cat-list">
            <li v-for="c in categories" :key="c.id" @click="setCategory(c.id)">
              <span class="name">{{ c.name }}</span><span class="count">{{ c.count }}</span>
            </li>
          </ul>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticles, getCategories, getTags } from '../api/blog'

const route = useRoute()
const router = useRouter()
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const categories = ref([])
const tags = ref([])
const activeCat = ref(null)
const activeTag = ref(null)

const formatDate = (d) => (d ? d.slice(0, 10) : '')
const setCategory = (id) => { activeCat.value = id; page.value = 1; load() }
const setTag = (name) => { activeTag.value = name; page.value = 1; load() }
const handlePage = (p) => { page.value = p; load() }

const load = async () => {
  loading.value = true
  try {
    const res = await getArticles({
      page: page.value, pageSize: pageSize.value,
      categoryId: activeCat.value || undefined,
      tag: activeTag.value || undefined
    })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e) {} finally { loading.value = false }
}

onMounted(async () => {
  categories.value = await getCategories().catch(() => [])
  tags.value = await getTags().catch(() => [])
  if (route.query.categoryId) activeCat.value = Number(route.query.categoryId)
  if (route.query.tag) activeTag.value = route.query.tag
  load()
})
watch(() => route.query, () => {
  activeCat.value = route.query.categoryId ? Number(route.query.categoryId) : null
  activeTag.value = route.query.tag || null
  page.value = 1
  load()
})
</script>
