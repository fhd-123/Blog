<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
      <h2>说说管理</h2>
      <el-button type="primary" @click="open()">+ 新增</el-button>
    </div>

    <el-table :data="list" border style="width:100%" v-loading="loading">
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="likes" label="赞" width="70" />
      <el-table-column prop="comments" label="评" width="70" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="open(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑' : '新增'" width="520px">
      <el-form label-width="70px">
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="作者"><el-input v-model="form.author" /></el-form-item>
        <el-form-item label="赞"><el-input-number v-model="form.likes" :min="0" /></el-form-item>
        <el-form-item label="评"><el-input-number v-model="form.comments" :min="0" /></el-form-item>
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
import { adminGetMoments, adminCreateMoment, adminUpdateMoment, adminDeleteMoment } from '../../api/blog'

const list = ref([])
const loading = ref(false)
const visible = ref(false)
const saving = ref(false)
const form = reactive({ id: null, content: '', author: '傅浩东', likes: 0, comments: 0 })

const load = async () => { loading.value = true; try { list.value = await adminGetMoments() } catch (e) {} finally { loading.value = false } }
const open = (row) => {
  Object.assign(form, { id: null, content: '', author: '傅浩东', likes: 0, comments: 0 })
  if (row) Object.assign(form, row)
  visible.value = true
}
const submit = async () => {
  saving.value = true
  try { if (form.id) await adminUpdateMoment(form); else await adminCreateMoment(form); ElMessage.success('已保存'); visible.value = false; load() }
  catch (e) {} finally { saving.value = false }
}
const remove = async (row) => { try { await ElMessageBox.confirm('确定删除？', '提示'); await adminDeleteMoment(row.id); ElMessage.success('已删除'); load() } catch (e) {} }
onMounted(load)
</script>
