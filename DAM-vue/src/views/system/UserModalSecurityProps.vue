<template>
  <a-modal
    title="确认操作"
    :visible="visible"
    @cancel="handleCancel"
    @ok="handleOk"
  >
    <user-list-security-props :maxSecurity=maxSecurity />
  </a-modal>

</template>
<script>

import UserListSecurityProps from '@views/system/UserListSecurityProps.vue'

export default {
  name: 'UserModalSecurityProps',
  props: {
    maxSecurity: {
      type: Number
    }
  },
  components: { UserListSecurityProps },
  data() {
    return {
      visible: false
    }
  },
  mounted() {
    console.log('this.maxSecurity: ', this.maxSecurity)
  },
  methods: {
    show() {
      this.visible = true
    },
    hide() {
      this.visible = false
    },
    handleOk() {
      /* 确认逻辑 */
      this.hide()
    },
    handleCancel() {
      this.hide()
    }
  }
}
</script>


<style scoped>

</style>