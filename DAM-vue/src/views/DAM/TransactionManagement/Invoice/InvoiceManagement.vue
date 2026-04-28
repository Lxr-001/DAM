<template>
  <div class="invoice-management">
    <!-- 顶部栏：标题 + 搜索框 -->
    <div class="top-bar">
      <div class="page-title">
        <h2>发票管理</h2>
      </div>
      <div class="search-group">
        <a-input-search
          placeholder="请输入合约编号"
          style="width: 240px; margin-right: 16px;"
        />
        <a-input-search
          placeholder="请输入账单编号"
          style="width: 240px;"
        />
      </div>
    </div>

    <!-- 数据表格 -->
    <a-table
      :columns="columns"
      :data-source="dataSource"
      row-key="id"
      :pagination="false"
      :locale="{ emptyText: '暂无数据' }"
    >
      <template slot="bodyCell" slot-scope="{ column, record }">
        <template v-if="column.key === 'operation'">
          <a-button type="link" size="small">操作</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script>
export default {
  name: 'InvoiceManagement',
  data() {
    return {
      columns: [
        { title: '合约编号', dataIndex: 'contractNo', key: 'contractNo', width: 120 },
        { title: '账单编号', dataIndex: 'billNo', key: 'billNo', width: 120 },
        { title: '买方', dataIndex: 'buyer', key: 'buyer', width: 100 },
        { title: '卖方', dataIndex: 'seller', key: 'seller', width: 100 },
        { title: '成交定价', dataIndex: 'price', key: 'price', width: 120 },
        { title: '产品用度(次)', dataIndex: 'usage', key: 'usage', width: 140 },
        { title: '账单金额(元)', dataIndex: 'amount', key: 'amount', width: 140 },
        { title: '记账周期', dataIndex: 'period', key: 'period', width: 120 },
        { title: '账单状态', dataIndex: 'status', key: 'status', width: 120 },
        { title: '操作', key: 'operation', width: 80 },
      ],
      dataSource: [],
    };
  },
};
</script>

<style scoped>
.invoice-management {
  padding: 60px 40px;
  background-color: #ffffff;
  min-height: 100vh;
}

/* 顶部栏：标题 + 搜索框 水平布局 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
}

.page-title h1 {
  margin: 0;
  font-size: 48px;
  font-weight: 700;
  color: #1a2b49;
}

.search-group {
  display: flex;
  align-items: center;
}

/* 表格样式 */
::v-deep .ant-table-thead > tr > th {
  background-color: #e8f4ff;
  color: #1a2b49;
  font-weight: 500;
  border-bottom: 1px solid #e8e8e8;
}

::v-deep .ant-table-tbody > tr > td {
  border-bottom: 1px solid #e8e8e8;
}

::v-deep .ant-empty-image {
  height: 80px;
}

::v-deep .ant-empty-description {
  color: #909399;
}
</style>