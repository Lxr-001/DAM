<template>
  <a-drawer
    :visible="visible"
    :width="600"
    title="转让登记"
    placement="right"
    @close="handleClose"
  >
    <div class="registration-steps">
      <a-steps :current="currentStep" size="small">
        <a-step title="选择转让用户" />
        <a-step title="选择审批者" />
      </a-steps>
    </div>

    <!-- Step 1: 选择转让用户 -->
    <div v-if="currentStep === 0" class="step-content">
      <a-form :form="step1Form" layout="vertical">
        <a-form-item label="当前数据Owner">
          <a-input :value="selectedAsset ? selectedAsset.ownerName : '-'" disabled />
        </a-form-item>
        <a-form-item label="资产名称">
          <a-input :value="selectedAsset ? selectedAsset.assetName : '-'" disabled />
        </a-form-item>
        <a-form-item label="选择新Owner" required>
          <a-select
            v-decorator="['newOwnerId', { rules: [{ required: true, message: '请选择新Owner' }] }]"
            placeholder="请从当前空间用户列表中选择新Owner"
            mode="multiple"
          >
            <a-select-option v-for="user in spaceUsers" :key="user.userId" :value="user.userId">
              {{ user.realname || user.username || '未知用户' }}{{ user.roleNames ? '-' + user.roleNames : '' }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <div class="step-footer">
        <a-button type="primary" @click="handleNextStep">下一步</a-button>
      </div>
    </div>

    <!-- Step 2: 选择审批者 -->
    <div v-if="currentStep === 1" class="step-content">
      <a-form :form="step2Form" layout="vertical">
        <a-form-item label="选择审批者" required>
          <a-select
            v-decorator="['approverId', { rules: [{ required: true, message: '请选择审批者' }] }]"
            placeholder="请从当前空间用户中选择一个作为审批者"
          >
            <a-select-option v-for="user in spaceUsers" :key="user.userId" :value="user.userId">
              {{ user.realname || user.username || '未知用户' }}{{ user.roleNames ? '-' + user.roleNames : '' }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <div class="step-footer">
        <a-button @click="handlePrevStep">上一步</a-button>
        <a-button type="primary" style="margin-left: 8px" @click="handleSubmit">提交</a-button>
        <a-button style="margin-left: 8px" @click="handleCancel">取消</a-button>
      </div>
    </div>
  </a-drawer>
</template>

<script>
import { submitTransfer } from '@/api/spaceAsset'
import { getAction } from '@/api/manage'

export default {
  name: 'TransferRegistrationModal',
  props: {
    visible: { type: Boolean, default: false },
    spaceInfo: { type: Object, default: () => ({}) },
    selectedAsset: { type: Object, default: () => null }
  },
  data() {
    return {
      currentStep: 0,
      step1Form: this.$form.createForm(this),
      step2Form: this.$form.createForm(this),
      spaceUsers: []
    }
  },
  created() {
    this.loadSpaceUsers()
  },
  methods: {
    show() {
      this.currentStep = 0
    },
    handleClose() {
      this.$emit('close')
      this.step1Form.resetFields()
      this.step2Form.resetFields()
    },
    loadSpaceUsers() {
      getAction('/spaceUser/listBySpaceId', { spaceId: this.spaceInfo.id, pageSize: 100 }).then(res => {
        if (res.success) {
          this.spaceUsers = res.result.records || res.result || []
        }
      })
    },
    handleNextStep() {
      this.step1Form.validateFields(['newOwnerId'], (err) => {
        if (!err) this.currentStep++
      })
    },
    handlePrevStep() {
      if (this.currentStep > 0) this.currentStep--
    },
    handleSubmit() {
      this.step2Form.validateFields(['approverId'], (err, values) => {
        if (err) return
        const step1Values = this.step1Form.getFieldsValue()
        const params = {
          assetId: this.selectedAsset.id,
          newOwnerId: Array.isArray(step1Values.newOwnerId) ? step1Values.newOwnerId[0] : step1Values.newOwnerId,
          approverId: values.approverId
        }
        submitTransfer(params).then(res => {
          if (res.success) {
            this.$message.success('转让登记申请已提交')
            this.$emit('success')
            this.handleClose()
          } else {
            this.$message.error(res.message || '提交失败')
          }
        })
      })
    },
    handleCancel() {
      this.$confirm({
        title: '提示',
        content: '是否要取消转让数据资产？',
        onOk: () => {
          this.handleClose()
        }
      })
    }
  }
}
</script>

<style scoped>
.registration-steps {
  margin-bottom: 24px;
}

.step-content {
  min-height: 300px;
}

.step-footer {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}
</style>
