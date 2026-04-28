<template>
  <div>
    <p>首页</p>
    <a-button
      type="primary"
      icon="download"
      size="small"
      @click="test()">
      跳转
    </a-button>

  </div>


</template>


<script setup>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { mixinDevice } from '@/utils/mixin'
import { getFileAccessHttpUrl } from '@api/manage'

export default {
  name: 'Home',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
  },
  data() {
    return {
      description: '文件管理管理页面',
      // 表头
      columns: [
        {
          title: '#',
          dataIndex: '',
          key: 'rowIndex',
          width: 60,
          align: 'center',
          customRender: function(t, r, index) {
            return parseInt(index) + 1
          }
        },
        {
          title: '文件名',
          align: 'center',
          dataIndex: 'fileName'
        },
        {
          title: '密级',
          align: 'center',
          dataIndex: 'security'
        },
        {
          title: '标签',
          align: 'center',
          dataIndex: 'description'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 147,
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: '/akFile/akFile/list',
        delete: '/akFile/akFile/delete',
        deleteBatch: '/akFile/akFile/deleteBatch',
        exportXlsUrl: '/akFile/akFile/exportXls',
        importExcelUrl: 'akFile/akFile/importExcel',
        importDataUrl: 'datapackage/import'
      },
    }
  },
  computed: {
    importExcelUrl: function() {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    },
    importDataUrl: function() {
      return `${window._CONFIG['domianURL']}/${this.url.importDataUrl}`
    }
  },
  methods: {
    test() {
      this.$router.push({path: '/dashboard/analysis'})
    },
  }
}
</script>

<style scoped>

</style>