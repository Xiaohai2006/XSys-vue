import axios from 'axios'
import { clearToken, getToken } from './token'

const http = axios.create({
  baseURL: '/api',
  timeout: 60000,
})

http.interceptors.request.use((config) => {
  const t = getToken()
  if (t) {
    config.headers.Authorization = `Bearer ${t}`
  }
  return config
})

http.interceptors.response.use(
  (res) => {
    const body = res.data
    if (body && typeof body.code === 'number' && body.code !== 0) {
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    return body?.data
  },
  (err) => {
    const data = err.response?.data
    if (err.response?.status === 401) {
      clearToken()
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login'
      }
    }
    const msg =
      (data && typeof data === 'object' && data.message) ||
      err.message ||
      (typeof data === 'string' ? data : '网络错误')
    return Promise.reject(new Error(msg))
  },
)

export default http
