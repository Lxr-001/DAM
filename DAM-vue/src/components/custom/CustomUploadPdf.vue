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
      <div class="selectFileDiv">
        <a-upload-dragger
          name="file"
          :showUploadList="false"
          :fileList="fileList"
          :multiple="true"
          :headers="tokenHeader"
          :action="importExcelUrl"
          :beforeUpload="beforeUpload"
          @change="handleImport"
          accept=".pdf"
        >

          <p class="ant-upload-drag-icon"><a-icon type="inbox" /></p>
          <p class="ant-upload-text">点击此处或拖拽到此区域进行上传</p>

        </a-upload-dragger>
      </div>

      <div class="div_import_result">
        <a-row v-if="fileList.length > 0">
          <a-col :span="12">
            <a-popconfirm title="确定清空吗?" @confirm="() => clearFileList()" style="margin: 10px 0px">
              <a>清空上传列表</a>
            </a-popconfirm>
          </a-col>
          <a-col :span="12">
            <a-tooltip placement="topLeft" title="未匹配：【上传的文件名称与标准附件名称不匹配】">
              <a>结果信息提示</a>
            </a-tooltip>
          </a-col>
        </a-row>

        <a-row style="font-weight: 700;font-size: 15px;">
          <a-col :span="8">文件名称</a-col>
          <a-col :span="4">状态</a-col>
          <a-col :span="12">结果</a-col>
        </a-row>

        <a-row v-for="file in fileList" :key="file.uid">
          <a-col :span="8">{{file.name}}</a-col>
          <a-col :span="4">{{file.status}}</a-col>
          <a-col :span="12">{{fileMsg[file.uid]}}</a-col>
        </a-row>

      </div>
    </div>


  </a-modal>

</template>

<script>
    import { JeecgListMixin } from '@/mixins/JeecgListMixin'
    import Vue from 'vue'

    export default {
      name: 'CustomUploadPdf',
      mixins:[JeecgListMixin],
      props: {
        importUrl: {
          type: String,
          default: '',
          required: true
        }
      },
      data () {
        return {
          disableMixinCreated: true,
          title:"PDF批量导入",
          visible: false,
          bodyStyle:{
            padding: "0",
          },
          confirmLoading: false,
          fileName: '空',
          fileSize: 0,
          fileList:[],
          fileMsg: [],
          isFileHandle: true,
        }
      },
      created () {
      },
      computed:{
        importExcelUrl: function(){
          return `${window._CONFIG['domianURL']}/${this.importUrl}`;
        },
        errorFilePath: function(){
          return `${window._CONFIG['domianURL']}/sys/common/getImpLog?fileName=${this.errorFileName}`;
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
        beforeUpload: function(file){
          var index = file.name.lastIndexOf('.');
          var fileType = file.name.substr(index + 1);
          this.isFileHandle = true;
          if(fileType.indexOf('pdf') < 0){
            this.$message.warning('请上传PDF格式的文件');
            this.isFileHandle = false;
            return false;
          }
          if(this.fileList){
            if(this.fileList.length >= 100){
              this.$message.warning("每次最多上传100个！");
              this.isFileHandle = false;
            }
            const index = this.fileList.findIndex(item => item.name === file.name);
            if(index !== -1){
              this.$message.warning('文件['+file.name+']已在上传列表中！');
              this.isFileHandle = false;
              return false
            }
          }
        },

        handleImport(info){
          if(!this.isFileHandle){
            return;
          }
          this.fileList = [...info.fileList];
          var resp = info.file.response;
          var that = this;

          if(info.file.status === 'done'){
            if (resp.success) {
              that.fileMsg[info.file.uid] = "成功！";
            } else {
              that.fileMsg[info.file.uid] = resp.message;
            }
          }else if(info.file.status === 'error'){
            that.fileMsg[info.file.uid] = "后台错误！";
          }
        },
        clearFileList(){
          this.fileList = [];
        }

      }


    }
</script>

<style scoped>
  .div_import{
    width: 100%; height: 600px;padding: 5px;
  }
  .div_import_result{
    height:75%;
    margin-top:10px;
    border:1px solid #1890ff;
    border-radius: 5px;
    padding: 2px 5px;
    overflow-y: auto;
  }
  .selectFileDiv{
    height: 120px;
  }

</style>