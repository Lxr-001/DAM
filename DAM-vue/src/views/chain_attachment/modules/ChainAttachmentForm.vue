<template>
  <div class="clearfix">
    <a-form-model ref="form" slot="detail" :model="model" :rules="validatorRules">
      <a-row>
        <a-col :span="24">
          <a-form-model-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="密级"
            prop="security"
          >
            <j-dict-select-tag
              v-model="model.security"
              v-decorator="['security', { rules: [{ required: true, message: '请选择密级!' }] }]"
              dictCode="system_security"
              placeholder="请选择密级"
              type="list"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="24">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注" prop="remark">
            <a-input v-model="model.remark" placeholder="请输入备注"></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :span="24">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="附件" prop="remark">
            <a-upload
              :before-upload="beforeUpload"
              :file-list="fileList"
              :multiple="true"
              :remove="handleRemove"
            >
              <a-button :disabled="isShow" style="text-align: center;">
                <a-icon type="upload" />
                选择文件
              </a-button>
            </a-upload>
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model>
  </div>
</template>
<script>
import { postAction, httpAction, getAction } from '@/api/manage'
import { USER_INFO } from '@/store/mutation-types'

export default {
  props: {
    // 挂载数据的密级，通过父组件传递
    parentSecurity: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      isShow: true,
      visible: false,
      pid: '',
      userSecurity: 0, // 当前用户密级
      maxAllowedSecurity: 0, // 允许的最大密级（用户密级和挂载数据密级的最小值）
      validatorRules: {
        security: [
          { required: true, message: '请选择密级!' },
          { validator: (rule, value, callback) => {
              if (value > this.maxAllowedSecurity) {
                callback('附件密级不能高于当前用户密级和挂载数据密级的最小值');
              } else {
                callback();
              }
            }
          }
        ]
      },
      model: {
        remark: ''
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      fileList: [],
      uploading: false
    }
  },
  watch: {
    // 监听父组件传递的挂载数据密级变化
    parentSecurity: {
      handler(newVal) {
        this.updateMaxAllowedSecurity();
      },
      immediate: true
    }
  },
  created() {
    this.getUserSecurity();
  },
  inject: ['submitCallback'],
  methods: {
    // 获取当前用户密级
    getUserSecurity() {
      try {
        // 从本地存储获取用户信息
        const userInfoStr = localStorage.getItem(USER_INFO);
        if (userInfoStr) {
          const userInfo = JSON.parse(userInfoStr);
          this.userSecurity = parseInt(userInfo.security || 0);
          this.updateMaxAllowedSecurity();
        }
      } catch (error) {
        console.error('获取用户密级失败:', error);
      }
    },

    // 不再需要通过接口获取挂载数据密级，改为通过props接收

    // 更新允许的最大密级
    updateMaxAllowedSecurity() {
      // 取用户密级和挂载数据密级的最小值
      this.maxAllowedSecurity = Math.min(this.userSecurity, this.parentSecurity || this.userSecurity);
      console.log('允许的最大密级:', this.maxAllowedSecurity);
    },

    edit(record) {
      this.model = Object.assign({}, record)
      this.visible = true
    },
    handleRemove(file) {
      const index = this.fileList.indexOf(file)
      const newFileList = this.fileList.slice()
      newFileList.splice(index, 1)
      this.fileList = newFileList
    },
    beforeUpload(file) {
      this.fileList = [...this.fileList, file]
      return false
    },
    handleUpload() {
      const { fileList } = this
      const formData = new FormData()
      fileList.forEach(file => {
        formData.append('files', file)
      })
      const that = this

      // 再次更新最大允许密级，确保最新值生效
      this.updateMaxAllowedSecurity();

      // 触发表单验证
      this.$refs.form.validate(valid => {
        if (valid) {
          // 确保密级不超过允许的最大值
          if (parseInt(this.model.security) > this.maxAllowedSecurity) {
            this.$message.error('附件密级不能高于当前用户密级和挂载数据密级的最小值');
            return 'invalid';
          }

          if (this.model.id != undefined && this.model.id != '') {
            that.confirmLoading = true
            let httpurl = '/chainAttachment/edit'
            let method = 'put'
            httpAction(httpurl, this.model, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
              } else {
                that.$message.warning(res.message)
              }
            }).finally(() => {
              that.confirmLoading = false
            })
          } else {
            if (fileList.length === 0) {
              this.$message.error('请选择上传文件!!')
              return
            }
            console.log('this.model.security: ', this.model.security)
            this.uploading = true
            postAction('/chainAttachment/uploadFile?security=' + this.model.security + '&pid=' + this.pid + '&remark=' + this.model.remark, formData).then((res) => {
              this.uploading = false
              if (res.success) {
                this.fileList = []
                this.uploading = false
                this.submitCallback()
                this.$message.success('文件上传成功')
              } else {
                this.uploading = false
                this.$message.error('upload failed.')
              }
            })
          }
          return 'valid'
        } else if (!this.model.security) {
          this.$message.error('请选择密级!!')
          return 'invalid'
        }
      })
    }
  }
}
</script>
