<script setup>
import { onMounted } from 'vue'
import { NButton, NSpace, NCard, NH2, NText } from 'naive-ui'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

onMounted(async () => {
  if (auth.token && !auth.user) {
    try {
      await auth.fetchMe()
    } catch {
      auth.logout()
    }
  }
})

function goAdmin() {
  router.push('/admin')
}
</script>

<template>
  <div style="max-width: 720px; margin: 48px auto; padding: 0 16px">
    <n-card title="XSys">
      <n-h2 style="margin-top: 0">后台管理模板</n-h2>
      <n-text depth="3">登录 / 注册、JWT、角色与权限、默认注册角色（Spring Boot + MyBatis + Vue + Naive UI）。</n-text>
      <div style="margin-top: 24px">
        <n-space>
          <n-button v-if="!auth.isLoggedIn" type="primary" @click="router.push('/login')">
            登录
          </n-button>
          <n-button v-if="!auth.isLoggedIn" secondary @click="router.push('/register')">
            注册
          </n-button>
          <n-button v-if="auth.isLoggedIn && auth.canAccessAdmin()" type="primary" @click="goAdmin">
            进入后台
          </n-button>
          <n-button v-if="auth.isLoggedIn" quaternary @click="auth.logout(); router.push('/')">
            退出
          </n-button>
        </n-space>
      </div>
    </n-card>
  </div>
</template>
