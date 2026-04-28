<template>
  <a-modal
    :confirmLoading="confirmLoading"
    :ok="false"
    :okButtonProps="{ props: { disabled: disableSubmit } }"
    :title="title"
    :visible="visible"
    :width="800"
    cancelText="关闭"
    @cancel="handleCancel"
    @ok="handleOk"
  >
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <a-form-model-item
          v-if="pt"
          label="所属平台"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol1"
          prop="sspt">
          <j-dict-select-tag type="list" v-model="model.sspt" dictCode="fly_pt" placeholder="请选择所属平台" />
        </a-form-model-item>

        <a-form-model-item label="密级" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="secretLevel">
          <j-dict-select-tag
            v-model="model.secretLevel"
            :showKOption="false"
            :key="maxLevel"
            :maxLevel="maxLevel"
            dictCode="system_security"
            placeholder="请选择密级"
            type="list"
          />
        </a-form-model-item>
        <a-form-model-item
          :hidden="false"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          hasFeedback
          label="成品型号"
          prop="cpxh"
        >
          <a-input v-model="model.cpxh" placeholder="请输入成品型号" />
        </a-form-model-item>
        <a-form-model-item
          :hidden="false"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          hasFeedback
          label="成品名称"
          prop="cpmc"
        >
          <a-input v-model="model.cpmc" placeholder="请输入成品名称" />
        </a-form-model-item>
        <a-form-model-item
          :hidden="false"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          hasFeedback
          label="研制单位"
          prop="yzdw"
        >
          <a-input v-model="model.yzdw" placeholder="请输入研制单位" />
        </a-form-model-item>
        <a-form-model-item
          :hidden="false"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          hasFeedback
          label="产品名称"
          prop="productName"
        >
          <a-input v-model="model.productName" placeholder="请输入产品名称" />
        </a-form-model-item>
        <a-form-model-item
          :hidden="false"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          hasFeedback
          label="产品级别"
          prop="cpjb"
        >
          <j-dict-select-tag type="list" v-model="model.cpjb" dictCode="cpjb" placeholder="请选择产品级别" />
        </a-form-model-item>
        <a-form-model-item
          :hidden="false"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          hasFeedback
          label="产品重要度"
          prop="cpzyd"
        >
          <j-dict-select-tag type="list" v-model="model.cpzyd" dictCode="abc"
                             placeholder="请选择产品重要度" />
        </a-form-model-item>
        <a-form-model-item
          :hidden="false"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          hasFeedback
          label="产品类型"
          prop="cplx"
        >
          <j-dict-select-tag type="list" v-model="model.cplx" dictCode="productType" placeholder="请选择产品类型" />
        </a-form-model-item>
        <a-form-model-item
          :hidden="false"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          hasFeedback
          label="产品类别"
          prop="cplb"
        >
          <j-dict-select-tag type="list" v-model="model.cplb" dictCode="cplb" placeholder="请选择产品类别"
                             @change="(v) => handleCplbAsyncChange(v)" />
        </a-form-model-item>

        <a-form-model-item v-if="ifTctl" label="同厂同类编号" :labelCol="labelCol" :wrapperCol="wrapperCol"
                           prop="tctlNo">
          <a-input v-model="model.tctlNo" placeholder="请输入同厂同类编号"></a-input>
        </a-form-model-item>
      </a-form-model>
  </a-modal>
</template>

<script>

import { getAction, httpAction } from '@/api/manage'
import ATextarea from 'ant-design-vue/es/input/TextArea'
import { validateDuplicateValue } from '@/utils/util'

export default {
  name: 'ChainProductCatalogTreeModal',
  components: { ATextarea },
  data() {
    return {
      maxLevel: 0,
      level: 1,
      departTree: [],
      orgTypeData: [],
      phoneWarning: '',
      title: '操作',
      pt: false,
      visible: false,
      condition: true,
      disableSubmit: false,
      model: {},
      defaultModel: {},
      menuhidden: false,
      menuusing: true,
      ifTctl: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      confirmLoading: false,
      validatorRules: {
        sspt: [
          // { required: true},
          { required: true, message: '请选择所属平台' },
          //暂时不需要唯一性校验
          { validator: (rule, value, callback) => validateDuplicateValue('ak_pbs_info', 'sspt', value, this.model.id, callback) }
        ],
        secretLevel: [
          { required: true, message: '请选择密级' }
          //暂时不需要唯一性校验
          //{ validator: (rule, value, callback) => validateDuplicateValue('ak_pbs_info', 'sspt', value, this.model.id, callback)},
        ],
        cpmc: [
          { required: true, message: '请输入成品名称' }
          //暂时不需要唯一性校验
          //{ validator: (rule, value, callback) => validateDuplicateValue('ak_pbs_info', 'sspt', value, this.model.id, callback)},
        ],
        cpxh: [
          { required: true, message: '请输入成品型号' },
          //唯一性校验
          { validator: (rule, value, callback) => validateDuplicateValue('ak_pbs_info', 'cpxh', value, this.model.id, callback) }
        ],
        cpjb: [
          { required: true, message: '请选择产品级别' }
        ],
        cpzyd: [
          { required: true, message: '请选择产品重要度' }
        ],
        cplb: [
          { required: true, message: '请选择产品类别' }
        ]
      },
      url: {
        add: '/akPbsInfo/akPbsInfo/add',
        edit: '/akPbsInfo/akPbsInfo/edit'
      },
      dictDisabled: true
    }
  },
  created() {
  },
  methods: {
    loadTreeData() {
      var that = this
      getAction('/akPbsInfo/akPbsInfo/queryTreeList').then((res) => {
        if (res.success) {
          that.departTree = []
          for (let i = 0; i < res.result.length; i++) {
            let temp = res.result[i]
            that.departTree.push(temp)
          }
        }

      })
    },
    add(parentId, maxLevel) {
      console.log('pt', this.pt)
      this.pt = !parentId
      // 新增的节点默认为叶子节点
      this.model.isLeaf = '1'
      this.edit(parentId, maxLevel)
    },
    edit(parentId, maxLevel) {
      if (!parentId) {
        this.model.isLeaf = true
      }
      this.visible = true
      this.model.bid = parentId
      if(maxLevel){
        this.maxLevel = maxLevel;
      }
    },
    handleCplbAsyncChange(value) {
      switch (value) {
        case '2':
          this.ifTctl = true
          break
        default:
          this.ifTctl = false
      }
    },
    close() {
      this.$emit('close')
      this.disableSubmit = false
      this.visible = false
      this.$refs.form.resetFields()
    },
    handleOk() {
      const that = this
      // 触发表单验证
      this.$refs.form.validate(valid => {
        if (valid) {
          that.confirmLoading = true
          let httpurl = ''
          let method = ''
          if (!this.model.id) {
            httpurl += this.url.add
            method = 'post'
          } else {
            httpurl += this.url.edit
            method = 'put'
          }
          httpAction(httpurl, this.model, method).then((res) => {
            if (res.success) {
              that.$message.success(res.message)
              that.loadTreeData()
              that.$emit('ok')
            } else {
              that.$message.warning(res.message)
            }
          }).finally(() => {
            that.confirmLoading = false
            that.close()
          })

        } else {
          return false
        }
      })
    },
    handleCancel() {
      this.close()
    }
  }
}
</script>

<style scoped>
</style>