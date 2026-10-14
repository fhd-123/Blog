<template>
  <div class="app-container">
    <div class="page-hero">
      <router-link to="/" class="back-link">← 返回上一级</router-link>
      <span class="eyebrow">About · 关于我</span>
      <h1>{{ p.resume_name || '关于我' }}</h1>
      <p>{{ p.resume_title || '' }} · {{ p.slogan || '' }}</p>
    </div>

    <div class="about-grid">
      <div>
        <section class="section" style="margin-top:0;">
          <h2 class="section-title"><span class="title-bar"></span>个人简介</h2>
          <p class="about-quote reveal">{{ p.slogan ? `“${p.slogan}”` : '' }}</p>
          <div class="widget reveal">
            <p>{{ p.about_intro || '暂无简介' }}</p>
            <ul class="cat-list" style="border:none;margin-top:14px;">
              <li v-if="p.contact_phone"><span class="name">📞 电话</span><a :href="'tel:' + p.contact_phone">{{ p.contact_phone }}</a></li>
              <li v-if="p.contact_email"><span class="name">✉️ 邮箱</span><a :href="'mailto:' + p.contact_email">{{ p.contact_email }}</a></li>
              <li v-if="p.resume_education"><span class="name">🎓 学历</span>{{ p.resume_education }}</li>
              <li v-if="p.resume_title"><span class="name">🎯 意向</span>{{ p.resume_title }}</li>
              <li v-if="p.contact_wechat"><span class="name">💬 微信</span>{{ p.contact_wechat }}</li>
              <li><span class="name">💻 GitHub</span><a href="https://github.com/fhd-123" target="_blank" rel="noopener">fhd-123</a></li>
            </ul>
          </div>
        </section>

        <section class="section">
          <h2 class="section-title"><span class="title-bar"></span>实习与项目经历</h2>
          <div class="widget reveal" v-for="(pr, i) in projects" :key="i" style="margin-bottom:18px;">
            <h3 class="widget-title" style="border:none;padding:0;">{{ pr.title }} <small style="color:var(--text-soft);font-weight:400;">{{ pr.time }}</small></h3>
            <p class="widget-text">{{ pr.role }}</p>
            <ul class="cat-list" style="border:none;">
              <li v-for="(point, j) in pr.points" :key="j"><span class="name" style="width:auto;">•</span>{{ point }}</li>
            </ul>
          </div>
        </section>

        <section class="section">
          <h2 class="section-title"><span class="title-bar"></span>教育经历与荣誉</h2>
          <div class="widget reveal" v-for="(e, i) in education" :key="i">
            <h3 class="widget-title" style="border:none;padding:0;">{{ e.title }}</h3>
            <p class="widget-text">{{ e.desc }}</p>
            <div class="tags" style="margin-top:12px;">
              <span v-for="tag in e.tags" :key="tag">🏆 {{ tag }}</span>
            </div>
          </div>
        </section>
      </div>

      <aside class="sidebar">
        <div class="about-card reveal">
          <div class="avatar-lg">{{ (p.resume_name || '傅').charAt(0) }}</div>
          <div class="name">{{ p.resume_name || '傅浩东' }}</div>
          <div class="role">{{ p.resume_title || 'Java 后端开发工程师' }}</div>
          <p class="widget-text" style="font-size:.86rem;">{{ p.resume_base || '江西南昌 · 本科 · 软件工程' }}</p>
          <div class="tags" style="margin-top:10px;">
            <span v-for="s in (p.resume_skills || '').split('/').filter(Boolean)" :key="s">{{ s.trim() }}</span>
          </div>
        </div>
        <div class="widget reveal">
          <h3 class="widget-title">专业技能</h3>
          <div v-for="sk in skills" :key="sk.t" style="margin-bottom:14px;">
            <div style="font-weight:600;font-family:var(--serif);">{{ sk.t }}</div>
            <p class="widget-text" style="margin:4px 0 8px;font-size:.86rem;">{{ sk.d }}</p>
            <div class="tags"><span v-for="t in sk.tags" :key="t">{{ t }}</span></div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getProfile } from '../api/blog'
import { useReveal } from '../composables/useReveal'

useReveal()
const p = ref({})
const skills = ref([])
const projects = ref([])
const education = ref([])

const parse = (v, fallback) => { try { return JSON.parse(v) } catch (e) { return fallback } }

onMounted(async () => {
  const data = await getProfile().catch(() => ({}))
  p.value = data
  skills.value = parse(data.about_skills, [])
  projects.value = parse(data.about_projects, [])
  education.value = parse(data.about_education, [])
})
</script>
