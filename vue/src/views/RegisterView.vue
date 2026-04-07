<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  NButton,
  NCard,
  NForm,
  NFormItem,
  NInput,
  NSpace,
  useMessage,
} from 'naive-ui'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const message = useMessage()
const auth = useAuthStore()

const formRef = ref(null)
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  nickname: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function onSubmit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    await auth.register({
      username: form.username,
      password: form.password,
      nickname: form.nickname || undefined,
    })
    message.success('注册成功')
    await router.replace('/admin')
  } catch (e) {
    message.error(e?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="display: flex; justify-content: center; padding-top: 72px">
    <n-card title="注册" style="width: 100%; max-width: 400px">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="72">
        <n-form-item path="username" label="用户名">
          <n-input v-model:value="form.username" autocomplete="username" />
        </n-form-item>
        <n-form-item path="password" label="密码">
          <n-input
            v-model:value="form.password"
            type="password"
            show-password-on="click"
            autocomplete="new-password"
          />
        </n-form-item>
        <n-form-item path="nickname" label="昵称">
          <n-input v-model:value="form.nickname" placeholder="可选" />
        </n-form-item>
        <n-space justify="end" style="width: 100%">
          <n-button quaternary @click="router.push('/login')">去登录</n-button>
          <n-button type="primary" :loading="loading" @click="onSubmit">注册</n-button>
        </n-space>
      </n-form>
    </n-card>
  </div>
</template>
