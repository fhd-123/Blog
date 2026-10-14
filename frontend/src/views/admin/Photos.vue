<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h2>照片墙管理</h2>
      <el-button type="primary" @click="open()">+ 新增</el-button>
    </div>

    <el-table :data="list" border style="width:100%" v-loading="loading">
      <el-table-column prop="caption" label="标题" />
      <el-table-column prop="date" label="日期" width="130" />
      <el-table-column prop="emoji" label="图标" width="80" />
      <el-table-column prop="gradient" label="渐变(CSS)" show-overflow-tooltip />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="open(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑' : '新增'" width="520px">
      <el-form label-width="70px">
        <el-form-item label="标题"><el-input v-model="form.caption" /></el-form-item>
        <el-form-item label="日期"><el-input v-model="form.date" placeholder="2026-02-14" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.emoji" placeholder="🌄" /></el-form-item>
        <el-form-item label="图片"><el-input v-model="form.imageUrl" placeholder="图片链接 https://... （填了则优先显示图片）" /></el-form-item>
        <el-form-item label="渐变"><el-input v-model="form.gradient" placeholder="无图片时的兜底渐变 linear-gradient(135deg,#6366f1,#a78bfa)" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminGetPhotos, adminCreatePhoto, adminUpdatePhoto, adminDeletePhoto } from '../../api/blog'

const list = ref([])
const loading = ref(false)
const visible = ref(false)
const saving = ref(false)
const form = reactive({ id: null, caption: '', date: '', emoji: '🌄', gradient: '', imageUrl: '', sort: 0 })

const load = async () => { loading.value = true; try { list.value = await adminGetPhotos() } catch (e) {} finally { loading.value = false } }
const open = (row) => {
  Object.assign(form, { id: null, caption: '', date: '', emoji: '🌄', gradient: '', imageUrl: '', sort: 0 })
  if (row) Object.assign(form, row)
  visible.value = true
}
const submit = async () => {
  saving.value = true
  try { if (form.id) await adminUpdatePhoto(form); else await adminCreatePhoto(form); ElMessage.success('已保存'); visible.value = false; load() }
  catch (e) {} finally { saving.value = false }
}
const remove = async (row) => { try { await ElMessageBox.confirm('确定删除？', '提示'); await adminDeletePhoto(row.id); ElMessage.success('已删除'); load() } catch (e) {} }
onMounted(load)
</script>
