<template>
  <a-drawer
    :visible="visible"
    :width="720"
    title="变更登记"
    placement="right"
    @close="handleClose"
  >
    <div class="registration-steps">
      <a-steps :current="currentStep" size="small">
        <a-step title="填写变更信息" />
        <a-step title="选择审批者" />
      </a-steps>
    </div>

    <!-- Step 1: 填写变更信息 -->
    <div v-if="currentStep === 0" class="step-content">
      <a-form :form="step1Form" layout="vertical">
        <a-divider>资产基本信息</a-divider>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="资产名称" required>
              <a-input
                v-decorator="['assetName', { rules: [{ required: true, message: '请输入资产名称' }] }]"
                placeholder="请输入资产名称"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="更新频率">
              <a-select v-decorator="['updateFrequency']" placeholder="请选择更新频率">
                <a-select-option value="按天更新">按天更新</a-select-option>
                <a-select-option value="按周更新">按周更新</a-select-option>
                <a-select-option value="按月更新">按月更新</a-select-option>
                <a-select-option value="按年更新">按年更新</a-select-option>
                <a-select-option value="按需更新">按需更新</a-select-option>
                <a-select-option value="不更新">不更新</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="资产描述">
          <a-textarea v-decorator="['assetDesc']" :rows="3" placeholder="请输入资产描述" />
        </a-form-item>

        <template v-if="selectedAsset && selectedAsset.assetType === 'dataset'">
          <a-divider>数据分类信息</a-divider>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="数据密级">
                <a-select v-decorator="['dataLevel']" placeholder="请选择数据密级">
                  <a-select-option value="公开">公开</a-select-option>
                  <a-select-option value="内部">内部</a-select-option>
                  <a-select-option value="秘密">秘密</a-select-option>
                  <a-select-option value="机密">机密</a-select-option>
                  <a-select-option value="绝密">绝密</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="数据形态">
                <a-select v-decorator="['dataStructure']" placeholder="请选择数据形态">
                  <a-select-option value="structured">结构化数据</a-select-option>
                  <a-select-option value="unstructured">非结构化数据</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
        </template>

        <template v-if="selectedAsset && selectedAsset.assetType === 'api'">
          <a-divider>API分类信息</a-divider>
          <a-row :gutter="16">
            <a-col :span="8">
              <a-form-item label="开放程度">
                <a-select v-decorator="['openDegree']" placeholder="请选择">
                  <a-select-option value="open">开放API</a-select-option>
                  <a-select-option value="internal">内部API</a-select-option>
                  <a-select-option value="private">私有API</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="操作类型">
                <a-select v-decorator="['operationType']" placeholder="请选择">
                  <a-select-option value="read_only">只读API</a-select-option>
                  <a-select-option value="write_only">只写API</a-select-option>
                  <a-select-option value="read_write">读写API</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="数据粒度">
                <a-select v-decorator="['dataGranularity']" placeholder="请选择">
                  <a-select-option value="detail">明细API</a-select-option>
                  <a-select-option value="summary">汇总API</a-select-option>
                  <a-select-option value="wide_table">宽表API</a-select-option>
                  <a-select-option value="theme_domain">主题域API</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
          <a-form-item label="调用地址">
            <a-input v-decorator="['apiAddress']" placeholder="请输入API调用地址" />
          </a-form-item>
        </template>

        <template v-if="selectedAsset && selectedAsset.assetType === 'training_set'">
          <a-divider>训练集分类信息</a-divider>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="数据格式">
                <a-select v-decorator="['dataFormat']" placeholder="请选择">
                  <a-select-option value="json">json</a-select-option>
                  <a-select-option value="csv">csv</a-select-option>
                  <a-select-option value="text">text</a-select-option>
                  <a-select-option value="imagefolder">imagefolder</a-select-option>
                  <a-select-option value="soundfolder">soundfolder</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="数据模态">
                <a-select v-decorator="['dataModality']" placeholder="请选择">
                  <a-select-option value="text">文本数据</a-select-option>
                  <a-select-option value="image">图像数据</a-select-option>
                  <a-select-option value="audio">音频数据</a-select-option>
                  <a-select-option value="video">视频数据</a-select-option>
                  <a-select-option value="multimodal">多模态数据</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
        </template>

        <a-divider>数据体量</a-divider>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="数据规模（条）">
              <a-input-number v-decorator="['dataScale']" style="width: 100%" :min="0" placeholder="请输入" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="数据体量">
              <a-slider v-model="dataVolumeSlider" :marks="{ 0: '0K', 500: '500G', 1000: '1T' }" :min="0" :max="1000" />
              <div style="text-align: center">{{ formatDataVolume(dataVolumeSlider) }}</div>
            </a-form-item>
          </a-col>
        </a-row>
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
import { submitChange } from '@/api/spaceAsset'
import { getAction } from '@/api/manage'

