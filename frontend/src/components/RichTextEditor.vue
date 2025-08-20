<template>
  <div class="rich-text-editor">
    <div ref="quillEditor" class="quill-editor"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import Quill from 'quill'
import 'quill/dist/quill.snow.css'
import { ElMessage } from 'element-plus'
import { fileApi } from '@/api/file'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '请输入内容...'
  },
  height: {
    type: String,
    default: '400px'
  }
})

const emit = defineEmits(['update:modelValue'])

const quillEditor = ref(null)
let quill = null

// 自定义图片上传处理
const imageHandler = () => {
  const input = document.createElement('input')
  input.setAttribute('type', 'file')
  input.setAttribute('accept', 'image/*')
  input.click()

  input.onchange = async () => {
    const file = input.files[0]
    if (file) {
      try {
        const response = await fileApi.uploadFile(file)
        if (response.code === 200) {
          const range = quill.getSelection()
          quill.insertEmbed(range.index, 'image', response.data.fileUrl)
          ElMessage.success('图片上传成功')
        } else {
          ElMessage.error('图片上传失败')
        }
      } catch (error) {
        console.error('图片上传失败:', error)
        ElMessage.error('图片上传失败')
      }
    }
  }
}

// 自定义视频上传处理
const videoHandler = () => {
  const input = document.createElement('input')
  input.setAttribute('type', 'file')
  input.setAttribute('accept', 'video/*')
  input.click()

  input.onchange = async () => {
    const file = input.files[0]
    if (file) {
      try {
        const response = await fileApi.uploadFile(file)
        if (response.code === 200) {
          const range = quill.getSelection()
          quill.insertEmbed(range.index, 'video', response.data.fileUrl)
          ElMessage.success('视频上传成功')
        } else {
          ElMessage.error('视频上传失败')
        }
      } catch (error) {
        console.error('视频上传失败:', error)
        ElMessage.error('视频上传失败')
      }
    }
  }
}

// 插入链接处理
const linkHandler = () => {
  const range = quill.getSelection()
  if (range) {
    const url = prompt('请输入链接地址:')
    if (url) {
      const text = prompt('请输入链接文字:', url)
      if (text) {
        quill.insertText(range.index, text, 'link', url)
      }
    }
  }
}

onMounted(() => {
  // 配置工具栏
  const toolbarOptions = [
    [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
    [{ 'font': [] }],
    [{ 'size': ['small', false, 'large', 'huge'] }],
    ['bold', 'italic', 'underline', 'strike'],
    [{ 'color': [] }, { 'background': [] }],
    [{ 'script': 'sub'}, { 'script': 'super' }],
    [{ 'list': 'ordered'}, { 'list': 'bullet' }],
    [{ 'indent': '-1'}, { 'indent': '+1' }],
    [{ 'direction': 'rtl' }],
    [{ 'align': [] }],
    ['blockquote', 'code-block'],
    ['link', 'image', 'video'],
    ['clean']
  ]

  // 初始化Quill编辑器
  quill = new Quill(quillEditor.value, {
    theme: 'snow',
    placeholder: props.placeholder,
    modules: {
      toolbar: {
        container: toolbarOptions,
        handlers: {
          'image': imageHandler,
          'video': videoHandler,
          'link': linkHandler
        }
      }
    }
  })

  // 设置编辑器高度
  quillEditor.value.querySelector('.ql-editor').style.minHeight = props.height

  // 监听内容变化
  quill.on('text-change', () => {
    const html = quill.root.innerHTML
    emit('update:modelValue', html === '<p><br></p>' ? '' : html)
  })

  // 设置初始内容
  if (props.modelValue) {
    quill.root.innerHTML = props.modelValue
  }
})

// 监听外部内容变化
watch(() => props.modelValue, (newVal) => {
  if (quill && newVal !== quill.root.innerHTML) {
    quill.root.innerHTML = newVal || ''
  }
})

// 暴露方法给父组件
defineExpose({
  getQuill: () => quill,
  focus: () => quill?.focus(),
  blur: () => quill?.blur()
})
</script>

<style scoped>
.rich-text-editor {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.rich-text-editor:hover {
  border-color: #c0c4cc;
}

.rich-text-editor:focus-within {
  border-color: #409eff;
}

:deep(.ql-toolbar) {
  border-bottom: 1px solid #dcdfe6;
  border-top: none;
  border-left: none;
  border-right: none;
}

:deep(.ql-container) {
  border: none;
  font-size: 14px;
}

:deep(.ql-editor) {
  line-height: 1.6;
  padding: 12px 15px;
}

:deep(.ql-editor.ql-blank::before) {
  color: #c0c4cc;
  font-style: normal;
}

:deep(.ql-snow .ql-tooltip) {
  z-index: 9999;
}
</style>