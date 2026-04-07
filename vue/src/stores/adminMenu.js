import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as authApi from '@/api/auth'
import { getToken } from '@/api/token'

export const useAdminMenuStore = defineStore('adminMenu', () => {
  const tree = ref([])

  async function load() {
    if (!getToken()) {
      tree.value = []
      return
    }
    try {
      tree.value = await authApi.fetchMenus()
    } catch {
      tree.value = []
    }
  }

  return { tree, load }
})
