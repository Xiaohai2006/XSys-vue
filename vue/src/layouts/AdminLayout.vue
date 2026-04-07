<script setup>
import { computed, onMounted, watch, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NIcon, NLayout, NLayoutContent, NLayoutHeader, NLayoutSider, NMenu, NSpace } from 'naive-ui'
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
import { useAuthStore } from '@/stores/auth'
import { useAdminMenuStore } from '@/stores/adminMenu'

const iconMap = {
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
}

function getIconComponent(name) {
  if (!name) return null
  return iconMap[name] || null
}

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const adminMenu = useAdminMenuStore()

onMounted(() => adminMenu.load())
watch(
  () => auth.token,
  () => adminMenu.load(),
)

const activeKey = computed(() => {
  const p = route.path
  if (p === '/admin' || p === '/admin/') return '/admin'
  return p
})

function toMenuOptions(nodes) {
  if (!nodes?.length) return []
  return nodes.map((n) => {
    const hasChildren = n.children?.length > 0
    const key =
      n.menuType === 'catalog' || (hasChildren && !(n.path && String(n.path).length))
        ? `__g_${n.id}`
        : n.path || `__g_${n.id}`
    const opt = { label: n.title, key }
    const iconComp = getIconComponent(n.icon)
    if (iconComp) {
      opt.icon = () => h(NIcon, null, { default: () => h(iconComp) })
    }
    if (hasChildren) {
      opt.children = toMenuOptions(n.children)
    }
    return opt
  })
}

const menuOptions = computed(() => {
  const built = toMenuOptions(adminMenu.tree)
  if (built.length) return built
  if (auth.canAccessAdmin()) {
    return [{ label: '仪表盘', key: '/admin' }]
  }
  return []
})

function onSelect(key) {
  if (String(key).startsWith('__g_')) return
  router.push(key)
}

function onLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <n-layout has-sider style="height: 100vh">
    <n-layout-sider bordered show-trigger collapse-mode="width" :width="220" :collapsed-width="64">
      <div style="padding: 16px; font-weight: 600">XSys</div>
      <n-menu :value="activeKey" :options="menuOptions" @update:value="onSelect" />
    </n-layout-sider>
    <n-layout>
      <n-layout-header
        bordered
        style="
          height: 56px;
          padding: 0 16px;
          display: flex;
          align-items: center;
          justify-content: space-between;
        "
      >
        <span>后台管理</span>
        <n-space align="center">
          <span>{{ auth.user?.username }}</span>
          <n-button size="small" @click="onLogout">退出</n-button>
        </n-space>
      </n-layout-header>
      <n-layout-content content-style="padding: 16px; min-height: calc(100vh - 56px)">
        <router-view />
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>
