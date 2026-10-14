<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h2>音乐管理</h2>
      <el-button type="primary" @click="open()">+ 新增</el-button>
    </div>

    <el-table :data="list" border style="width:100%" v-loading="loading">
      <el-table-column prop="emoji" label="图标" width="80" />
      <el-table-column prop="name" label="歌名" />
      <el-table-column prop="artist" label="作者" width="160" />
      <el-table-column prop="mood" label="心情" width="160" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="open(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑' : '新增'" width="480px">
      <el-form label-width="70px">
        <el-form-item label="图标"><el-input v-model="form.emoji" placeholder="🎧" /></el-form-item>
        <el-form-item label="歌名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="作者"><el-input v-model="form.artist" /></el-form-item>
        <el-form-item label="心情"><el-input v-model="form.mood" /></el-form-item>
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
import { adminGetMusics, adminCreateMusic, adminUpdateMusic, adminDeleteMusic } from '../../api/blog'

const list = ref([])
const loading = ref(false)
const visible = ref(false)
const saving = ref(false)
const form = reactive({ id: null, emoji: '🎧', name: '', artist: '', mood: '', sort: 0 })

const load = async () => { loading.value = true; try { list.value = await adminGetMusics() } catch (e) {} finally { loading.value = false } }
const open = (row) => {
  Object.assign(form, { id: null, emoji: '🎧', name: '', artist: '', mood: '', sort: 0 })
  if (row) Object.assign(form, row)
  visible.value = true
}
const submit = async () => {
  saving.value = true
  try { if (form.id) await adminUpdateMusic(form); else await adminCreateMusic(form); ElMessage.success('已保存'); visible.value = false; load() }
  catch (e) {} finally { saving.value = false }
}
const remove = async (row) => { try { await ElMessageBox.confirm('确定删除？', '提示'); await adminDeleteMusic(row.id); ElMessage.success('已删除'); load() } catch (e) {} }
onMounted(load)
</script>
