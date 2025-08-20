import { createApp } from 'vue'
import PositionedConfirm from '@/components/PositionedConfirm.vue'
import ElementPlus from 'element-plus'

/**
 * 在指定元素旁边显示确认弹窗
 * @param {string} message - 弹窗消息
 * @param {string} title - 弹窗标题
 * @param {Object} options - 配置选项
 * @param {HTMLElement} triggerElement - 触发元素
 * @returns {Promise} - 返回Promise，确认时resolve，取消时reject
 */
export function showPositionedConfirm(message, title = '提示', options = {}, triggerElement = null) {
  return new Promise((resolve, reject) => {
    // 创建容器元素
    const container = document.createElement('div')
    document.body.appendChild(container)
    
    // 默认配置
    const defaultOptions = {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }
    
    const finalOptions = { ...defaultOptions, ...options }
    
    // 创建Vue应用实例
    const app = createApp(PositionedConfirm, {
      visible: true,
      message,
      title,
      triggerElement,
      ...finalOptions,
      onConfirm: () => {
        cleanup()
        resolve('confirm')
      },
      onCancel: () => {
        cleanup()
        reject('cancel')
      },
      'onUpdate:visible': (visible) => {
        if (!visible) {
          cleanup()
          reject('cancel')
        }
      }
    })
    
    // 使用Element Plus
    app.use(ElementPlus)
    
    // 挂载应用
    app.mount(container)
    
    // 清理函数
    function cleanup() {
      setTimeout(() => {
        app.unmount()
        if (container.parentNode) {
          container.parentNode.removeChild(container)
        }
      }, 300) // 等待动画完成
    }
  })
}

/**
 * 获取事件目标元素
 * @param {Event} event - 事件对象
 * @returns {HTMLElement} - 目标元素
 */
export function getEventTarget(event) {
  return event.currentTarget || event.target
}

/**
 * 在按钮旁边显示删除确认弹窗
 * @param {string} itemName - 要删除的项目名称
 * @param {Event} event - 点击事件
 * @returns {Promise} - 返回Promise
 */
export function showDeleteConfirm(itemName, event) {
  const triggerElement = getEventTarget(event)
  return showPositionedConfirm(
    `确定要删除"${itemName}"吗？删除后无法恢复！`,
    '确认删除',
    {
      type: 'error',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    },
    triggerElement
  )
}

/**
 * 在按钮旁边显示警告确认弹窗
 * @param {string} message - 警告消息
 * @param {string} title - 标题
 * @param {Event} event - 点击事件
 * @returns {Promise} - 返回Promise
 */
export function showWarningConfirm(message, title = '警告', event) {
  const triggerElement = getEventTarget(event)
  return showPositionedConfirm(
    message,
    title,
    {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    },
    triggerElement
  )
}

/**
 * 在按钮旁边显示信息确认弹窗
 * @param {string} message - 信息消息
 * @param {string} title - 标题
 * @param {Event} event - 点击事件
 * @returns {Promise} - 返回Promise
 */
export function showInfoConfirm(message, title = '提示', event) {
  const triggerElement = getEventTarget(event)
  return showPositionedConfirm(
    message,
    title,
    {
      type: 'info',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    },
    triggerElement
  )
}