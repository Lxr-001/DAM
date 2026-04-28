<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <!-- <a-col :span="24">
            <a-form-model-item label="部门ID" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="departId">
              <a-input v-model="model.departId" placeholder="请输入部门ID"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="部门名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="departName">
              <a-input v-model="model.departName" placeholder="请输入部门名称"  ></a-input>
            </a-form-model-item>
          </a-col> -->
          <a-col :span="24">
            <a-form-model-item label="空间名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="spaceName">
              <a-input v-model="model.spaceName" placeholder="请输入空间名称"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="空间类型" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="spaceType">
              <a-select v-model="model.spaceType" placeholder="请选择空间类型">
                <a-select-option value="公共">公共</a-select-option>
                <a-select-option value="私有">私有</a-select-option>
                <a-select-option value="项目">项目</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="空间容量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="spaceCapacity">
              <a-input v-model="model.spaceCapacity" placeholder="请输入空间容量"  ></a-input>
            </a-form-model-item>
          </a-col>
          <!-- <a-col :span="24">
            <a-form-model-item label="成员数" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="userCount">
              <a-input v-model="model.userCount" placeholder="请输入成员数"  ></a-input>
            </a-form-model-item>
          </a-col> -->
        </a-row>
      </a-form-model>
    </j-form-container>
  </a-spin>
</template>

<script>

  import { httpAction, getAction } from '@/api/manage'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: 'SpaceForm',
    components: {
    },
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      },
      departId: {
        type: [String, Number],
        default: ''
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
          add: "/space/add",
          edit: "/space/edit",
          queryById: "/space/queryById"
        }
      }
    },
    computed: {
      formDisabled(){
        return this.disabled
      },
      validatorRules:function() {
        return {
          spaceName:[{ required: true, message: '请输入空间名称!' }],
          spaceType:[{ required: true, message: '请选择空间类型!' }],
        }
      },
    },
    created () {
       //备份model原始值
      this.modelDefault = JSON.parse(JSON.stringify(this.model));
    },
    methods: {
      add (departId) {
        const currentDepartId = departId || this.departId
        const addRecord = Object.assign({}, this.modelDefault, {
          departId: currentDepartId
        })
        this.edit(addRecord);
      },
      edit (record) {
        this.model = Object.assign({}, record);
        this.visible = true;
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
            httpAction(httpurl,this.model,method).then((res)=>{
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