<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="需求标题" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="demandTitile">
              <a-input v-model="model.demandTitile" placeholder="请输入需求标题"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="需求类型" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="demandType">
              <a-select v-model="model.demandType" placeholder="请选择需求类型">
                <a-select-option value="数据资源">数据资源</a-select-option>
                <a-select-option value="数据产品">数据产品</a-select-option>
                <a-select-option value="数据服务">数据服务</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="需求状态" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="demandState">
              <a-select v-model="model.demandState" placeholder="请选择需求状态" allow-clear>
                <a-select-option value="生效中">生效中</a-select-option>
                <a-select-option value="已完成">已完成</a-select-option>
                <a-select-option value="已过期">已过期</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="应用场景" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="applicationScenario">
              <a-select v-model="model.applicationScenario" placeholder="请选择应用场景">
                <a-select-option value="共享服务">共享服务</a-select-option>
                <a-select-option value="科研生产">科研生产</a-select-option>
                <a-select-option value="经营管理">经营管理</a-select-option>
                <a-select-option value="试验生产">试验生产</a-select-option>
                <a-select-option value="运行支撑">运行支撑</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <!-- <a-col :span="24">
            <a-form-model-item label="细分场景" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="segmentedScenario">
              <a-input v-model="model.segmentedScenario" placeholder="请输入细分场景"  ></a-input>
            </a-form-model-item>
          </a-col> -->
          <a-col :span="24">
            <a-form-model-item label="截止日期" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="demandDeadline">
              <j-date
                placeholder="请选择截止日期"
                v-model="model.demandDeadline"
                :show-time="false"
                date-format="YYYY-MM-DD"
                :disabled-date="disabledDeadlineDate"
                style="width: 100%"
              />
            </a-form-model-item>
          </a-col>

          
          <a-col :span="24">
            <a-form-model-item label="需求描述" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="demandDescription">
              <a-textarea v-model="model.demandDescription" placeholder="请输入需求描述"  ></a-textarea>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
  </a-spin>
</template>

<script>

  import { httpAction, getAction } from '@/api/manage'
  import { validateDuplicateValue } from '@/utils/util'
  import moment from 'moment'

  export default {
    name: 'DemandForm',
    components: {
    },
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data () {
      return {
        model:{
         },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        url: {
          add: "/demand/add",
          edit: "/demand/edit",
          queryById: "/demand/queryById"
        }
      }
    },
    computed: {
      formDisabled(){
        return this.disabled
      },
      validatorRules:function() {
        return {
          demandTitile:[{ required: true, message: '请输入需求标题!' }],
          demandType:[{ required: true, message: '请选择需求类型!' }],
          applicationScenario:[{ required: true, message: '请选择应用场景!' }],
          // segmentedScenario:[{ required: true, message: '请输入细分场景!' }],
          demandDeadline:[{ required: true, message: '请选择截止日期!' }],
          demandDescription:[{ required: true, message: '请输入需求描述!' }],
        }
      },
    },
    created () {
       //备份model原始值
      this.modelDefault = JSON.parse(JSON.stringify(this.model));
    },
    methods: {
      /**
       * 截止日期不可选今日之前的日期
       * @param {import('moment').Moment} current 日历中的日期
       * @returns {boolean} 为 true 时该日期不可选
       */
      disabledDeadlineDate (current) {
        return current ? current.isBefore(moment(), 'day') : false
      },
      add () {
        this.edit(this.modelDefault);
      },
      edit (record) {
        const m = Object.assign({}, record)
        // 后端常为 yyyy-MM-dd HH:mm:ss，j-date 仅展示日期时需规范为 YYYY-MM-DD
        if (m.demandDeadline) {
          const parsed = moment(m.demandDeadline)
          if (parsed.isValid()) {
            m.demandDeadline = parsed.format('YYYY-MM-DD')
          }
        }
        this.model = m
        this.visible = true;
      },
      /**
       * 将截止日期格式化为后端 Jackson 可解析的 yyyy-MM-dd HH:mm:ss
       * @param {string|undefined} value 表单中的 demandDeadline
       * @returns {string|undefined}
       */
      formatDemandDeadlineForSubmit (value) {
        if (value == null || value === '') return value
        const s = String(value).trim()
        if (/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(s)) {
          return s
        }
        if (/^\d{4}-\d{2}-\d{2}$/.test(s)) {
          return `${s} 00:00:00`
        }
        const parsed = moment(s)
        return parsed.isValid() ? parsed.format('YYYY-MM-DD HH:mm:ss') : s
      },
      submitForm () {
        const that = this;
        // 触发表单验证
        this.$refs.form.validate(valid => {
          if (valid) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            const payload = Object.assign({}, that.model, {
              demandDeadline: that.formatDemandDeadlineForSubmit(that.model.demandDeadline)
            })
            httpAction(httpurl, payload, method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
            })
          }
         
        })
      },
    }
  }
</script>