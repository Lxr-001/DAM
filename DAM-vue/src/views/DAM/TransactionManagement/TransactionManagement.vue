<template>
  <div class="transaction-management-page">
    <!-- 顶部统计卡片区域 -->
    <a-row :gutter="20" class="stats-row">
      <a-col :span="6">
        <a-card class="stat-card" hoverable>
          <div class="stat-content">
            <div class="stat-text">
              <div class="stat-label">销售合约总数</div>
              <div class="stat-value">0</div>
            </div>
            <div class="stat-icon-wrapper">
              <!-- 统一为 fluency-systems-filled 风格 -->
              <img
                src="https://img.icons8.com/fluency-systems-filled/60/0066CC/briefcase.png"
                alt="销售合约总数"
                class="stat-icon"
              />
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card" hoverable>
          <div class="stat-content">
            <div class="stat-text">
              <div class="stat-label">销售合约已完成</div>
              <div class="stat-value">0</div>
            </div>
            <div class="stat-icon-wrapper">
              <img
                src="https://img.icons8.com/fluency-systems-filled/60/00B42A/checked-2.png"
                alt="销售合约已完成"
                class="stat-icon"
              />
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card" hoverable>
          <div class="stat-content">
            <div class="stat-text">
              <div class="stat-label">购买合约总数</div>
              <div class="stat-value">0</div>
            </div>
            <div class="stat-icon-wrapper">
              <img
                src="https://img.icons8.com/fluency-systems-filled/60/FF7D00/shopping-bag.png"
                alt="购买合约总数"
                class="stat-icon"
              />
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card" hoverable>
          <div class="stat-content">
            <div class="stat-text">
              <div class="stat-label">购买合约已完成</div>
              <div class="stat-value">0</div>
            </div>
            <div class="stat-icon-wrapper">
              <img
                src="https://img.icons8.com/fluency-systems-filled/60/86909C/ok.png"
                alt="购买合约已完成"
                class="stat-icon"
              />
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 主内容区域 -->
    <a-row :gutter="20" class="main-content-row">
      <a-col :span="18">
        <!-- 交易主板 -->
        <a-card
          class="main-board-card"
          title="交易主板"
          :bordered="false"
          titleStyle="font-size: 18px; font-weight: 600; color: #1f2937;"
        >
          <a-row :gutter="20">
            <!-- 合约管理 -->
            <a-col :span="6">
              <a-card
                class="module-card"
                hoverable
                @click="goToPage('contract')"
                :bordered="false"
              >
                <div class="module-title" @click="goToPage('contract')">合约管理</div>
                <div class="module-desc">点击此处进入合约管理界面，查看您所有的合约信息。</div>
                <div class="module-icon">
                  <img
                    src="https://img.icons8.com/fluency/100/0066CC/contract.png"
                    alt="合约管理"
                    class="module-img"
                  />
                </div>
              </a-card>
            </a-col>
            <!-- 账单管理 -->
            <a-col :span="6">
              <a-card
                class="module-card"
                hoverable
                @click="goToPage('bill')"
                :bordered="false"
              >
                <div class="module-title" @click="goToPage('bill')">账单管理</div>
                <div class="module-desc">点击此处进入账单管理界面，查看购买产品的账单信息等相关内容。</div>
                <div class="module-icon">
                  <img
                    src="https://img.icons8.com/fluency/100/0066CC/bill.png"
                    alt="账单管理"
                    class="module-img"
                  />
                </div>
              </a-card>
            </a-col>
            <!-- 合同管理 -->
            <a-col :span="6">
              <a-card
                class="module-card"
                hoverable
                @click="goToPage('record')"
                :bordered="false"
              >
                <div class="module-title" @click="goToPage('record')">合同管理</div>
                <div class="module-desc">点击此处进入交易备案登记页面，支持将您平台外交易的数据录入平台。</div>
                <div class="module-icon">
                  <!-- 统一为 fluency 风格，与后两个模块一致 -->
                  <img
                    src="https://img.icons8.com/fluency/100/0066CC/file.png"
                    alt="合同管理"
                    class="module-img"
                  />
                </div>
              </a-card>
            </a-col>
            <!-- 交易市场 -->
            <a-col :span="6">
              <a-card
                class="module-card"
                hoverable
                @click="goToPage('market')"
                :bordered="false"
              >
                <div class="module-title" @click="goToPage('market')">交易市场</div>
                <div class="module-desc">点击此处访问交易市场，查看全部产品信息，挑选符合需求的产品。</div>
                <div class="module-icon">
                  <!-- 统一为 fluency 风格，与后两个模块一致 -->
                  <img
                    src="https://img.icons8.com/fluency/100/0066CC/shopping-cart.png"
                    alt="交易市场"
                    class="module-img"
                  />
                </div>
              </a-card>
            </a-col>

          </a-row>
        </a-card>
      </a-col>

      <!-- 右侧侧边栏 -->
      <a-col :span="6">
        <a-card class="sidebar-card user-card" :bordered="false">
          <div class="user-info">
            <div class="user-avatar">
              <a-avatar size={64} icon="user" :style="{ backgroundColor: '#0066CC' }" />
            </div>
            <div class="user-name">LxR</div>
          </div>
          <a-divider />
          <div class="user-detail">
            <div class="detail-item">
              <span class="detail-label">所属账户类型</span>
              <span class="detail-value">个人</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">个人认证</span>
              <span class="detail-value">
                <a-tag color="#0066CC" class="auth-tag">未认证 &gt;</a-tag>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">最后登录时间</span>
              <span class="detail-value">2026-03-05 11:05:07</span>
            </div>
          </div>
        </a-card>

        <a-card
          class="sidebar-card notice-card"
          title="系统公告"
          :bordered="false"
          titleStyle="font-size: 16px; font-weight: 600; color: #1f2937;"
        >
          <div class="notice-empty">
            <img
              src="https://img.icons8.com/fluency/80/CCCCCC/notification.png"
              alt="暂无公告"
              class="notice-img"
            />
            <div class="notice-empty-text">暂无公告</div>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
