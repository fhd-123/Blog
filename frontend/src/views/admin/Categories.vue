<template>
  <div>
    <h2 style="font-family:var(--serif);margin-bottom:16px;">分类与标签</h2>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card>
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;">
            <strong>分类</strong>
            <el-button size="small" type="primary" @click="addCat">+ 新增</el-button>
          </div>
          <el-table :data="categories" border>
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="description" label="描述" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }"><el-button size="small" @click="delCat(row.id)">删除</el-button></template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;">
            <strong>标签</strong>
            <el-input v-model="newTag" placeholder="新标签名" style="width:160px;" @keyup.enter="addTag" />
            <el-button size="small" type="primary" @click="addTag">+ 新增</el-button>
          </div>
          <div style="display:flex;flex-wrap:wrap;gap:8px;">
            <el-tag v-for="t in tags" :key="t.id" closable @close="delTag(t.id)">{{ t.name }}</el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  adminGetCategories, adminCreateCategory, adminDeleteCategory,
  adminGetTags, adminCreateTag, adminDeleteTag
} from '../../api/blog'

const categories = ref([])
const tags = ref([])
const newTag = ref('')

const load = async () => {
  categories.value = await adminGetCategories().catch(() => [])
  tags.value = await adminGetTags().catch(() => [])
}
const addCat = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请输入分类名称', '新增分类')
    await adminCreateCategory({ name: value })
    ElMessage.success('已添加'); load()
  } catch (e) {}
}
const delCat = async (id) => { try { await adminDeleteCategory(id); ElMessage.success('已删除'); load() } catch (e) {} }
const addTag = async () => {
  if (!newTag.value) return
  try { await adminCreateTag({ name: newTag.value }); ElMessage.success('已添加'); newTag.value = ''; load() } catch (e) {}
}
const delTag = async (id) => { try { await adminDeleteTag(id); ElMessage.success('已删除'); load() } catch (e) {} }

onMounted(load)
</script>
