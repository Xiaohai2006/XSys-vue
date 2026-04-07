import http from './http'

export function login(data) {
  return http.post('/auth/login', data)
}

export function register(data) {
  return http.post('/auth/register', data)
}

export function me() {
  return http.get('/auth/me')
}

export function fetchMenus() {
  return http.get('/auth/menus')
}
