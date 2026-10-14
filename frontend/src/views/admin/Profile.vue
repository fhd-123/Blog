<template>
  <div>
    <h2 style="margin-bottom:16px;">站点资料</h2>
    <el-table :data="rows" border style="width:100%">
      <el-table-column prop="key" label="字段" width="180" />
      <el-table-column label="值">
        <template #default="{ row }">
          <el-input v-model="row.value" type="textarea" :rows="2" />
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-top:16px;">
      <el-button type="primary" :loading="saving" @click="save">保存全部</el-button>
      <span style="color:var(--text-soft);font-size:.85rem;margin-left:12px;">about_skills / about_projects / about_education 为 JSON 数组，请保持格式</span>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminGetProfile, adminUpdateProfile } from '../../api/blog'

const rows = ref([])
const saving = ref(false)

onMounted(async () => {
  const data = await adminGetProfile().catch(() => ({}))
  rows.value = Object.keys(data).map(k => ({ key: k, value: data[k] }))
})

const save = async () => {
  saving.value = true
  try {
    await adminUpdateProfile(rows.value)
    ElMessage.success('已保存')
  } catch (e) {} finally { saving.value = false }
}
</script>
