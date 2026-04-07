import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import * as authApi from '@/api/auth'
import { clearToken, getToken, setToken } from '@/api/token'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(getToken())
  const user = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  function hasPermission(code) {
    return !!user.value?.permissions?.includes(code)
  }

  function canAccessAdmin() {
    return !!user.value?.permissions?.some((p) => p.startsWith('system:'))
  }

  async function login(payload) {
    const res = await authApi.login(payload)
    setToken(res.token)
    token.value = res.token
    user.value = res.user
    return res
  }

  async function register(payload) {
    const res = await authApi.register(payload)
    setToken(res.token)
    token.value = res.token
    user.value = res.user
    return res
  }

  function logout() {
    clearToken()
    token.value = null
    user.value = null
  }

  async function fetchMe() {
    if (!token.value) {
      user.value = null
      return null
    }
    const u = await authApi.me()
    user.value = u
    return u
  }

  return {
    token,
    user,
    isLoggedIn,
    hasPermission,
    canAccessAdmin,
    login,
    register,
    logout,
    fetchMe,
  }
})
