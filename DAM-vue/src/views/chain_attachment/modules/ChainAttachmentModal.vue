<template>
  <j-modal
    :okButtonProps="{ class: { 'jee-hidden': disableSubmit } }"
    :title="title"
    :visible="visible"
    :width="width"
    cancelText="关闭"
    switchFullscreen
    @cancel="handleCancel"
    @ok="handleOk"
  >
    <chain-attachment-form ref="realForm" :disabled="disableSubmit" :parentSecurity="parentSecurity" @ok="submitCallback"></chain-attachment-form>
  </j-modal>
</template>

<script>

import ChainAttachmentForm from './ChainAttachmentForm'

export default {
  name: 'ChainAttachmentModal',
  components: {
    ChainAttachmentForm
  },
  props: {
    // 挂载数据的密级，通过父组件传递
    parentSecurity: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      title: '上传附件',
      width: 800,
      visible: false,
      disableSubmit: false
    }
  },
  provide() {
    return {
      submitCallback: this.submitCallback
    }
  },
  methods: {
    test() {
      this.visible = false
    },
    show(id) {
      this.visible = true
      this.$nextTick(() => {
        this.$refs.realForm.pid = id
        this.$refs.realForm.isShow = false
        console.log('ChainAttachmentModal show:', id, 'parentSecurity:', this.parentSecurity)
      })
    },
    add() {
      this.visible = true
      this.$nextTick(() => {
        this.$refs.realForm.add()
      })
    },
    edit(record) {
      this.visible = true
      this.$nextTick(() => {
        this.$refs.realForm.edit(record)
      })
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    handleOk() {
      var res = this.$refs.realForm.handleUpload()
      if (res === 'valid') {
        this.visible = false
      }
    },
    submitCallback() {
      this.$emit('ok')
      this.visible = false
    },
    handleCancel() {
      this.close()
    }
  }
}
</script>
