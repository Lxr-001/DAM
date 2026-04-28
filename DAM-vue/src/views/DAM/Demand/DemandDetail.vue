<template>
  <div class="demand-detail-page">
    <a-spin :spinning="loading">
      <div class="top-bar">
        <h1 class="page-title">{{ detail.demandTitile || '-' }}</h1>
      </div>

      <table class="detail-table">
        <tbody>
          <tr>
            <td class="label-col">需求标题</td>
            <td class="value-col">{{ detail.demandTitile || '-' }}</td>
            <td class="label-col">需求类型</td>
            <td class="value-col">{{ detail.demandType || '-' }}</td>
          </tr>
          <tr>
            <td class="label-col">应用场景</td>
            <td class="value-col">{{ detail.applicationScenario || '-' }}</td>
            <td class="label-col">细分场景</td>
            <td class="value-col">{{ detail.segmentedScenario || '-' }}</td>
          </tr>
          <tr>
            <td class="label-col">需求描述</td>
            <td class="value-col" colspan="3">{{ detail.demandDescription || '-' }}</td>
          </tr>
        </tbody>
      </table>
    </a-spin>
  </div>
</template>

<script>
import { getAction } from '@/api/manage'

export default {
  name: 'DemandDetail',
  data() {
    return {
      loading: false,
      detail: {}
    }
  },
  mounted() {
    this.loadDetail()
  },
  methods: {
    loadDetail() {
      const demandId = this.$route.query.id
      if (!demandId) {
        this.detail = {}
        return
      }
      this.loading = true
      getAction('/demand/queryById', { id: demandId }).then((res) => {
        if (res.success) {
          this.detail = res.result || {}
        } else {
          this.$message.warning(res.message || '查询详情失败')
        }
      }).finally(() => {
        this.loading = false
      })
    }
  },
  watch: {
    '$route.query.id'() {
      this.loadDetail()
    }
  }
}
</script>

<style scoped>
.demand-detail-page {
  padding: 24px;
  background-color: #fff;
  min-height: 100vh;
}

.top-bar {
  margin-bottom: 16px;
}
.page-title {
  font-size: 22px;
  font-weight: 500;
  margin: 0;
}

.detail-table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #e8e8e8;
}
.detail-table td {
  border: 1px solid #e8e8e8;
  padding: 12px 16px;
  font-size: 14px;
}
.label-col {
  width: 20%;
  background-color: #fafafa;
  color: #666;
  font-weight: 500;
  text-align: right;
}
.value-col {
  width: 30%;
  color: #333;
  white-space: pre-wrap;
}
</style>