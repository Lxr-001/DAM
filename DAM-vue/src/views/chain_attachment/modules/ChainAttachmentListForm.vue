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
    <chain-attachment-list ref="realForm" :disabled="disableSubmit" @ok="submitCallback"></chain-attachment-list>
  </j-modal>
</template>

<script>

import ChainAttachmentList from '../ChainAttachmentList.vue'

export default {
  name: 'ChainAttachmentListForm',
  components: {
    ChainAttachmentList
  },
  data() {
    return {
      title: '附件列表',
      width: 900,
      visible: false,
      disableSubmit: false
    }
  },
  methods: {
    show(record) {
      this.visible = true
      this.$nextTick(() => {
        this.$refs.realForm.show(record)
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
      // this.$refs.realForm.handleUpload();
      this.visible = false
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
