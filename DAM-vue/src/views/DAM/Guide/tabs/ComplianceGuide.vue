<template>
  <div class="compliance-guide-page">
    <h2 class="page-title">合规指引</h2>
    <div class="card-grid">
      <a-card
        v-for="(item, index) in complianceList"
        :key="index"
        class="compliance-card"
        :body-style="{ padding: '20px' }"
      >
        <div class="card-bg"></div>
        <p class="card-text">{{ item.title }}</p>
      </a-card>
    </div>
    <a-pagination
      class="pagination"
      :total="3"
      :page-size="9"
      default-current="1"
      show-size-changer
      :page-size-options="['9', '15', '20']"
    />
  </div>
</template>

<script>
export default {
  name: 'ComplianceGuide',
  data() {
    return {
      complianceList: [
        { title: '广东数据产权登记-规则解析' },
        { title: '广东省数据产权登记试点工作登记程序要点' },
        { title: '广东省数据产权登记试点工作基本要点' }
      ]
    }
  }
}
</script>

<style scoped>
.compliance-guide-page {
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
.compliance-card {
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