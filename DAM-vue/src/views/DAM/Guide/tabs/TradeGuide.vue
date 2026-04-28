<template>
  <div class="trade-guide-page">
    <h2 class="page-title">交易指引</h2>
    <div class="card-grid">
      <a-card
        v-for="(item, index) in guideList"
        :key="index"
        class="guide-card"
        :body-style="{ padding: '20px' }"
      >
        <div class="card-bg"></div>
        <p class="card-text">{{ item.title }}</p>
      </a-card>
    </div>
    <a-pagination
      class="pagination"
      :total="28"
      :page-size="9"
      default-current="1"
      show-size-changer
      :page-size-options="['9', '15', '20']"
    />
  </div>
</template>

<script>
export default {
  name: 'TradeGuide',
  data() {
    return {
      guideList: [
        { title: '如何注册广州数据交易所会员并完成实名认证？' },
        { title: '交易手把手 | 个人信息保护影响评估 (PIA) 包含哪些内容？' },
        { title: '交易手把手 | 个人信息处理者的义务有哪些？' },
        { title: '交易手把手 | 特殊情形下，个人和个人信息处理者应如何处理个人信息？' },
        { title: '交易手把手 | 在个人信息处理活动中，个人的权利有哪些？' },
        { title: '交易手把手 | 个人信息跨境提供的规则有哪些？' },
        { title: '交易手把手 | 敏感个人信息的处理规则有哪些？' },
        { title: '交易手把手 | 个人信息处理涉及共同处理、委托处理、转移与自动化决策时要注意什么？' },
        { title: '交易手把手 | 如何处理个人信息？' }
      ]
    }
  }
}
</script>

<style scoped>
.trade-guide-page {
  padding: 0;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 3px solid #0052d9;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.guide-card {
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  background: linear-gradient(180deg, #ff4d4f 0%, #0052d9 100%);
  color: #fff;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZGVmcz48cGF0dGVybiBpZD0iZ3JpZCIgd2lkdGg9IjIwIiBoZWlnaHQ9IjIwIiBwYXR0ZXJuVW5pdHM9InVzZXJTcGFjZU9uVXNlIj48cGF0aCBkPSJNMjAgMEgwVjIwSDIwVjBaIiBmaWxsPSJub25lIiBzdHJva2U9IiMxMDQwODAiIHN0cm9rZS13aWR0aD0iMSIvPjwvcGF0dGVybj48L2RlZnM+PHJlY3Qgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSIgZmlsbD0idXJsKCNncmlkKSIvPjxwYXRoIGQ9Ik01MCAxMDBMMzAgMTUwTDEwIDEwMEwzMCA1MEw1MCAxMDBaIiBmaWxsPSJub25lIiBzdHJva2U9IiMwMDUyZDkiIHN0cm9rZS13aWR0aD0iMiIvPjxwYXRoIGQ9Ik0xNTAgMTAwTDEzMCAxNTBMMTEwIDEwMEwxMzAgNTBMMTUwIDEwMFoiIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzAwNTJkOSIgc3Ryb2tlLXdpZHRoPSIyIi8+PC9zdmc+');
  opacity: 0.3;
}

.card-text {
  position: relative;
  z-index: 1;
  font-size: 14px;
  line-height: 1.5;
  text-align: center;
  margin: 0;
}

.pagination {
  text-align: center;
}
</style>