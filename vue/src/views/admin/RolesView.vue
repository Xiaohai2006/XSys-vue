<script setup>
import { h, onMounted, ref } from 'vue'
import {
  NButton,
  NCard,
  NDataTable,
  NForm,
  NFormItem,
  NInput,
  NModal,
  NSpace,
  NTransfer,
  useMessage,
} from 'naive-ui'
import * as adminApi from '@/api/admin'
import { useAuthStore } from '@/stores/auth'

const message = useMessage()
const auth = useAuthStore()

const loading = ref(false)
const saving = ref(false)
const rows = ref([])
const permissions = ref([])

const permOptions = ref([])

const createOpen = ref(false)
const editOpen = ref(false)
const permOpen = ref(false)

const form = ref({ code: '', name: '', description: '' })
const editingId = ref(null)

const selectedPermIds = ref([])
const permRoleId = ref(null)

const canWrite = () => auth.hasPermission('system:role:write')

async function load() {
  loading.value = true
  try {
    const [r, p] = await Promise.all([adminApi.fetchRoles(), adminApi.fetchPermissions()])
    rows.value = r
    permissions.value = p
    permOptions.value = p.map((x) => ({
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

function openCreate() {
  editingId.value = null
  form.value = { code: '', name: '', description: '' }
  createOpen.value = true
}

function openEdit(row) {
  editingId.value = row.id
  form.value = { code: row.code, name: row.name, description: row.description || '' }
  editOpen.value = true
}

function openPerm(row) {
  permRoleId.value = row.id
  selectedPermIds.value = [...(row.permissionIds || [])]
  permOpen.value = true
}

async function saveCreate() {
  saving.value = true
  try {
    await adminApi.createRole(form.value)
    message.success('已创建')
    createOpen.value = false
    await load()
  } catch (e) {
    message.error(e?.message || '创建失败')
  } finally {
    saving.value = false
  }
}

async function saveEdit() {
  saving.value = true
  try {
    await adminApi.updateRole(editingId.value, form.value)
    message.success('已保存')
    editOpen.value = false
    await load()
  } catch (e) {
    message.error(e?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function savePerm() {
  saving.value = true
  try {
    await adminApi.assignRolePermissions(permRoleId.value, selectedPermIds.value)
    message.success('权限已更新')
    permOpen.value = false
    await load()
    await auth.fetchMe()
  } catch (e) {
    message.error(e?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  try {
    await adminApi.deleteRole(row.id)
    message.success('已删除')
    await load()
    await auth.fetchMe()
  } catch (e) {
    message.error(e?.message || '删除失败')
  }
}

const columns = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '编码', key: 'code' },
  { title: '名称', key: 'name' },
  { title: '说明', key: 'description' },
  {
    title: '操作',
    key: 'actions',
    width: 260,
    render(row) {
      if (!canWrite()) return '—'
      return h(
        NSpace,
        { size: 8 },
        {
          default: () => [
            h(
              NButton,
              { size: 'small', tertiary: true, type: 'primary', onClick: () => openPerm(row) },
              { default: () => '权限' },
            ),
            h(
              NButton,
              { size: 'small', tertiary: true, onClick: () => openEdit(row) },
              { default: () => '编辑' },
            ),
            h(
              NButton,
              {
                size: 'small',
                tertiary: true,
                type: 'error',
                onClick: async () => {
                  if (!window.confirm('确定删除该角色？')) return
                  await remove(row)
                },
              },
              { default: () => '删除' },
            ),
          ],
        },
      )
    },
  },
]
</script>

<template>
  <div>
    <n-space vertical size="large">
      <n-space justify="space-between" align="center">
        <span />
        <n-button v-if="canWrite()" type="primary" @click="openCreate">新建角色</n-button>
      </n-space>
      <n-data-table :loading="loading" :columns="columns" :data="rows" :bordered="false" />
    </n-space>

    <n-modal v-model:show="createOpen">
      <n-card title="新建角色" style="width: 560px" :bordered="false" size="huge">
        <n-form label-placement="left" label-width="88">
          <n-form-item label="编码">
            <n-input v-model:value="form.code" placeholder="例如 STAFF" />
          </n-form-item>
          <n-form-item label="名称">
            <n-input v-model:value="form.name" />
          </n-form-item>
          <n-form-item label="说明">
            <n-input v-model:value="form.description" type="textarea" :autosize="{ minRows: 2 }" />
          </n-form-item>
        </n-form>
        <template #footer>
          <n-space justify="end">
            <n-button @click="createOpen = false">取消</n-button>
            <n-button type="primary" :loading="saving" @click="saveCreate">创建</n-button>
          </n-space>
        </template>
      </n-card>
    </n-modal>

    <n-modal v-model:show="editOpen">
      <n-card title="编辑角色" style="width: 560px" :bordered="false" size="huge">
        <n-form label-placement="left" label-width="88">
          <n-form-item label="编码">
            <n-input v-model:value="form.code" />
          </n-form-item>
          <n-form-item label="名称">
            <n-input v-model:value="form.name" />
          </n-form-item>
          <n-form-item label="说明">
            <n-input v-model:value="form.description" type="textarea" :autosize="{ minRows: 2 }" />
          </n-form-item>
        </n-form>
        <template #footer>
          <n-space justify="end">
            <n-button @click="editOpen = false">取消</n-button>
            <n-button type="primary" :loading="saving" @click="saveEdit">保存</n-button>
          </n-space>
        </template>
      </n-card>
    </n-modal>

    <n-modal v-model:show="permOpen">
      <n-card title="配置权限" style="width: 720px" :bordered="false" size="huge">
        <n-transfer
          v-model:value="selectedPermIds"
          virtual-scroll
          :options="permOptions"
          source-filterable
          target-filterable
        />
        <template #footer>
          <n-space justify="end">
            <n-button @click="permOpen = false">取消</n-button>
            <n-button type="primary" :loading="saving" @click="savePerm">保存</n-button>
          </n-space>
        </template>
      </n-card>
    </n-modal>
  </div>
</template>
