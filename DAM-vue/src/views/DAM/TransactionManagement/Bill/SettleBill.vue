<template>
  <div class="settlement-bill-page">
    <!-- 顶部标题 + 筛选按钮 -->
    <div class="page-header">
      <h2>结算账单</h2>
      <div class="filter-group">
        <!-- 账单日期下拉（支持年份调节） -->
        <a-dropdown trigger="click" placement="bottomRight">
          <a-button class="filter-btn">
            <a-icon type="calendar" />
            <span>账单日期</span>
            <!-- 显示当前选中的年月（可选） -->
            <span v-if="selectedYear && selectedMonth" class="selected-date">
              ({{ selectedYear }}年{{ selectedMonth }}月)
            </span>
          </a-button>
          <template slot="overlay">
            <div class="date-menu">
              <!-- 年份调节头部 -->
              <div class="date-header">
                <a-icon
                  type="left"
                  class="nav-icon"
                  @click.stop="changeYear(-1)"
                />
                <span class="year-text">{{ currentYear }}年</span>
                <a-icon
                  type="right"
                  class="nav-icon"
                  @click.stop="changeYear(1)"
                />
              </div>
              <!-- 月份网格 -->
              <div class="month-grid">
                <div
                  class="month-item"
                  v-for="month in months"
                  :key="month"
                  :class="{ active: month === selectedMonth && currentYear === selectedYear }"
                  @click="handleMonthSelect(month)"
                >
                  {{ month }}月
                </div>
              </div>
            </div>
          </template>
        </a-dropdown>

        <!-- 结算状态下拉 -->
        <a-dropdown trigger="click" placement="bottomRight">
          <a-button class="filter-btn">
            <a-icon type="sync" />
            <span>结算状态</span>
            <span v-if="selectedStatus" class="selected-status">
              ({{ getStatusLabel(selectedStatus) }})
            </span>
          </a-button>
          <template slot="overlay">
            <div class="status-menu">
              <div
                class="status-item"
                v-for="status in statusList"
                :key="status.value"
                :class="{ active: status.value === selectedStatus }"
                @click="handleStatusSelect(status.value)"
              >
                {{ status.label }}
              </div>
            </div>
          </template>
        </a-dropdown>
      </div>
    </div>

    <!-- 数据表格 -->
    <a-table
      :columns="columns"
      :data-source="dataSource"
      row-key="id"
      :pagination="false"
      class="bill-table"
    >
      <template slot="emptyText">
        <div class="empty-state">
          <a-icon type="folder-open" class="empty-icon" />
          <p>暂无数据</p>
        </div>
      </template>
    </a-table>
  </div>
</template>

<script>
export default {
  name: 'SettlementBill',
  data() {
    return {
      // 基础数据
      months: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
      statusList: [
        { label: '待结算', value: 'pending' },
        { label: '部分结算', value: 'partial' },
        { label: '已结算', value: 'settled' }
      ],
      // 年份调节相关
      currentYear: new Date().getFullYear(), // 当前显示的年份（默认今年）
      selectedYear: null, // 选中的年份
      selectedMonth: null, // 选中的月份
      selectedStatus: null, // 选中的结算状态
      // 表格列配置
      columns: [
        { title: '数商名称', dataIndex: 'merchantName', width: 120, align: 'center' },
        { title: '交易总额(元)', dataIndex: 'totalAmount', width: 160, align: 'center' },
        { title: '交易总笔数', dataIndex: 'totalCount', width: 140, align: 'center' },
        { title: '佣金总额(元)', dataIndex: 'commissionAmount', width: 160, align: 'center' },
        { title: '应结算总额(元)', dataIndex: 'settleAmount', width: 180, align: 'center' },
        { title: '结算状态', dataIndex: 'status', width: 120, align: 'center' },
        { title: '实际结算金额(元)', dataIndex: 'actualAmount', width: 200, align: 'center' },
        { title: '账单日期', dataIndex: 'billDate', width: 140, align: 'center' },
        { title: '最新结算时间', dataIndex: 'settleTime', width: 160, align: 'center' },
        { title: '操作', dataIndex: 'action', width: 80, align: 'center' }
      ],
      dataSource: []
    }
  },
  methods: {
    /**
     * 调节年份（上一年/下一年）
     * @param {Number} step - 调节步长（-1：上一年，1：下一年）
     */
    changeYear(step) {
      this.currentYear += step
      // 如果选中的年份和当前显示年份一致，保留选中月份；否则清空选中
      if (this.selectedYear !== this.currentYear) {
        this.selectedMonth = null
      }
    },

    /**
     * 选择月份
     * @param {Number} month - 选中的月份
     */
    handleMonthSelect(month) {
      this.selectedMonth = month
      this.selectedYear = this.currentYear
      console.log('选中账单日期：', `${this.selectedYear}年${this.selectedMonth}月`)
      // 这里可添加筛选表格数据的逻辑
    },

    /**
     * 选择结算状态
     * @param {String} status - 选中的状态值
     */
    handleStatusSelect(status) {
      this.selectedStatus = status
      console.log('选中结算状态：', this.getStatusLabel(status))
      // 这里可添加筛选表格数据的逻辑
    },

    /**
     * 根据状态值获取状态标签
     * @param {String} status - 状态值
     * @returns {String} 状态标签
     */
    getStatusLabel(status) {
      const item = this.statusList.find(item => item.value === status)
      return item ? item.label : ''
    }
  }
}
</script>

<style scoped>
.settlement-bill-page {
  padding: 24px;
  background-color: #fff;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
}

.filter-group {
  display: flex;
  gap: 12px;
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background-color: #fff;
  color: #404040;
  padding: 4px 12px;
  font-size: 14px;
}

.filter-btn:hover {
  color: #1890ff;
  border-color: #1890ff;
}

/* 选中的日期/状态提示文字 */
.selected-date, .selected-status {
  font-size: 12px;
  color: #666;
  margin-left: 4px;
}

/* 账单日期下拉菜单 */
.date-menu {
  width: 280px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.date-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  border-bottom: 1px solid #e8e8e8;
}

.nav-icon {
  cursor: pointer;
  color: #666;
  font-size: 12px;
  transition: color 0.2s;
}

.nav-icon:hover {
  color: #1890ff;
}

.year-text {
  font-size: 14px;
  font-weight: 500;
}

.month-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 4px;
  padding: 8px;
}

.month-item {
  text-align: center;
  padding: 8px 0;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.month-item:hover {
  background-color: #e6f7ff;
}

/* 选中的月份样式 */
.month-item.active {
  background-color: #1890ff;
  color: #fff;
}

/* 结算状态下拉菜单 */
.status-menu {
  min-width: 120px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.status-item {
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.status-item:hover {
  background-color: #e6f7ff;
}

/* 选中的状态样式 */
.status-item.active {
  background-color: #1890ff;
  color: #fff;
}

/* 表格样式 */
.bill-table {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}

.bill-table .ant-table-thead > tr > th {
  background-color: #fafafa;
  color: #212121;
  font-weight: 500;
  text-align: center;
  font-size: 18px;
  padding: 16px 8px;
  border-bottom: 1px solid #e8e8e8;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  color: #999;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  color: #e0e0e0;
}

.empty-state p {
  margin: 0;
  font-size: 18px;
}
</style>