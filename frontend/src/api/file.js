import request from '@/utils/request'

export const fileApi = {
  // 上传文件
  uploadFile(file) {
    const formData = new FormData()
    formData.append('file', file)
    
    return request({
      url: '/files/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 上传头像
  uploadAvatar(file) {
    const formData = new FormData()
    formData.append('file', file)
    
    return request({
      url: '/files/upload/avatar',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取用户文件列表
  getUserFiles(params = {}) {
    return request({
      url: '/files/list',
      method: 'get',
      params: {
        page: params.page || 0,
        size: params.size || 10,
        sort: params.sortBy || 'createdAt',
        direction: params.sortDir || 'desc'
      }
    })
  },

  // 获取用户图片文件
  getUserImageFiles() {
    return request({
      url: '/files/images',
      method: 'get'
    })
  },

  // 搜索文件
  searchFiles(keyword, params = {}) {
    return request({
      url: '/files/search',
      method: 'get',
      params: {
        keyword,
        page: params.page || 0,
        size: params.size || 10,
        sortBy: params.sortBy || 'createdAt',
        sortDir: params.sortDir || 'desc'
      }
    })
  },

  // 删除文件
  deleteFile(id) {
    return request({
      url: `/files/${id}`,
      method: 'delete'
    })
  },

  // 获取文件统计信息
  getFileStats() {
    return request({
      url: '/files/stats',
      method: 'get'
    })
  }
}

export default fileApi

// 导出具体方法
export const {
  uploadFile,
  uploadAvatar,
  getUserFiles,
  getUserImageFiles,
  searchFiles,
  deleteFile,
  getFileStats
} = fileApi