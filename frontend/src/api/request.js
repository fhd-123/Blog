import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 15000
})

service.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    }
    ElMessage.error(res.message || '请求失败')
    if (res.code === 401) {
      localStorage.removeItem('token')
      if (location.hash.indexOf('/admin') !== -1) {
        location.hash = '#/admin/login'
      }
    }
    return Promise.reject(new Error(res.message || 'Error'))
  },
  (error) => {
    const msg = error.response && error.response.data ? error.response.data.message : error.message
    ElMessage.error(msg || '网络错误')
    return Promise.reject(error)
  }
)

export default service
