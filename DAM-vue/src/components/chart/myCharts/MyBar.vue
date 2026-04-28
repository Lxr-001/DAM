<template>
  <div style="height: 400px;padding: 20px 0 0 32px">
    <h4 :style="{ marginBottom: '20px' }">{{ title }}</h4>
    <v-chart :forceFit="true" :height="height" :data="dataSource" :scale="scale" :padding="padding">
      <v-tooltip/>
      <v-axis/>
      <v-bar position="x*y"/>
    </v-chart>
    <a-button-group style="margin-top: 15px">
      <a-button type="primary" v-if="type===1" @click="changeType(1)">生产厂家</a-button>
      <a-button  v-else @click="changeType(1)">生产厂家</a-button>

      <a-button type="primary" v-if="type===2" @click="changeType(2)">问题类型</a-button>
      <a-button  v-else @click="changeType(2)">问题类型</a-button>

      <a-button type="primary" v-if="type===3" @click="changeType(3)">问题影响</a-button>
      <a-button  v-else @click="changeType(3)">问题影响</a-button>
    </a-button-group>
  </div>
</template>

<script>
import { triggerWindowResizeEvent } from '@/utils/util'
import { getAction } from '@api/manage'

export default {
  name: 'MyBar',
  props: {
    yaxisText: {
      type: String,
      default: 'y'
    },
    title: {
      type: String,
      default: ''
    },
    height: {
      type: Number,
      default: 245
    }
  },
  data() {
    return {
      padding: ['auto', 'auto', '40', '50'],
      dataSource:[],
      type: 1,
    }
  },
  computed: {
    scale() {
      return [{
        dataKey: 'y',
        alias: this.yaxisText
      }]
    }
  },
  mounted() {
    triggerWindowResizeEvent()
  },
  created() {
    this.getBarData()
  },
  methods: {
    changeType(type){
      this.type = type
      this.getBarData()
    },
    getBarData(){
      let formData = {
        type: this.type
      }
      getAction('/akIssueReport/akIssueReport/list2', formData).then(res => {
        if (res.success) {
          this.dataSource = res.result
        }
      })
    },
  },
}
</script>