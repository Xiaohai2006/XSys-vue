<script setup>
import { h, onMounted, ref } from 'vue'
import { NButton, NCard, NDataTable, NModal, NSelect, NSpace, NTag, useMessage } from 'naive-ui'
import * as adminApi from '@/api/admin'
import { useAuthStore } from '@/stores/auth'

const message = useMessage()
const auth = useAuthStore()

const loading = ref(false)
const saving = ref(false)
const rows = ref([])
const roles = ref([])

const assignOpen = ref(false)
const assignUserId = ref(null)
const assignRoleIds = ref([])

const roleOptions = ref([])

const canWrite = () => auth.hasPermission('system:user:write')

async function load() {
  loading.value = true
  try {
    const [u, r] = await Promise.all([adminApi.fetchUsers(), adminApi.fetchRoles()])
    rows.value = u
    roles.value = r
    roleOptions.value = r.map((x) => ({
      label: `${x.code} · ${x.name}`,
      value: x.id,
    }))
  } catch (e) {
    message.error(e?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(load)

function openAssign(row) {
  assignUserId.value = row.id
  assignRoleIds.value = []
  const map = new Map(roles.value.map((x) => [x.code, x.id]))
  for (const code of row.roleCodes || []) {
    const id = map.get(code)
    if (id) assignRoleIds.value.push(id)
  }
  assignOpen.value = true
}

async function saveAssign() {
  saving.value = true
  try {
    await adminApi.assignUserRoles(assignUserId.value, assignRoleIds.value)
    message.success('已保存')
    assignOpen.value = false
    await load()
    if (auth.user?.id === assignUserId.value) {
      await auth.fetchMe()
    }
  } catch (e) {
    message.error(e?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const columns = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '用户名', key: 'username' },
  { title: '昵称', key: 'nickname' },
  {
    title: '状态',
    key: 'enabled',
    width: 90,
    render(row) {
      return h(NTag, { type: row.enabled ? 'success' : 'default', size: 'small' }, () =>
        row.enabled ? '启用' : '禁用',
      )
    },
  },
  {
    title: '角色',
    key: 'roleCodes',
    render(row) {
      const codes = row.roleCodes || []
      return codes.length ? codes.join(', ') : '—'
    },
  },
  {
    title: '创建时间',
    key: 'createdAt',
    width: 200,
    render(row) {
      const v = row.createdAt
      if (!v) return '—'
      if (typeof v === 'string') return v
      return new Date(v).toLocaleString()
    },
  },
  {
    title: '操作',
    key: 'actions',
    width: 120,
    render(row) {
      if (!canWrite()) return '—'
      return h(
        NButton,
        { size: 'small', type: 'primary', tertiary: true, onClick: () => openAssign(row) },
        () => '分配角色',
      )
    },
  },
]
</script>

<template>
  <div>
    <n-data-table :loading="loading" :columns="columns" :data="rows" :bordered="false" />
    <n-modal v-model:show="assignOpen">
      <n-card
        title="分配角色"
        style="width: 560px"
        :bordered="false"
        size="huge"
        role="dialog"
        aria-modal="true"
      >
        <n-select v-model:value="assignRoleIds" multiple filterable :options="roleOptions" />
        <template #footer>
          <n-space justify="end">
            <n-button @click="assignOpen = false">取消</n-button>
            <n-button type="primary" :loading="saving" @click="saveAssign">保存</n-button>
          </n-space>
        </template>
      </n-card>
    </n-modal>
  </div>
</template>
