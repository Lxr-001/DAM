// 想要实现数据共享的页面就引用本 mixin 就可以
import { postAction } from '@api/manage'

export const ListDataShareMixin = {
  data() {
    return {
      shareModalVisible: false,
      currentShareDataId: '',
      maxDataSecurity: 0//选中数据的最大密级 （下层页面选择的被共享人  和 审批人 密级都要大于等于此密级）
    }
  },
  methods: {
    // 处理数据共享
    handleShare() {
      // 1. 检查是否有选中项
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请先选择要共享的数据')
        return
      }

      // 2. 核心改动：校验选中项的创建人是否都为当前用户
      const currentUserName = this.userInfo().realname // 获取当前登录用户姓名
      let hasInvalidData = false

      for (const id of this.selectedRowKeys) {
        const selectedItem = this.dataSource.find((row) => row.id === id)
        // 检查是否找到该数据，以及创建人是否与当前用户一致
        if (!selectedItem || selectedItem.createBy !== currentUserName) {
          hasInvalidData = true
          break // 只要发现一个不合法的数据，就立即退出循环
        }
      }

      // 3. 根据校验结果做出不同操作
      if (hasInvalidData) {
        this.$message.error('选中的数据中包含其他用户创建的数据，不允许共享！')
        return // 阻止后续操作
      }

      // 4. 如果校验通过，才继续执行原来的共享逻辑
      const selectedRowIds = this.selectedRowKeys.join(',')
      if (selectedRowIds) {
        var maxSecurity = 0
        for (var i = 0; i < this.selectedRowKeys.length; i++) {
          const item = this.dataSource.find((row) => row.id === this.selectedRowKeys[i])
          if (item.secretLevel > maxSecurity) {
            maxSecurity = item.secretLevel
          }
        }
        this.maxDataSecurity = maxSecurity
        this.currentShareDataId = selectedRowIds
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
    handleRecall(record) {
      this.$confirm({
        title: '确认撤回',
        content: '确定要撤回这条数据吗？',
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.loading = true
          // 调用撤回接口，传递id
          postAction(this.url.recall, record)
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
    }
  }
}