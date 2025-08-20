/**
 * UTF-8安全的Base64编码函数
 * @param {string} str - 要编码的字符串
 * @returns {string} Base64编码结果
 */
function utf8ToBase64(str) {
  try {
    // 使用encodeURIComponent处理UTF-8字符，然后转换为Base64
    return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, function(match, p1) {
      return String.fromCharCode('0x' + p1)
    }))
  } catch (error) {
    // 如果还是失败，直接返回URL编码的SVG
    console.warn('Base64编码失败，使用URL编码:', error)
    return encodeURIComponent(str)
  }
}

/**
 * 生成SVG占位图片
 * @param {number} width - 图片宽度
 * @param {number} height - 图片高度
 * @param {string} text - 显示文本
 * @param {string} bgColor - 背景颜色
 * @param {string} textColor - 文字颜色
 * @returns {string} SVG数据URL
 */
export function generatePlaceholderImage(
  width = 300, 
  height = 200, 
  text = `${width}×${height}`, 
  bgColor = '#f5f5f5', 
  textColor = '#999'
) {
  const svg = `
    <svg width="${width}" height="${height}" xmlns="http://www.w3.org/2000/svg">
      <rect width="100%" height="100%" fill="${bgColor}"/>
      <text x="50%" y="50%" font-family="Arial, sans-serif" font-size="16" 
            fill="${textColor}" text-anchor="middle" dominant-baseline="middle">
        ${text}
      </text>
    </svg>
  `
  
  try {
    return `data:image/svg+xml;base64,${utf8ToBase64(svg)}`
  } catch (error) {
    // 如果Base64编码失败，使用URL编码作为备选方案
    console.warn('SVG Base64编码失败，使用URL编码:', error)
    return `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`
  }
}

/**
 * 生成文章封面占位图
 * @param {number} width - 图片宽度
 * @param {number} height - 图片高度
 * @returns {string} SVG数据URL
 */
export function generateArticleCoverPlaceholder(width = 300, height = 200) {
  return generatePlaceholderImage(
    width, 
    height, 
    '文章封面', 
    '#e8f4fd', 
    '#409eff'
  )
}

/**
 * 生成用户头像占位图
 * @param {string} name - 用户名
 * @param {number} size - 头像大小
 * @returns {string} SVG数据URL
 */
export function generateAvatarPlaceholder(name = '用户', size = 40) {
  const firstChar = name.charAt(0).toUpperCase()
  const colors = [
    { bg: '#409eff', text: '#fff' },
    { bg: '#67c23a', text: '#fff' },
    { bg: '#e6a23c', text: '#fff' },
    { bg: '#f56c6c', text: '#fff' },
    { bg: '#909399', text: '#fff' }
  ]
  
  // 根据名字生成固定颜色
  const colorIndex = name.charCodeAt(0) % colors.length
  const color = colors[colorIndex]
  
  return generatePlaceholderImage(
    size, 
    size, 
    firstChar, 
    color.bg, 
    color.text
  )
}

/**
 * 生成默认图片占位符
 * @param {number} width - 图片宽度
 * @param {number} height - 图片高度
 * @returns {string} SVG数据URL
 */
export function generateDefaultPlaceholder(width = 200, height = 200) {
  return generatePlaceholderImage(
    width, 
    height, 
    '暂无图片', 
    '#f0f0f0', 
    '#c0c4cc'
  )
}