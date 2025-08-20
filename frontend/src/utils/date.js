/**
 * 日期格式化工具函数
 */

/**
 * 格式化日期
 * @param {Date|string} date - 日期对象或日期字符串
 * @param {string} format - 格式化模板，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''
  
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化相对时间（多久前）
 * @param {Date|string} date - 日期对象或日期字符串
 * @returns {string} 相对时间字符串
 */
export function formatRelativeTime(date) {
  if (!date) return ''
  
  const now = new Date()
  const target = new Date(date)
  const diff = now - target
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const week = 7 * day
  const month = 30 * day
  const year = 365 * day
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < week) {
    return `${Math.floor(diff / day)}天前`
  } else if (diff < month) {
    return `${Math.floor(diff / week)}周前`
  } else if (diff < year) {
    return `${Math.floor(diff / month)}个月前`
  } else {
    return `${Math.floor(diff / year)}年前`
  }
}

/**
 * 格式化日期为简短格式
 * @param {Date|string} date - 日期对象或日期字符串
 * @returns {string} 简短格式的日期字符串
 */
export function formatDateShort(date) {
  return formatDate(date, 'YYYY-MM-DD')
}

/**
 * 格式化时间为简短格式
 * @param {Date|string} date - 日期对象或日期字符串
 * @returns {string} 简短格式的时间字符串
 */
export function formatTimeShort(date) {
  return formatDate(date, 'HH:mm')
}

/**
 * 判断是否为今天
 * @param {Date|string} date - 日期对象或日期字符串
 * @returns {boolean} 是否为今天
 */
export function isToday(date) {
  if (!date) return false
  
  const today = new Date()
  const target = new Date(date)
  
  return today.getFullYear() === target.getFullYear() &&
         today.getMonth() === target.getMonth() &&
         today.getDate() === target.getDate()
}

/**
 * 判断是否为昨天
 * @param {Date|string} date - 日期对象或日期字符串
 * @returns {boolean} 是否为昨天
 */
export function isYesterday(date) {
  if (!date) return false
  
  const yesterday = new Date()
  yesterday.setDate(yesterday.getDate() - 1)
  const target = new Date(date)
  
  return yesterday.getFullYear() === target.getFullYear() &&
         yesterday.getMonth() === target.getMonth() &&
         yesterday.getDate() === target.getDate()
}

/**
 * 获取友好的日期显示
 * @param {Date|string} date - 日期对象或日期字符串
 * @returns {string} 友好的日期显示
 */
export function getFriendlyDate(date) {
  if (!date) return ''
  
  if (isToday(date)) {
    return '今天 ' + formatTimeShort(date)
  } else if (isYesterday(date)) {
    return '昨天 ' + formatTimeShort(date)
  } else {
    return formatDate(date, 'MM-DD HH:mm')
  }
}

export default {
  formatDate,
  formatRelativeTime,
  formatDateShort,
  formatTimeShort,
  isToday,
  isYesterday,
  getFriendlyDate
}