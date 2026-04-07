<script setup>
import { onMounted, ref, computed } from 'vue'
import {
  NButton,
  NCard,
  NForm,
  NFormItem,
  NInput,
  NModal,
  NRadio,
  NRadioGroup,
  NSpace,
  NSwitch,
  NSelect,
  NTag,
  NIcon,
  NPopover,
  useMessage,
  useDialog,
} from 'naive-ui'
import {
  HomeOutline,
  PeopleOutline,
  SettingsOutline,
  KeyOutline,
  ListOutline,
  FolderOutline,
  DocumentOutline,
  PersonOutline,
  MenuOutline,
  AppsOutline,
  BusinessOutline,
  CalendarOutline,
  ChatbubbleOutline,
  CodeOutline,
  CompassOutline,
  ConstructOutline,
  EyeOutline,
  GiftOutline,
  HeartOutline,
  ImageOutline,
  InformationOutline,
  LocationOutline,
  MailOutline,
  MapOutline,
  MedalOutline,
  NewspaperOutline,
  NotificationsOutline,
  PaperPlaneOutline,
  RocketOutline,
  SearchOutline,
  StarOutline,
  TimeOutline,
  TrashOutline,
  TrophyOutline,
  VideocamOutline,
  WalletOutline,
  WarningOutline,
  CloudOutline,
  BookOutline,
  CameraOutline,
  CartOutline,
} from '@vicons/ionicons5'
import draggable from 'vuedraggable'
import * as adminApi from '@/api/admin'
import { useAuthStore } from '@/stores/auth'
import { useAdminMenuStore } from '@/stores/adminMenu'

const message = useMessage()
const dialog = useDialog()
const auth = useAuthStore()
const adminMenu = useAdminMenuStore()

const loading = ref(false)
const saving = ref(false)
const rows = ref([])
const roles = ref([])

const createOpen = ref(false)
const editOpen = ref(false)
const form = ref({
  parentId: null,
  title: '',
  path: '',
  icon: '',
  sortOrder: 0,
  permissionCode: null,
  menuType: 'menu',
  enabled: true,
  visibleRoleIds: [],
})
const editingId = ref(null)

const canWrite = () => auth.hasPermission('system:menu:write')

const iconList = [
  { name: 'HomeOutline', component: HomeOutline, label: '首页' },
  { name: 'PeopleOutline', component: PeopleOutline, label: '用户' },
  { name: 'SettingsOutline', component: SettingsOutline, label: '设置' },
  { name: 'KeyOutline', component: KeyOutline, label: '密钥' },
  { name: 'ListOutline', component: ListOutline, label: '列表' },
  { name: 'FolderOutline', component: FolderOutline, label: '文件夹' },
  { name: 'DocumentOutline', component: DocumentOutline, label: '文档' },
  { name: 'PersonOutline', component: PersonOutline, label: '个人' },
  { name: 'MenuOutline', component: MenuOutline, label: '菜单' },
  { name: 'AppsOutline', component: AppsOutline, label: '应用' },
  { name: 'BusinessOutline', component: BusinessOutline, label: '企业' },
  { name: 'CalendarOutline', component: CalendarOutline, label: '日历' },
  { name: 'ChatbubbleOutline', component: ChatbubbleOutline, label: '聊天' },
  { name: 'CodeOutline', component: CodeOutline, label: '代码' },
  { name: 'CompassOutline', component: CompassOutline, label: '指南针' },
  { name: 'ConstructOutline', component: ConstructOutline, label: '构建' },
  { name: 'EyeOutline', component: EyeOutline, label: '眼睛' },
  { name: 'GiftOutline', component: GiftOutline, label: '礼物' },
  { name: 'HeartOutline', component: HeartOutline, label: '心' },
  { name: 'ImageOutline', component: ImageOutline, label: '图片' },
  { name: 'InformationOutline', component: InformationOutline, label: '信息' },
  { name: 'LocationOutline', component: LocationOutline, label: '位置' },
  { name: 'MailOutline', component: MailOutline, label: '邮件' },
  { name: 'MapOutline', component: MapOutline, label: '地图' },
  { name: 'MedalOutline', component: MedalOutline, label: '奖章' },
  { name: 'NewspaperOutline', component: NewspaperOutline, label: '新闻' },
  { name: 'NotificationsOutline', component: NotificationsOutline, label: '通知' },
  { name: 'PaperPlaneOutline', component: PaperPlaneOutline, label: '发送' },
  { name: 'RocketOutline', component: RocketOutline, label: '火箭' },
  { name: 'SearchOutline', component: SearchOutline, label: '搜索' },
  { name: 'StarOutline', component: StarOutline, label: '星' },
  { name: 'TimeOutline', component: TimeOutline, label: '时间' },
  { name: 'TrashOutline', component: TrashOutline, label: '删除' },
  { name: 'TrophyOutline', component: TrophyOutline, label: '奖杯' },
  { name: 'VideocamOutline', component: VideocamOutline, label: '视频' },
  { name: 'WalletOutline', component: WalletOutline, label: '钱包' },
  { name: 'WarningOutline', component: WarningOutline, label: '警告' },
  { name: 'CloudOutline', component: CloudOutline, label: '云' },
  { name: 'BookOutline', component: BookOutline, label: '书籍' },
  { name: 'CameraOutline', component: CameraOutline, label: '相机' },
  { name: 'CartOutline', component: CartOutline, label: '购物车' },
]

