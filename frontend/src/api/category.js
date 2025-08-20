import request from '@/utils/request'

export const categoryApi = {
  // 获取所有分类
  getAllCategories() {
    return request({
      url: '/categories',
      method: 'get'
    })
  },

  // 根据slug获取分类
  getCategoryBySlug(slug) {
    return request({
      url: `/categories/${slug}`,
      method: 'get'
    })
  }
}

// 具名导出，方便按需导入
export const getAllCategories = categoryApi.getAllCategories
export const getCategoryBySlug = categoryApi.getCategoryBySlug

// 默认导出，兼容默认导入用法
export default categoryApi