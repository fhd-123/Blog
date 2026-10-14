<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h2 style="font-family:var(--serif);">友链管理</h2>
      <el-button type="primary" @click="dialog = true">+ 新增友链</el-button>
    </div>
    <el-table :data="list" border v-loading="loading">
      <el-table-column prop="name" label="名称" width="160" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="url" label="链接" min-width="200" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="edit(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="del(row.id)">
            <template #reference><el-button size="small" type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog" :title="form.id ? '编辑友链' : '新增友链'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
        <el-form-item label="链接"><el-input v-model="form.url" /></el-form-item>
        <el-form-item label="头像"><el-input v-model="form.avatar" placeholder="单字或图片 URL" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminGetFriends, adminCreateFriend, adminUpdateFriend, adminDeleteFriend } from '../../api/blog'

const list = ref([])
const loading = ref(false)
const dialog = ref(false)
const form = ref({ id: null, name: '', description: '', url: '', avatar: '', sort: 0 })

const load = async () => { loading.value = true; try { list.value = await adminGetFriends() } catch (e) {} finally { loading.value = false } }
const edit = (row) => { form.value = { ...row }; dialog.value = true }
const del = async (id) => { try { await adminDeleteFriend(id); ElMessage.success('已删除'); load() } catch (e) {} }
const save = async () => {
  try {
    if (form.value.id) await adminUpdateFriend(form.value)
    else await adminCreateFriend(form.value)
    ElMessage.success('已保存'); dialog.value = false; load()
  } catch (e) {}
}
onMounted(load)
</script>
