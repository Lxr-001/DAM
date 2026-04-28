<template>
  <div class="contract-management-wrapper">
    <div class="contract-management">
      <!-- 顶部操作栏 -->
      <div class="top-bar">
        <div class="left-actions">
          <a-button type="primary" class="action-btn">新建</a-button>
          <a-button type="primary" class="action-btn">导入</a-button>
          <a-button type="primary" class="action-btn">导出</a-button>
        </div>
        <div class="right-search">
          <a-input-search
            placeholder="请输入产品名称"
            style="width: 280px; margin-right: 16px"
          />
          <a-date-picker
            placeholder="开始日期"
            style="width: 200px; margin-right: 16px"
          />
          <a-date-picker
            placeholder="结束日期"
            style="width: 200px; margin-right: 16px"
          />
          <a-select placeholder="备案状态" style="width: 160px">
            <a-select-option value="pending_material">待补充材料</a-select-option>
            <a-select-option value="pending_audit">待审核</a-select-option>
            <a-select-option value="approved">已通过</a-select-option>
            <a-select-option value="rejected">已驳回</a-select-option>
          </a-select>
        </div>
      </div>

      <!-- 数据表格 -->
      <a-table
        :columns="columns"
        :data-source="dataSource"
        row-key="id"
        :pagination="false"
        :locale="{ emptyText: '暂无数据' }"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: 'ContractManagement',
  data() {
    return {
      columns: [
        { title: '备案登记号', dataIndex: 'recordNo', key: 'recordNo' },
        { title: '合同起始日期', dataIndex: 'startDate', key: 'startDate' },
        { title: '合同结束日期', dataIndex: 'endDate', key: 'endDate' },
        { title: '产品名称', dataIndex: 'productName', key: 'productName' },
        { title: '产品用途', dataIndex: 'productUsage', key: 'productUsage' },
        { title: '定价模式', dataIndex: 'pricingMode', key: 'pricingMode' },
        { title: '产品类型', dataIndex: 'productType', key: 'productType' },
        { title: '产品提供商名称', dataIndex: 'providerName', key: 'providerName' },
        { title: '产品应用商名称', dataIndex: 'applierName', key: 'applierName' },
        { title: '调用量/笔', dataIndex: 'callCount', key: 'callCount' },
        { title: '数据规模/MB', dataIndex: 'dataSize', key: 'dataSize' },
        { title: '备案类型', dataIndex: 'recordType', key: 'recordType' },
        { title: '备案金额/元', dataIndex: 'recordAmount', key: 'recordAmount' },
        { title: '状态', dataIndex: 'status', key: 'status' },
        { title: '操作', key: 'action', width: 120 }
      ],
      dataSource: [] // 空数据，显示"暂无数据"
    }
  }
}
</script>

<style scoped>
.contract-management-wrapper {
  background-color: #f0f2f5; /* 页面背景色 */
  padding: 24px;
  min-height: 100vh;
}
.contract-management {
  background-color: #ffffff; /* 内容区域白色背景 */
  padding: 24px;
  border-radius: 4px;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.left-actions .action-btn {
  margin-right: 12px;
  background-color: #1890ff;
  border-color: #1890ff;
  color: #fff;
  font-size: 16px;
  padding: 8px 24px;
  height: auto;
}

.right-search {
  display: flex;
  align-items: center;
}

/* 表格表头样式 */
/deep/ .ant-table-thead > tr > th {
  background-color: #e6f2ff;
  color: #333333;
  font-weight: 500;
}

/* 空状态样式 */
/deep/ .ant-table-placeholder {
  padding: 60px 0;
}
</style>