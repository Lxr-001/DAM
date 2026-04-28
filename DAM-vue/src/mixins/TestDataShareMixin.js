// 想要实现数据共享的页面就引用本 mixin 就可以
import { getAction, postAction } from '@api/manage'

export const TestDataShareMixin = {
  data() {
    return {
      shareModalVisible: false,
      currentShareDataId: '',
      maxDataSecurity: 0//选中数据的最大密级 （下层页面选择的被共享人  和 审批人 密级都要大于等于此密级）
    }
  },
  methods: {
    startShare() {
      // this.bid 就是要共享数据的 bid，理论上想要共享数据的 getByBid 的时候应该是一条数据，如果多条就取 createTime 最新的那条
      if (this.bid) {
        // 如果当前表单没有数据，则不进行共享
        if (!this.model.id) {
          this.$message.error('请先保存数据然后再进行数据共享操作')
          return
        }
        this.maxDataSecurity = this.model.security || 0
        this.currentShareDataId = this.model.id
        this.shareModalVisible = true
      }
    },
    // 处理共享弹窗确定
    handleShareModalOk() {
      this.shareModalVisible = false
      this.$message.success('数据共享申请已提交')
      // 可以在这里刷新列表或其他操作
    },
    // 处理共享弹窗取消
    handleShareModalCancel() {
      this.shareModalVisible = false
      this.currentShareDataId = ''
    },
    handleRecall() {
      this.$confirm({
        title: '确认撤回',
        content: '确定要撤回这条数据吗？',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.loading = true
          // 调用撤回接口，传递id
          postAction(this.url.recall, this.bid)
            .then((response) => {
              if (response.success) {
                this.$message.success('数据撤回成功！')
                // 刷新列表
                this.loadData()
              } else {
                this.$message.error(response.message || '数据撤回失败！')
              }
            })
            .catch((error) => {
              console.error('撤回请求出错:', error)
              this.$message.error('网络错误，数据撤回失败！')
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },

  }
}