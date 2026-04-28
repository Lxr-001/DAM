<template>
  <div class="main-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-bg"></div>
      <h1 class="title">交易指南</h1>
    </div>

    <div class="content-wrapper">
      <!-- 左侧侧边栏 -->
      <div class="sidebar">
        <a-menu
          mode="inline"
          :default-selected-keys="['tradeGuide']"
          @select="handleMenuSelect"
        >
          <a-menu-item key="tradeGuide" @click="currentPage = 'TradeGuide'">
            交易指引
          </a-menu-item>
          <a-menu-item key="complianceGuide" @click="currentPage = 'ComplianceGuide'">
            合规指引
          </a-menu-item>
          <a-menu-item key="tradeProcess" @click="currentPage = 'TradeProcess'">
            交易流程
          </a-menu-item>
          <a-menu-item key="operationGuide" @click="currentPage = 'OperationGuide'">
            操作指南
          </a-menu-item>
          <a-menu-item key="tradeSystem" @click="currentPage = 'TradeSystem'">
            交易制度
          </a-menu-item>
        </a-menu>
      </div>

      <!-- 右侧内容区 -->
      <div class="content">
        <TradeGuide v-if="currentPage === 'TradeGuide'" />
        <ComplianceGuide v-else-if="currentPage === 'ComplianceGuide'" />
        <TradeProcess v-else-if="currentPage === 'TradeProcess'" />
        <OperationGuide v-else-if="currentPage === 'OperationGuide'" />
        <TradeSystem v-else-if="currentPage === 'TradeSystem'" />
      </div>

    </div>
  </div>
</template>

<script>
import TradeGuide from '@views/DAM/Guide/tabs/TradeGuide.vue'
import ComplianceGuide from '@views/DAM/Guide/tabs/ComplianceGuide.vue'
import TradeProcess from '@views/DAM/Guide/tabs/TradeProcess.vue'
import OperationGuide from '@views/DAM/Guide/tabs/OperationGuide.vue'
// import TradeSystem from './TradeSystem.vue'

export default {
  name: 'MainPage',
  components: {
    TradeGuide,
    ComplianceGuide,
    TradeProcess,
    OperationGuide,
    // TradeSystem
  },
  data() {
    return {
      currentPage: 'TradeGuide'
    }
  },
  methods: {
    handleMenuSelect({ key }) {
      this.currentPage = key.replace('Guide', '').replace('Process', '').replace('System', '')
    }
  }
}
</script>

<style scoped>
.main-container {
  min-height: 100vh;
  background: #fff;
}
.header {
  position: relative;
  height: 80px;
  background: linear-gradient(90deg, #0052d9 0%, #003a8c 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwMCIgaGVpZ2h0PSI4MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZGVmcz48cGF0dGVybiBpZD0iZ3JpZCIgd2lkdGg9IjIwIiBoZWlnaHQ9IjIwIiBwYXR0ZXJuVW5pdHM9InVzZXJTcGFjZU9uVXNlIj48cGF0aCBkPSJNMjAgMEgwVjIwSDIwVjBaIiBmaWxsPSJub25lIiBzdHJva2U9IiMxMDQwODAiIHN0cm9rZS13aWR0aD0iMSIvPjwvcGF0dGVybj48L2RlZnM+PHJlY3Qgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSIgZmlsbD0idXJsKCNncmlkKSIvPjwvc3ZnPg==');
  opacity: 0.3;
}
.title {
  color: #fff;
  font-size: 32px;
  font-weight: 600;
  margin: 0;
  z-index: 1;
}
.content-wrapper {
  display: flex;
  padding: 20px;
}
.sidebar {
  width: 200px;
  margin-right: 20px;
}
.content {
  flex: 1;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.right-toolbar {
  width: 80px;
  margin-left: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.toolbar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  font-size: 12px;
  color: #666;
}
.toolbar-item .anticon {
  font-size: 20px;
  margin-bottom: 5px;
  color: #0052d9;
}
</style>