function getIconComponent(name) {
  const found = iconList.find((i) => i.name === name)
  return found?.component || null
}

function cloneTree(nodes) {
  return (nodes || []).map((n) => ({
    ...n,
    children: n.children?.length ? cloneTree(n.children) : undefined,
  }))
}

function flattenTree(nodes, result = []) {
  for (const n of nodes || []) {
    result.push(n)
    if (n.children?.length) flattenTree(n.children, result)
  }
  return result
}

function collectDescendantIds(rootId, tree) {
  const ids = new Set()
  function findNode(nodes, id) {
    for (const n of nodes || []) {
      if (n.id === id) return n
      const f = findNode(n.children, id)
      if (f) return f
    }
    return null
  }
  function addSubtree(n) {
    ids.add(n.id)
    for (const c of n.children || []) addSubtree(c)
  }
  const node = findNode(tree, rootId)
  if (node) addSubtree(node)
  return ids
}

const parentOptions = ref([])

function rebuildParentOptions() {
  const blocked =
    editingId.value != null ? collectDescendantIds(editingId.value, rows.value) : new Set()
  const opts = [{ label: '（顶级）', value: null }]
  function walk(nodes, depth) {
    for (const n of nodes || []) {
      if (blocked.has(n.id)) continue
      opts.push({ label: `${'\u3000'.repeat(depth)}${n.title}`, value: n.id })
      if (n.children?.length) walk(n.children, depth + 1)
    }
  }
  walk(rows.value, 0)
  parentOptions.value = opts
}

const roleOptions = computed(() => {
  return roles.value.map((r) => ({ label: r.name, value: r.id }))
})

