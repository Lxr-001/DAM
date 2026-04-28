<template>
  <j-modal
    title="pdf在线查看"
    :visible="visible"
    :fullscreen="true"
    @cancel="handleCancel"
    cancelText="关闭"
    :footer="null">

    <iframe :src="pdfUrl" style="width: 100%;height:100%;"></iframe>


  </j-modal>

</template>

<script>

    export default {
      name: 'CustomPdfView',
      data () {
        return {
          visible: false,
          pdfUrl: ''
        }
      },
      created() {

      },
      mounted() {

      },
      methods: {
        viewPdf(pdfUrl){
          this.pdfUrl = `/pdfjs-2.16.105/web/viewer.html?file=${encodeURIComponent(pdfUrl)}`;
          this.visible = true;
        },
        handleCancel(){
          this.visible = false;
        }

      }
    }
</script>

<style scoped>

</style>