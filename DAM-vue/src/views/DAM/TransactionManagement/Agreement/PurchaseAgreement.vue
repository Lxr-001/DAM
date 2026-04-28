<template>
  <div class="sales-contract-page">
    <!-- 页面标题 -->
    <h2 class="page-title">购买合约</h2>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <!-- 合约编号搜索框 -->
      <a-input-search
        placeholder="请输入合约编号"
        class="filter-item search-item"
      />

      <!-- 产品名称搜索框 -->
      <a-input-search
        placeholder="请输入产品名称"
        class="filter-item search-item"
      />

      <!-- 签约时间筛选（下拉日期面板） -->
      <a-dropdown
        :trigger="['click']"
        class="filter-item dropdown-item"
        placement="bottomLeft"
      >
        <a-button class="filter-btn">
          签约时间 <a-icon type="down" />
        </a-button>
        <div slot="overlay" class="date-overlay">
          <div class="date-picker-panel">
            <div class="date-header">
              <a-icon type="double-left" />
              <a-icon type="left" />
              <span>2026年3月</span>
              <a-icon type="right" />
              <a-icon type="double-right" />
            </div>
            <div class="date-week">
              <span>一</span><span>二</span><span>三</span><span>四</span><span>五</span><span>六</span><span>日</span>
            </div>
            <div class="date-days">
              <span class="prev-month">23</span><span class="prev-month">24</span><span class="prev-month">25</span><span class="prev-month">26</span><span class="prev-month">27</span><span class="prev-month">28</span><span>1</span>
              <span>2</span><span>3</span><span>4</span><span class="current-day">5</span><span>6</span><span>7</span><span>8</span>
              <span>9</span><span>10</span><span>11</span><span>12</span><span>13</span><span>14</span><span>15</span>
              <span>16</span><span>17</span><span>18</span><span>19</span><span>20</span><span>21</span><span>22</span>
              <span>23</span><span>24</span><span>25</span><span>26</span><span>27</span><span>28</span><span>29</span>
              <span>30</span><span>31</span><span class="next-month">1</span><span class="next-month">2</span><span class="next-month">3</span><span class="next-month">4</span><span class="next-month">5</span>
            </div>
            <div class="date-today">今天</div>
          </div>
        </div>
      </a-dropdown>

      <!-- 合约状态筛选 -->
      <a-dropdown
        :trigger="['click']"
        class="filter-item dropdown-item"
        placement="bottomLeft"
      >
        <a-button class="filter-btn">
          合约状态 <a-icon type="down" />
        </a-button>
        <a-menu slot="overlay" class="status-menu">
          <a-menu-item>待签约</a-menu-item>
          <a-menu-item>审核中</a-menu-item>
          <a-menu-item>审核驳回</a-menu-item>
          <a-menu-item>未交付</a-menu-item>
          <a-menu-item class="active-item">已完成</a-menu-item>
          <a-menu-item>待审核退款</a-menu-item>
        </a-menu>
      </a-dropdown>

      <!-- 服务方式筛选 -->
      <a-dropdown
        :trigger="['click']"
        class="filter-item dropdown-item"
        placement="bottomLeft"
      >
        <a-button class="filter-btn">
          服务方式 <a-icon type="down" />
        </a-button>
        <a-menu slot="overlay" class="service-menu">
          <a-menu-item>标准服务</a-menu-item>
          <a-menu-item>资源包</a-menu-item>
          <a-menu-item>面议</a-menu-item>
        </a-menu>
      </a-dropdown>
    </div>

    <!-- 合约表格（占满剩余高度） -->
    <a-table
      :columns="columns"
      :data-source="[]"
      bordered
      class="contract-table"
      :pagination="false"
      :scroll="{ y: 'calc(100vh - 220px)' }"
    >
      <!-- 空数据插槽 -->
      <template slot="emptyImage">
        <a-icon type="folder-open" style="font-size: 24px; color: #e8e8e8;" />
      </template>
      <template slot="emptyText">
        <span style="color: #999;">暂无数据</span>
      </template>
    </a-table>
  </div>
</template>

<script>
export default {
  name: "SalesContract",
  data() {
    return {
      columns: [
        { title: "合约编号", dataIndex: "contractNo", key: "contractNo" },
        { title: "产品名称", dataIndex: "productName", key: "productName" },
        { title: "买方", dataIndex: "buyer", key: "buyer" },
        { title: "卖方", dataIndex: "seller", key: "seller" },
        { title: "签约时间", dataIndex: "signTime", key: "signTime" },
        { title: "服务方式", dataIndex: "serviceMode", key: "serviceMode" },
        { title: "成交定价", dataIndex: "transactionPrice", key: "transactionPrice" },
        { title: "产品用度(次)", dataIndex: "productUsage", key: "productUsage" },
        { title: "合约总额(元)", dataIndex: "totalAmount", key: "totalAmount" },
        { title: "合约状态", dataIndex: "contractStatus", key: "contractStatus" },
        { title: "操作", dataIndex: "operation", key: "operation" }
      ]
    };
  }
};
</script>

<style scoped>
/* 页面基础样式 - 去除默认边距，让内容占满视口 */
.sales-contract-page {
  padding: 20px;
  background: #ffffff;
  height: 100vh; /* 页面占满视口高度 */
  display: flex;
  flex-direction: column;
  margin: 0;
}

.page-title {
  margin: 0 0 20px 0;
  font-size: 20px;
  font-weight: 600;
  color: #000000;
  flex-shrink: 0;
}

/* 筛选栏样式 */
.filter-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
  flex-shrink: 0; /* 筛选栏固定高度，不被压缩 */
}

.filter-item {
  flex-shrink: 0;
}

.search-item {
  width: 180px;
}

.filter-btn {
  background: #ffffff;
  border: 1px solid #d9d9d9;
  color: #000000;
  padding: 0 16px;
  height: 32px;
  border-radius: 2px;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 日期面板弹出层样式 */
.date-overlay {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 2px;
  padding: 12px;
}

.date-picker-panel {
  text-align: center;
  width: 280px;
}

.date-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.date-week {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 12px;
  color: #666666;
}

.date-days {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: space-between;
}

.date-days span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.date-days .prev-month,
.date-days .next-month {
  color: #cccccc;
}

.date-days .current-day {
  background: #1890ff;
  color: #ffffff;
}

.date-today {
  margin-top: 12px;
  font-size: 12px;
  color: #1890ff;
  cursor: pointer;
  text-align: center;
}

/* 合约状态/服务方式下拉菜单样式 */
.status-menu,
.service-menu {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 2px;
  min-width: 120px;
}

.status-menu .active-item {
  background: #e6f7ff;
  color: #1890ff;
}

/* 表格样式 - 占满剩余高度 */
.contract-table {
  border-radius: 2px;
  flex: 1; /* 表格占满剩余高度 */
  min-height: 0; /* 解决flex子元素高度溢出问题 */
}

.contract-table >>> .ant-table-thead > tr > th {
  background: #ffffff;
  color: #000000;
  font-weight: 500;
}

.contract-table >>> .ant-table-tbody > tr > td {
  color: #333333;
}

.contract-table >>> .ant-table-body {
  max-height: calc(100vh - 220px); /* 动态计算表格高度，适配视口 */
  overflow-y: auto;
}
</style>