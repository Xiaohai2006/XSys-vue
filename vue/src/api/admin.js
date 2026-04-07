import http from './http'

export function fetchRoles() {
  return http.get('/admin/roles')
}

export function fetchPermissions() {
  return http.get('/admin/permissions')
}

export function createPermission(data) {
  return http.post('/admin/permissions', data)
}

export function updatePermission(id, data) {
  return http.put(`/admin/permissions/${id}`, data)
}

export function deletePermission(id) {
  return http.delete(`/admin/permissions/${id}`)
}

export function createRole(data) {
  return http.post('/admin/roles', data)
}

export function updateRole(id, data) {
  return http.put(`/admin/roles/${id}`, data)
}

export function deleteRole(id) {
  return http.delete(`/admin/roles/${id}`)
}

export function assignRolePermissions(id, permissionIds) {
  return http.put(`/admin/roles/${id}/permissions`, { permissionIds })
}

export function fetchUsers() {
  return http.get('/admin/users')
}

export function assignUserRoles(id, roleIds) {
  return http.put(`/admin/users/${id}/roles`, { roleIds })
}

export function getDefaultRegisterRole() {
  return http.get('/admin/settings/default-register-role')
}

export function setDefaultRegisterRole(roleId) {
  return http.put('/admin/settings/default-register-role', { roleId })
}

export function fetchAdminMenus() {
  return http.get('/admin/menus')
}

export function createMenu(data) {
  return http.post('/admin/menus', data)
}

export function updateMenu(id, data) {
  return http.put(`/admin/menus/${id}`, data)
}

export function deleteMenu(id) {
  return http.delete(`/admin/menus/${id}`)
}

export function updateMenuSort(data) {
  return http.put('/admin/menus/sort', data)
}

export function fetchDailyNewUserStats(days = 7) {
  return http.get('/admin/users/stats/daily-new', { params: { days } })
}
