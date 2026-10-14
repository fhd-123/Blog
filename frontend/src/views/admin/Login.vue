<template>
  <div class="admin-login">
    <el-card class="login-card">
      <h2 style="font-family:var(--serif);text-align:center;margin-bottom:20px;color:var(--accent);">傅浩东の代码宝藏 · 后台</h2>
      <el-form :model="form" @submit.prevent="login">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="admin" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="admin123" @keyup.enter="login" />
        </el-form-item>
        <el-button type="primary" style="width:100%;" @click="login">登 录</el-button>
      </el-form>
      <p style="text-align:center;margin-top:14px;color:var(--text-soft);font-size:.85rem;">默认账号 admin / admin123</p>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogin } from '../../api/blog'

const router = useRouter()
const form = ref({ username: 'admin', password: 'admin123' })

const login = async () => {
  try {
    const data = await adminLogin(form.value)
    localStorage.setItem('token', data.token)
    localStorage.setItem('username', data.username)
    ElMessage.success('登录成功')
    router.push('/admin/dashboard')
  } catch (e) {}
}
</script>

<style scoped>
.admin-login { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: radial-gradient(900px 500px at 50% 0%, var(--accent-soft), transparent); }
.login-card { width: 360px; padding: 10px; }
</style>
