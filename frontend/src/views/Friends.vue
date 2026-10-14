<template>
  <div class="app-container">
    <div class="friends-hero">
      <span class="eyebrow">Friends · 云端引力</span>
      <h1>云端引力</h1>
      <p>那些散落在赛博宇宙各处的有趣灵魂与神经节点。</p>
    </div>

    <div class="friends-grid">
      <a class="friend-card-x reveal" v-for="f in friends" :key="f.id" :href="f.url" target="_blank" rel="noopener">
        <div class="friend-avatar-x">
          <img v-if="f.avatar" :src="f.avatar" :alt="f.name" />
          <span v-else>{{ f.name ? f.name.charAt(0) : '友' }}</span>
        </div>
        <div class="friend-info-x">
          <h3>{{ f.name }} <span class="friend-status">● Online</span></h3>
          <p>{{ f.description }}</p>
          <span>{{ f.url }}</span>
        </div>
      </a>
    </div>
    <el-empty v-if="friends.length === 0" description="暂无友链" />

    <div class="widget apply-card reveal" style="max-width:560px;">
      <h3 class="widget-title">申请友链</h3>
      <p class="widget-text">把你的站点信息发到邮箱 <strong>3375106693@qq.com</strong>，或加我微信/QQ，我会尽快添加～</p>
      <p class="widget-text">📞 17870390643 · ✉️ 3375106693@qq.com</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getFriends } from '../api/blog'
import { useReveal } from '../composables/useReveal'

useReveal()
const friends = ref([])
onMounted(async () => { friends.value = await getFriends().catch(() => []) })
</script>
