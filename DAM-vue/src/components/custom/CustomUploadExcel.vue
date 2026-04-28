<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :bodyStyle ="bodyStyle"
    :maskClosable="false"
    :mask = "true"
    destroyOnClose
    :footer="null"
    @cancel="handleCancel"
    cancelText="关闭">

    <div class="div_import">
      <span>
        <j-secret-level-select type="list" v-model="secretLevel" placeholder="请选择密级" />
      </span>
      <span class="left_border_line">
        <a-upload name="file"
                  :showUploadList="false"
                  :multiple="false"
                  :headers="tokenHeader"
                  :action="importExcelUrl"
                  :beforeUpload="beforeUpload"
                  @change="handleImport"
                  accept=".xls,.xlsx"
        >
        <a-button type="primary" icon="upload">选择文件</a-button>
      </a-upload>
      </span>
      <span v-if="templateFilename" class="left_border_line">
        导入模板下载：
        <a :href='templateFilePath' target="_blank" :download='templateFilename'>{{templateFilename}}</a>
      </span>
      <span v-if="importModel" class="left_border_line">
        <a-radio-group v-model="importModelVal">
          <a-radio :value="1">追加导入</a-radio>
          <a-radio :value="2">更新导入</a-radio>
        </a-radio-group>
      </span>
      <span v-if="importModel" class="left_border_line">
        <a-button type="link" icon="delete" @click="clear">清空</a-button>
      </span>

      <div class="div_import_result">
        <span>
          <strong>文件名称：</strong>{{fileName}}&nbsp;&nbsp; &nbsp;&nbsp; <strong>文件大小</strong>：{{fileSize}} kb<br/>
          <strong>导入结果：</strong><br/>
        </span>

        <span v-if="importSuccess == 0">
          &nbsp;&nbsp;文件导入成功，导入数据<span class="imp_number">{{successNum}}</span>条，错误数据<span class="imp_number">{{errorNum}}</span>条。
          <span v-html="otherMsg" />
          <span v-if="errorNum > 0">错误信息 请下载查看。<a :href='errorFilePath' target="_blank" :download='errorFileName'>点击下载</a>！</span>
        </span>

        <span v-if="importSuccess == 1">
          文件导入出错，错误信息如下：<br/>
          {{fileErrorMsg}}
        </span>
      </div>
    </div>


  </a-modal>

</template>

<script>
    import { JeecgListMixin } from '@/mixins/JeecgListMixin'
    import Vue from 'vue'
    import JSecretLevelSelect from '@comp/dict/JSecretLevelSelect.vue'

    export default {
      name: 'CustomUploadExcel',
      components: { JSecretLevelSelect },
      mixins:[JeecgListMixin],
      props: {
        importUrl: {
          type: String,
          default: '',
          required: true
        },
        templateFilename: {
          type: String,
          default: '',
          required: false
        },
        importModel: {
          type: Boolean,
          default: false,
          required: false
        }
      },
      data () {
        return {
          disableMixinCreated: true,
          title:"Excel导入",
          visible: false,
          bodyStyle:{
            padding: "0",
          },
          confirmLoading: false,
          secretLevel: 10, //导入密级，默认10
          importModelVal: 1, //导入模式；1-追加；2-更新
          fileName: '空',
          fileSize: 0,
          importSuccess: -1,
          fileErrorMsg: "", //文件上传错误信息
          errorFileName: "", //错误文件名称
          errorNum: 0,  //导入数据错误数量
          successNum: 0,  //导入数据成功数量
          otherMsg: '其他信息'
        }
      },
      created () {
      },
      computed:{
        importExcelUrl: function(){
          return `${window._CONFIG['domianURL']}/${this.importUrl}?importModel=${this.importModelVal}&secretLevel=${this.secretLevel}`;
        },
        errorFilePath: function(){
          return `${window._CONFIG['domianURL']}/sys/common/getImpLog?fileName=${this.errorFileName}`;
        },
        templateFilePath: function(){
          return `${window._CONFIG['domianURL']}/sys/common/getTemplateFile?fileName=${this.templateFilename}`;
        }
      },
      methods: {
        open(){
          this.visible = true;
        },
        close() {
          this.visible = false;
        },
        handleCancel() {
          this.visible = false;
        },

        beforeUpload: function(file, fileList){
          if(this.secretLevel === ''){
            this.$message.warning('请选择密级');
            fileList.clear();
            return false;
          }
          var index = file.name.lastIndexOf('.');
          var fileType = file.name.substr(index + 1);
          if(fileType.indexOf('xls') < 0 && fileType.indexOf('xlsx') < 0){
            this.$message.warning('请上传Excel文件');
            fileList.clear();
            return false;
          }
        },
        /* 导入 */
        handleImport(info){
          this.fileName = info.file.name;
          this.fileSize = (info.file.size / 1024).toFixed(2);

          this.confirmLoading = true;
          var that = this;
          var resp = info.file.response;
          if (info.file.status !== 'uploading') {
            // console.log(info.file, info.fileList);
          }
          if (info.file.status === 'done') {
            this.confirmLoading = false;
            if (resp.success) {
              that.importSuccess = 0;
              that.errorNum = resp.result.errorNum;
              that.successNum = resp.result.successNum;
              that.errorFileName = resp.result.errorFileName;
              that.otherMsg = resp.result.otherMsg;
              this.$emit('importCallback');
            } else {
              that.importSuccess = 1;
              that.fileErrorMsg = resp.message;
            }
          } else if (info.file.status === 'error') {
            this.confirmLoading = false;
            if (info.file.response.status === 500) {
              let data = info.file.response
              const token = Vue.ls.get(ACCESS_TOKEN)
              if (token && data.message.includes("Token失效")) {
                this.$error({
                  title: '登录已过期',
                  content: '很抱歉，登录已过期，请重新登录',
                  okText: '重新登录',
                  mask: false,
                  onOk: () => {
                    store.dispatch('Logout').then(() => {
                      Vue.ls.remove(ACCESS_TOKEN)
                      window.location.reload();
                    })
                  }
                })
              }
            } else {
              that.importSuccess = 1;
              that.fileErrorMsg = `文件上传失败: ${info.file.msg} `;
            }
          }
        },

        clear(){
            this.secretLevel=10 //导入密级，默认10
            this.importModelVal=1 //导入模式；1-追加；2-更新
            this.fileName='空'
            this.fileSize=0
            this.importSuccess=-1
            this.fileErrorMsg="" //文件上传错误信息
            this.errorFileName="" //错误文件名称
            this.errorNum=0  //导入数据错误数量
            this.successNum=0  //导入数据成功数量
            this.otherMsg='其他信息'
        }


      }


    }
</script>

<style scoped>
  .div_import{
    width: 100%; height: 200px;padding: 5px;
  }
  .div_import_result{
    height:70%;
    margin-top:10px;
    border:1px solid #1890ff;
    border-radius: 5px;
    padding: 2px 5px;
  }
  .imp_number{
    color: darkred;
    font-size: 16px;
    font-weight: 700;
  }
  .left_border_line{
    margin-left: 10px;
    border-left: 1px solid #a1a0a0;
    padding-left: 10px;
  }
</style>