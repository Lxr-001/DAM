<template>
  <div class="contract-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>登记凭证合约</h2>
    </div>

    <!-- 搜索筛选栏 -->
    <div class="search-bar">
      <!-- 合约编号搜索框 -->
      <a-input-search
        placeholder="请输入合约编号"
        class="search-input"
        @search="onSearch"
      />

      <!-- 日期范围选择器 -->
      <a-range-picker
        v-model="dateRange"
        :placeholder="['开始日期', '结束日期']"
        class="date-range-picker"
        @change="onDateChange"
      />

      <!-- 合约状态下拉菜单 -->
      <a-dropdown>
        <a-menu slot="overlay" @click="handleStatusClick">
          <a-menu-item key="待支付">待支付</a-menu-item>
          <a-menu-item key="支付中">支付中</a-menu-item>
          <a-menu-item key="已支付">已支付</a-menu-item>
        </a-menu>
        <a-button class="status-dropdown">
          合约状态 <a-icon type="down" />
        </a-button>
      </a-dropdown>
    </div>

    <!-- 数据表格 -->
    <a-table
      :columns="columns"
      :data-source="tableData"
      row-key="contractId"
      :pagination="false"
      :locale="{ emptyText: '暂无数据' }"
      :row-class-name="getRowClassName"
    >
      <template slot="operation" slot-scope="{ record }">
        <a-button type="link" size="small">查看</a-button>
        <a-button type="link" size="small">编辑</a-button>
      </template>
    </a-table>
  </div>
</template>

<script>
export default {
  name: 'ContractPage',
  data() {
    return {
      // 搜索条件
      contractNo: '',
      dateRange: [],
      contractStatus: '',

      // 表格列配置（完全匹配设计图）
      columns: [
        { title: '合约编号', dataIndex: 'contractNo', key: 'contractNo', width: 160 },
        { title: '数据名称', dataIndex: 'dataName', key: 'dataName', width: 160 },
        { title: '凭证编号', dataIndex: 'voucherNo', key: 'voucherNo', width: 160 },
        { title: '业务类型', dataIndex: 'businessType', key: 'businessType', width: 160 },
        { title: '本次支付费用/元', dataIndex: 'currentPay', key: 'currentPay', width: 200 },
        {
          title: '实际支付费用/元',
          dataIndex: 'actualPay',
          key: 'actualPay',
          width: 200,
          render: (text) => text || '--',
        },
        { title: '合约状态', dataIndex: 'status', key: 'status', width: 160 },
        { title: '支付完成时间', dataIndex: 'payTime', key: 'payTime', width: 200 },
        { title: '是否开票', dataIndex: 'isInvoiced', key: 'isInvoiced', width: 120 },
        {
          title: '操作',
          key: 'operation',
          slotName: 'operation',
          width: 120,
        },
      ],

      // 设计图中的测试数据
      tableData: [
        {
          contractId: 1,
          contractNo: 'CON20260305001',
          dataName: '企业流水数据',
          voucherNo: 'VOU20260305001',
          businessType: '数据采购',
          currentPay: 5000,
          actualPay: '--',
          status: '待支付',
          payTime: '--',
          isInvoiced: '否'
        },
        {
          contractId: 2,
          contractNo: 'CON20260305002',
          dataName: '用户画像数据',
          voucherNo: 'VOU20260305002',
          businessType: '数据采购',
          currentPay: 8000,
          actualPay: '--',
          status: '支付中',
          payTime: '--',
          isInvoiced: '否'
        },
        {
          contractId: 3,
          contractNo: 'CON20260305003',
          dataName: '交易明细数据',
          voucherNo: 'VOU20260305003',
          businessType: '数据采购',
          currentPay: 12000,
          actualPay: 12000,
          status: '已支付',
          payTime: '2026-03-04 15:30:22',
          isInvoiced: '是'
        }
      ],
    }
  },
  methods: {
    // 合约编号搜索
    onSearch(value) {
      this.contractNo = value
      this.fetchTableData()
    },

    // 日期范围变化
    onDateChange(dates) {
      this.dateRange = dates
      this.fetchTableData()
    },

    // 合约状态筛选
    handleStatusClick({ key }) {
      this.contractStatus = key
      this.fetchTableData()
    },

    // 模拟获取数据
    fetchTableData() {
      console.log('查询参数：', {
        contractNo: this.contractNo,
        dateRange: this.dateRange,
        contractStatus: this.contractStatus,
      })
    },

    // 核心：根据合约状态设置行背景色（匹配设计图）
    getRowClassName(record) {
      if (record.status === '支付中') {
        return 'row-paying' // 浅蓝色背景
      }
      return '' // 其他状态默认白色
    }
  },
}
</script>

<style scoped>
.contract-page {
  padding: 24px;
  background-color: #ffffff;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0;
  font-size: 56px;
  font-weight: 700;
  color: #000000;
  line-height: 1.1;
}

.search-bar {
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 24px; /* 搜索框之间的间距 */
}

/* 搜索框样式 */
.search-input {
  width: 300px;
}

/* 日期选择器样式 */
.date-range-picker {
  width: 320px;
}

/* 合约状态下拉按钮样式 */
.status-dropdown {
  width: 180px;
}

/* 表格样式重置与美化 */
.ant-table {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.ant-table-thead > tr > th {
  background-color: #fafafa;
  border-bottom: 1px solid #e8e8e8;
  font-weight: 500;
  color: #000000;
  padding: 16px;
}

.ant-table-tbody > tr > td {
  padding: 16px;
  border-bottom: 1px solid #e8e8e8;
}

/* 支付中行的浅蓝色背景 */
.row-paying {
  background-color: #f0faff !important;
}
</style>