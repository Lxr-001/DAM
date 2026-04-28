<template>
  <div class="income-bill-page">
    <!-- 顶部区域：标题 + 搜索筛选 -->
    <div class="header-section">
      <h1 class="page-title">收入账单</h1>
      <div class="search-bar">
        <a-input-search
          placeholder="请输入合约编号"
          style="width: 320px; margin-right: 16px;"
        />
        <a-input-search
          placeholder="请输入账单编号"
          style="width: 320px; margin-right: 16px;"
        />
        <a-dropdown>
          <a-menu slot="overlay" @click="handleStatusChange">
            <a-menu-item key="all">全部</a-menu-item>
            <a-menu-item key="unpaid">待付款</a-menu-item>
            <a-menu-item key="paid">已付款</a-menu-item>
            <a-menu-item key="closed">已关闭</a-menu-item>
          </a-menu>
          <a-button>
            账单状态 <a-icon type="sync" />
          </a-button>
        </a-dropdown>
      </div>
    </div>

    <!-- 数据表格 -->
    <a-table
      :columns="columns"
      :data-source="dataSource"
      row-key="id"
      :pagination="false"
      :locale="{ emptyText: emptyState }"
    >
      <template slot="operation" slot-scope="text, record">
        <a-button type="link" size="small">查看</a-button>
        <a-button type="link" size="small">导出</a-button>
      </template>
    </a-table>
  </div>
</template>

<script>
export default {
  name: 'IncomeBill',
  data() {
    return {
      columns: [
        { title: '合约编号', dataIndex: 'contractNo', key: 'contractNo' },
        { title: '账单编号', dataIndex: 'billNo', key: 'billNo' },
        { title: '买方', dataIndex: 'buyer', key: 'buyer' },
        { title: '卖方', dataIndex: 'seller', key: 'seller' },
        { title: '成交定价', dataIndex: 'price', key: 'price' },
        { title: '产品用度(次)', dataIndex: 'usage', key: 'usage' },
        { title: '账单金额(元)', dataIndex: 'amount', key: 'amount' },
        { title: '记账周期', dataIndex: 'period', key: 'period' },
        { title: '付款方式', dataIndex: 'payType', key: 'payType' },
        { title: '账单状态', dataIndex: 'status', key: 'status' },
        { title: '操作', key: 'operation', slotName: 'operation' }
      ],
      dataSource: [] // 数据为空时显示空状态
    }
  },
  methods: {
    handleStatusChange({ key }) {
      console.log('筛选状态：', key)
      // 可在此处添加状态筛选逻辑
    },
    // 自定义空状态模板
    emptyState() {
      return (
        <div style="padding: 40px 0; text-align: center;">
          <a-icon type="folder-open" style="font-size: 48px; color: #ccc; margin-bottom: 16px;"/>
          <p style="font-size: 14px; color: #999;">暂无数据</p>
        </div>
      )
    }
  }
}
</script>

<style scoped>
.income-bill-page {
  padding: 24px;
  background-color: #f5f7fa; /* 页面背景色 */
}

.header-section {
  display: flex;
  justify-content: space-between; /* 标题左，搜索栏右 */
  align-items: flex-end; /* 底部对齐 */
  margin-bottom: 24px;
}

.page-title {
  margin: 0 0 20px 0;
  font-size: 20px;
  font-weight: 600;
  color: #000000;
  flex-shrink: 0;
}

.search-bar {
  display: flex;
  align-items: center;
}

/* 表格样式 */
/deep/ .ant-table-thead > tr > th {
  background-color: #e6f2ff; /* 表头浅蓝色背景 */
  color: #333;
  font-weight: 500;
}

/deep/ .ant-table-tbody > tr > td {
  padding: 12px 8px;
}
</style>