export default {
  name: 'ChangeRegistrationModal',
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
      spaceUsers: [],
      dataVolumeSlider: 0
    }
  },
  created() {
    this.loadSpaceUsers()
  },
  watch: {
    visible(val) {
      if (val) {
        this.show()
      }
    }
  },
  methods: {
    show() {
      this.currentStep = 0
      // 重置表单
      this.step1Form.resetFields()
      this.step2Form.resetFields()
      this.dataVolumeSlider = 0

      if (this.selectedAsset) {
        // 根据数据体量设置滑块值
        const volume = this.selectedAsset.dataVolume || '0K'
        let sliderValue = 0
        if (volume.endsWith('K')) {
          sliderValue = parseInt(volume) || 0
        } else if (volume.endsWith('G')) {
          sliderValue = parseInt(volume) || 0
        } else if (volume.endsWith('T')) {
          sliderValue = 1000
        }
        this.dataVolumeSlider = sliderValue

        this.$nextTick(() => {
          this.step1Form.setFieldsValue({
            assetName: this.selectedAsset.assetName,
            assetDesc: this.selectedAsset.assetDesc,
            updateFrequency: this.selectedAsset.updateFrequency,
            dataLevel: this.selectedAsset.dataLevel,
            dataStructure: this.selectedAsset.dataStructure,
            dataFormat: this.selectedAsset.dataFormat,
            dataModality: this.selectedAsset.dataModality,
            openDegree: this.selectedAsset.openDegree,
            operationType: this.selectedAsset.operationType,
            dataGranularity: this.selectedAsset.dataGranularity,
            apiAddress: this.selectedAsset.apiAddress,
            dataScale: this.selectedAsset.dataScale ? parseInt(this.selectedAsset.dataScale) : null
          })
        })
      }
    },
    handleClose() {
      this.$emit('close')
    },
    loadSpaceUsers() {
      getAction('/spaceUser/listBySpaceId', { spaceId: this.spaceInfo.id, pageSize: 100 }).then(res => {
        if (res.success) {
          this.spaceUsers = res.result.records || res.result || []
        }
      })
    },
    handleNextStep() {
      this.step1Form.validateFields(['assetName'], (err) => {
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
        const data = {
          id: this.selectedAsset.id,
          spaceId: this.spaceInfo.id,
          assetType: this.selectedAsset.assetType,
          assetName: step1Values.assetName,
          assetDesc: step1Values.assetDesc,
          updateFrequency: step1Values.updateFrequency,
          dataLevel: step1Values.dataLevel,
          dataStructure: step1Values.dataStructure,
          dataFormat: step1Values.dataFormat,
          dataModality: step1Values.dataModality,
          openDegree: step1Values.openDegree,
          operationType: step1Values.operationType,
          dataGranularity: step1Values.dataGranularity,
          apiAddress: step1Values.apiAddress,
          dataScale: step1Values.dataScale,
          dataVolume: this.formatDataVolume(this.dataVolumeSlider)
        }
        submitChange(data, values.approverId).then(res => {
          if (res.success) {
            this.$message.success('变更登记申请已提交')
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
        content: '是否要取消变更数据资产？',
        onOk: () => {
          this.handleClose()
        }
      })
    },
    formatDataVolume(value) {
      if (value <= 0) return '0K'
      if (value < 500) return value + 'K'
      if (value < 1000) return value + 'G'
      return '1T'
    }
  }
}
</script>

<style scoped>
.registration-steps {
  margin-bottom: 24px;
}

.step-content {
  min-height: 400px;
}

.step-footer {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}
</style>
