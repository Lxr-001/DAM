<template>
  <div style="height: 400px;padding: 20px 0 0 32px">
    <h4 style="margin-bottom: 20px">{{ title }}</h4>
    <v-chart :forceFit="true" :height="300" :data="dataSource" :scale="scale" :onClick="handleClick">
      <v-tooltip :showTitle="false" dataKey="item*percent"/>
      <v-axis/>
      <v-legend dataKey="item"/>
      <v-pie position="percent" color="item" :v-style="pieStyle" :label="labelConfig"/>
      <v-coord type="theta"/>
    </v-chart>
  </div>
</template>

<script>
import { getAction } from '@api/manage'
import { ChartEventMixins } from '../mixins/ChartMixins'

const DataSet = require('@antv/data-set')

export default {
  name: 'allPie',
  mixins: [ChartEventMixins],
  props: {
    title: {
      type: String,
      default: ''
    },
    tab: {
      type: String,
      default: '0'
    },
    dataSource: {
      type: Array,
      default: () => [
        { item: '示例一', count: 40 },
        { item: '示例二', count: 21 },
        { item: '示例三', count: 17 },
        { item: '示例四', count: 13 },
        { item: '示例五', count: 9 }
      ]
    }
},
  data() {
    return {
      //dataSource:[],
      scale: [{
        dataKey: 'percent',
        min: 0,
        formatter: '.0%'
      }],
      pieStyle: {
        stroke: '#fff',
        lineWidth: 1
      },
      labelConfig: ['percent', {
        formatter: (val, item) => {
          return item.point.item + ': ' + val
        }
      }]
    }
  },
  computed: {
    data() {
      let dv = new DataSet.View().source(this.dataSource)
      // 计算数据百分比
      dv.transform({
        type: 'percent',
        field: 'count',
        dimension: 'item',
        as: 'percent'
      })
      return dv.rows
    }
  },
  created() {
    //this.getData()
  },
  methods: {
    getData(){
      let formData = {
        tabs: this.tab
      }
      getAction('/akFlightTestFailureData/akFlightTestFailureData/list4', formData).then(res => {
        if (res.success) {
          if(res.result){
            this.dataSource = res.result
          }
        }
      })
    },
  }
}
</script>