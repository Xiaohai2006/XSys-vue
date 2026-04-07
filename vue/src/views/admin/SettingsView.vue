<script setup>
import { onMounted, ref } from 'vue'
import { NButton, NCard, NSelect, NSpace, NText, useMessage } from 'naive-ui'
import * as adminApi from '@/api/admin'
import { useAuthStore } from '@/stores/auth'

const message = useMessage()
const auth = useAuthStore()

const loading = ref(false)
const saving = ref(false)

const roles = ref([])
const roleOptions = ref([])
const selectedRoleId = ref(null)

const current = ref(null)

const canWrite = () => auth.hasPermission('system:settings:write')

async function load() {
  loading.value = true
  try {
    const [r, s] = await Promise.all([adminApi.fetchRoles(), adminApi.getDefaultRegisterRole()])
    roles.value = r
    roleOptions.value = r.map((x) => ({ label: `${x.code} · ${x.name}`, value: x.id }))
    current.value = s
    selectedRoleId.value = s?.roleId ?? null
  } catch (e) {
    message.error(e?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(load)

async function save() {
  if (!selectedRoleId.value) {
    message.warning('请选择角色')
    return
  }
  saving.value = true
  try {
    const s = await adminApi.setDefaultRegisterRole(selectedRoleId.value)
    current.value = s
    message.success('已保存')
  } catch (e) {
    message.error(e?.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div>
    <n-card title="系统设置" :loading="loading">
      <n-space vertical size="large" style="width: 100%; max-width: 560px">
        <div>
          <n-text depth="3">新用户注册时默认分配的角色（可在「角色与权限」中维护可选角色）。</n-text>
        </div>
        <div>
          <div style="margin-bottom: 8px">当前默认角色</div>
          <n-text v-if="current?.roleId">
            {{ current.roleCode }} · {{ current.roleName }}（ID {{ current.roleId }}）
          </n-text>
          <n-text v-else depth="3">未配置（将回退到 USER 角色）</n-text>
        </div>
        <div>
          <div style="margin-bottom: 8px">修改为</div>
          <n-select
            v-model:value="selectedRoleId"
            :options="roleOptions"
            placeholder="选择角色"
            filterable
            clearable
            style="width: 100%"
            :disabled="!canWrite()"
          />
        </div>
        <n-space>
          <n-button type="primary" :loading="saving" :disabled="!canWrite()" @click="save">
            保存
          </n-button>
        </n-space>
      </n-space>
    </n-card>
  </div>
</template>
