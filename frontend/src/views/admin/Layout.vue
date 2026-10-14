<template>
  <div class="admin-layout">
    <aside class="admin-side">
      <div class="logo">✦ 傅浩东后台</div>
      <router-link to="/admin/dashboard">📊 仪表盘</router-link>
      <router-link to="/admin/articles">📝 文章管理</router-link>
      <router-link to="/admin/categories">🏷️ 分类与标签</router-link>
      <router-link to="/admin/profile">🪪 站点资料</router-link>
      <router-link to="/admin/projects">🚀 项目管理</router-link>
      <router-link to="/admin/photos">🖼️ 照片墙</router-link>
      <router-link to="/admin/musics">🎵 音乐</router-link>
      <router-link to="/admin/tree">🌳 灵境</router-link>
      <router-link to="/admin/moments">💭 说说</router-link>
      <router-link to="/admin/essays">📒 杂谈</router-link>
      <router-link to="/admin/friends">🔗 友链管理</router-link>
      <router-link to="/admin/comments">💬 评论管理</router-link>
      <a @click="logout" style="margin-top:20px;color:var(--gem-rose);">🚪 退出登录</a>
    </aside>
    <main class="admin-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const logout = async () => {
  try {
    await ElMessageBox.confirm('确定退出登录？', '提示')
    localStorage.removeItem('token')
    router.push('/admin/login')
  } catch (e) {}
}
</script>