async function load() {
  loading.value = true
  try {
    const [menus, rolesData] = await Promise.all([
      adminApi.fetchAdminMenus(),
      adminApi.fetchRoles(),
    ])
    rows.value = menus
    roles.value = rolesData
    rebuildParentOptions()
  } catch (e) {
    message.error(e?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(load)

function openCreate() {
  editingId.value = null
  form.value = {
    parentId: null,
    title: '',
    path: '',
    icon: '',
    sortOrder: 0,
    permissionCode: null,
    menuType: 'menu',
    enabled: true,
    visibleRoleIds: [],
  }
  rebuildParentOptions()
  createOpen.value = true
}

function openEdit(row) {
  editingId.value = row.id
  form.value = {
    parentId: row.parentId ?? null,
    title: row.title,
    path: row.path ?? '',
    icon: row.icon ?? '',
    sortOrder: row.sortOrder ?? 0,
    permissionCode: row.permissionCode ?? null,
    menuType: row.menuType || 'menu',
    enabled: !!row.enabled,
    visibleRoleIds: row.visibleRoleIds ?? [],
  }
  rebuildParentOptions()
  editOpen.value = true
}

function payloadFromForm() {
  return {
    parentId: form.value.parentId,
    title: form.value.title,
    path: form.value.path,
    icon: form.value.icon || null,
    sortOrder: form.value.sortOrder,
    permissionCode: form.value.permissionCode,
    menuType: form.value.menuType,
    enabled: form.value.enabled,
    visibleRoleIds: form.value.visibleRoleIds,
  }
}

async function saveCreate() {
  saving.value = true
  try {
    await adminApi.createMenu(payloadFromForm())
    message.success('已创建')
    createOpen.value = false
    await load()
    await adminMenu.load()
  } catch (e) {
    message.error(e?.message || '创建失败')
  } finally {
    saving.value = false
  }
}

async function saveEdit() {
  saving.value = true
  try {
    await adminApi.updateMenu(editingId.value, payloadFromForm())
    message.success('已保存')
    editOpen.value = false
    await load()
    await adminMenu.load()
  } catch (e) {
    message.error(e?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  try {
    await adminApi.deleteMenu(row.id)
    message.success('已删除')
    await load()
    await adminMenu.load()
  } catch (e) {
    message.error(e?.message || '删除失败')
  }
}

const dragOptions = {
  group: 'menu',
  animation: 150,
  handle: '.drag-handle',
  ghostClass: 'ghost',
}

function onDragEnd() {
  saveSort()
}

async function saveSort() {
  const items = []
  function collect(nodes, parentId = null) {
    for (let i = 0; i < nodes.length; i++) {
      const n = nodes[i]
      items.push({ id: n.id, parentId, sortOrder: i })
      if (n.children?.length) collect(n.children, n.id)
    }
  }
  collect(rows.value)
  try {
    await adminApi.updateMenuSort({ items })
    await adminMenu.load()
  } catch (e) {
    message.error(e?.message || '排序保存失败')
  }
}

function selectIcon(iconName) {
  form.value.icon = iconName
}

function getIconLabel(name) {
  const found = iconList.find((i) => i.name === name)
  return found?.label || '无图标'
}
</script>

<template>
  <div>
    <n-space vertical size="large">
      <n-space justify="space-between" align="center">
        <span class="tip" v-if="canWrite()">拖拽菜单项可调整顺序</span>
        <n-button v-if="canWrite()" type="primary" @click="openCreate">新建菜单</n-button>
      </n-space>

      <div class="menu-tree" v-if="!loading">
        <draggable
          v-model="rows"
          v-bind="dragOptions"
          item-key="id"
          @end="onDragEnd"
        >
          <template #item="{ element }">
            <div class="menu-item">
              <div class="menu-row">
                <span class="drag-handle" v-if="canWrite()">☰</span>
                <span class="menu-title">
                  <n-icon v-if="element.icon" size="18" class="icon-preview">
                    <component :is="getIconComponent(element.icon)" />
                  </n-icon>
                  {{ element.title }}
                </span>
                <span class="menu-path">{{ element.path || '-' }}</span>
                <span class="menu-type">{{ element.menuType === 'catalog' ? '目录' : '菜单' }}</span>
                <span class="menu-enabled">{{ element.enabled ? '启用' : '禁用' }}</span>
                <span class="menu-roles">
                  <n-tag v-if="!element.visibleRoleIds?.length" size="small" type="info">全部可见</n-tag>
                  <n-space v-else size="small">
                    <n-tag v-for="id in element.visibleRoleIds" :key="id" size="small">
                      {{ roles.find(r => r.id === id)?.name }}
                    </n-tag>
                  </n-space>
                </span>
                <span class="menu-actions">
                  <n-space size="small">
                    <n-button size="tiny" tertiary @click="openEdit(element)">编辑</n-button>
                    <n-button
                      size="tiny"
                      tertiary
                      type="error"
                      @click="() => {
                        dialog.warning({
                          title: '确认删除',
                          content: '确定删除？子菜单会一并删除。',
                          positiveText: '删除',
                          negativeText: '取消',
                          onPositiveClick: async () => {
                            await remove(element)
                          }
                        })
                      }"
                    >删除</n-button>
                  </n-space>
                </span>
              </div>
              <draggable
                v-if="element.children?.length"
                v-model="element.children"
                v-bind="dragOptions"
                item-key="id"
                @end="onDragEnd"
                class="menu-children"
              >
                <template #item="{ element: child }">
                  <div class="menu-item">
                    <div class="menu-row">
                      <span class="drag-handle" v-if="canWrite()">☰</span>
                      <span class="menu-title">
                        <n-icon v-if="child.icon" size="18" class="icon-preview">
                          <component :is="getIconComponent(child.icon)" />
                        </n-icon>
                        {{ child.title }}
                      </span>
                      <span class="menu-path">{{ child.path || '-' }}</span>
                      <span class="menu-type">{{ child.menuType === 'catalog' ? '目录' : '菜单' }}</span>
                      <span class="menu-enabled">{{ child.enabled ? '启用' : '禁用' }}</span>
                      <span class="menu-roles">
                        <n-tag v-if="!child.visibleRoleIds?.length" size="small" type="info">全部可见</n-tag>
                        <n-space v-else size="small">
                          <n-tag v-for="id in child.visibleRoleIds" :key="id" size="small">
                            {{ roles.find(r => r.id === id)?.name }}
                          </n-tag>
                        </n-space>
                      </span>
                      <span class="menu-actions">
                        <n-space size="small">
                          <n-button size="tiny" tertiary @click="openEdit(child)">编辑</n-button>
                          <n-button
                            size="tiny"
                            tertiary
                            type="error"
                            @click="() => {
                              dialog.warning({
                                title: '确认删除',
                                content: '确定删除？',
                                positiveText: '删除',
                                negativeText: '取消',
                                onPositiveClick: async () => {
                                  await remove(child)
                                }
                              })
                            }"
                          >删除</n-button>
                        </n-space>
                      </span>
                    </div>
                  </div>
                </template>
              </draggable>
            </div>
          </template>
        </draggable>
      </div>
      <div v-else class="loading">加载中...</div>
    </n-space>

    <n-modal v-model:show="createOpen">
      <n-card title="新建菜单" style="width: 600px" :bordered="false" size="huge">
        <n-form label-placement="left" label-width="100">
          <n-form-item label="父级">
            <n-select
              v-model:value="form.parentId"
              :options="parentOptions"
              clearable
              filterable
            />
          </n-form-item>
          <n-form-item label="标题">
            <n-input v-model:value="form.title" />
          </n-form-item>
          <n-form-item label="路由路径">
            <n-input v-model:value="form.path" placeholder="如 /admin/users；目录可留空" />
          </n-form-item>
          <n-form-item label="图标">
            <n-popover
              trigger="click"
              placement="bottom-start"
              :show-arrow="false"
              content-style="padding: 8px;"
            >
              <template #trigger>
                <n-button>
                  <template #icon>
                    <n-icon v-if="form.icon">
                      <component :is="getIconComponent(form.icon)" />
                    </n-icon>
                  </template>
                  {{ form.icon ? getIconLabel(form.icon) : '选择图标' }}
                </n-button>
              </template>
              <div class="icon-picker">
                <div
                  class="icon-item"
                  :class="{ active: form.icon === '' }"
                  @click="selectIcon('')"
                >
                  <div class="icon-preview-empty">无</div>
                  <span class="icon-label">无图标</span>
                </div>
                <div
                  v-for="icon in iconList"
                  :key="icon.name"
                  class="icon-item"
                  :class="{ active: form.icon === icon.name }"
                  @click="selectIcon(icon.name)"
                >
                  <n-icon size="24" class="icon-preview">
                    <component :is="icon.component" />
                  </n-icon>
                  <span class="icon-label">{{ icon.label }}</span>
                </div>
              </div>
            </n-popover>
          </n-form-item>
          <n-form-item label="可见角色">
            <n-select
              v-model:value="form.visibleRoleIds"
              :options="roleOptions"
              multiple
              clearable
              placeholder="不选则所有角色可见"
            />
          </n-form-item>
          <n-form-item label="类型">
            <n-radio-group v-model:value="form.menuType">
              <n-radio value="menu">菜单项</n-radio>
              <n-radio value="catalog">目录（仅分组）</n-radio>
            </n-radio-group>
          </n-form-item>
          <n-form-item label="启用">
            <n-switch v-model:value="form.enabled" />
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
      <n-card title="编辑菜单" style="width: 600px" :bordered="false" size="huge">
        <n-form label-placement="left" label-width="100">
          <n-form-item label="父级">
            <n-select
              v-model:value="form.parentId"
              :options="parentOptions"
              clearable
              filterable
            />
          </n-form-item>
          <n-form-item label="标题">
            <n-input v-model:value="form.title" />
          </n-form-item>
          <n-form-item label="路由路径">
            <n-input v-model:value="form.path" placeholder="如 /admin/users；目录可留空" />
          </n-form-item>
          <n-form-item label="图标">
            <n-popover
              trigger="click"
              placement="bottom-start"
              :show-arrow="false"
              content-style="padding: 8px;"
            >
              <template #trigger>
                <n-button>
                  <template #icon>
                    <n-icon v-if="form.icon">
                      <component :is="getIconComponent(form.icon)" />
                    </n-icon>
                  </template>
                  {{ form.icon ? getIconLabel(form.icon) : '选择图标' }}
                </n-button>
              </template>
              <div class="icon-picker">
                <div
                  class="icon-item"
                  :class="{ active: form.icon === '' }"
                  @click="selectIcon('')"
                >
                  <div class="icon-preview-empty">无</div>
                  <span class="icon-label">无图标</span>
                </div>
                <div
                  v-for="icon in iconList"
                  :key="icon.name"
                  class="icon-item"
                  :class="{ active: form.icon === icon.name }"
                  @click="selectIcon(icon.name)"
                >
                  <n-icon size="24" class="icon-preview">
                    <component :is="icon.component" />
                  </n-icon>
                  <span class="icon-label">{{ icon.label }}</span>
                </div>
              </div>
            </n-popover>
          </n-form-item>
          <n-form-item label="可见角色">
            <n-select
              v-model:value="form.visibleRoleIds"
              :options="roleOptions"
              multiple
              clearable
              placeholder="不选则所有角色可见"
            />
          </n-form-item>
          <n-form-item label="类型">
            <n-radio-group v-model:value="form.menuType">
              <n-radio value="menu">菜单项</n-radio>
              <n-radio value="catalog">目录（仅分组）</n-radio>
            </n-radio-group>
          </n-form-item>
          <n-form-item label="启用">
            <n-switch v-model:value="form.enabled" />
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
  </div>
</template>

<style scoped>
.tip {
  color: #999;
  font-size: 13px;
}

.menu-tree {
  border: 1px solid #e0e0e6;
  border-radius: 6px;
  overflow: hidden;
}

.menu-item {
  background: #fff;
}

.menu-row {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e0e0e6;
  gap: 16px;
}

.menu-children .menu-row {
  padding-left: 48px;
  background: #fafafa;
}

.drag-handle {
  cursor: grab;
  color: #999;
  font-size: 16px;
  user-select: none;
}

.drag-handle:active {
  cursor: grabbing;
}

.menu-title {
  flex: 1;
  min-width: 120px;
  font-weight: 500;
  display: flex;
  align-items: center;
}

.icon-preview {
  margin-right: 6px;
}

.menu-path {
  width: 160px;
  color: #666;
  font-size: 13px;
}

.menu-type {
  width: 60px;
  font-size: 13px;
}

.menu-enabled {
  width: 50px;
  font-size: 13px;
}

.menu-roles {
  width: 180px;
}

.menu-actions {
  width: 120px;
  text-align: right;
}

.ghost {
  opacity: 0.5;
  background: #e8f4ff;
}

.loading {
  padding: 40px;
  text-align: center;
  color: #999;
}

.icon-picker {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
  max-width: 420px;
  max-height: 320px;
  overflow-y: auto;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 8px 4px;
  border: 1px solid #e0e0e6;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: #fff;
}

.icon-item:hover {
  border-color: #18a058;
  background: #f0fdf4;
}

.icon-item.active {
  border-color: #18a058;
  background: #e8f5e9;
}

.icon-preview {
  margin-bottom: 4px;
  color: #333;
}

.icon-preview-empty {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
  margin-bottom: 4px;
}

.icon-label {
  font-size: 11px;
  color: #666;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 60px;
}
</style>