export default {
  name: 'TransactionManagement',
  methods: {
    // 模块跳转方法
    goToPage(module) {
      // 替换为你实际的路由地址
      const routes = {
        record: '/transaction-record',
        market: '/transaction-market',
        contract: '/contract-management',
        bill: '/bill-management'
      }
      // 路由存在则跳转，否则提示
      if (routes[module] && this.$router) {
        this.$router.push(routes[module])
      } else {
        this.$message.info(`即将跳转到【${module === 'record' ? '交易登记备案' : module === 'market' ? '交易市场' : module === 'contract' ? '合约管理' : '账单管理'}】页面`)
      }
    }
  }
}
</script>

<style scoped>
/* 全局页面样式 */
.transaction-management-page {
  padding: 20px;
  background-color: #F5F7FA;
  min-height: calc(100vh - 64px);
}

/* 顶部统计行 */
.stats-row {
  margin-bottom: 20px;
}

/* 统计卡片样式 */
.stat-card {
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  background: #FFFFFF;
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.stat-text .stat-label {
  font-size: 14px;
  color: #6B7280;
  margin-bottom: 6px;
}

.stat-text .stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1F2937;
}

/* 统计卡片图标容器 */
.stat-icon-wrapper {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #F0F7FF;
}

.stat-icon {
  transition: all 0.2s ease;
  width: 40px;
  height: 40px;
  object-fit: contain;
}

.stat-card:hover .stat-icon {
  transform: scale(1.1);
}

/* 主内容区域 */
.main-content-row {
  .main-board-card {
    margin-bottom: 20px;
    background: #FFFFFF;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    min-height: 520px;
    display: flex;
    flex-direction: column;
  }

  .main-board-card >>> .ant-card-body {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  /* 交易主板模块卡片 */
  .module-card {
    background: #F9FAFB;
    border-radius: 12px;
    padding: 20px;
    height: 100%;
    transition: all 0.3s ease;
    min-height: 380px;
    display: flex;
    flex-direction: column;
  }

  .module-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 16px rgba(0, 102, 204, 0.1);
  }

  .module-title {
    font-size: 18px;
    font-weight: 600;
    color: #1F2937;
    margin-bottom: 12px;
    cursor: pointer;
  }

  .module-title:hover {
    color: #0066CC;
  }

  .module-desc {
    font-size: 14px;
    color: #6B7280;
    line-height: 1.6;
    margin-bottom: 20px;
    flex: 1;
  }

  .module-icon {
    text-align: center;
  }

  /* 模块图片样式统一化 */
  .module-img {
    width: 80px;
    height: 80px;
    object-fit: contain;
    transition: all 0.3s ease;
  }

  .module-card:hover .module-img {
    transform: scale(1.05);
  }
}

/* 右侧侧边栏 */
.sidebar-card {
  background: #FFFFFF;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  padding: 20px;
}

/* 用户信息卡片 */
.user-card {
  min-height: 320px;
}

.user-card .user-info {
  text-align: center;
  margin-bottom: 16px;
}

.user-card .user-avatar {
  margin-bottom: 12px;
}

.user-card .user-name {
  font-size: 20px;
  font-weight: 600;
  color: #1F2937;
}

.user-card .user-detail {
  padding-top: 8px;
}

.user-card .detail-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.user-card .detail-label {
  font-size: 14px;
  color: #6B7280;
}

.user-card .detail-value {
  font-size: 14px;
  color: #1F2937;
}

.auth-tag {
  border-radius: 4px;
  background: #0066CC !important;
  color: #FFFFFF !important;
}

/* 公告卡片 */
.notice-card {
  padding: 20px;
  min-height: 180px;
}

.notice-empty {
  text-align: center;
  padding: 30px 0;
}

.notice-img {
  margin-bottom: 12px;
  opacity: 0.6;
  width: 60px;
  height: 60px;
  object-fit: contain;
}

.notice-empty-text {
  font-size: 14px;
  color: #9CA3AF;
}
</style>