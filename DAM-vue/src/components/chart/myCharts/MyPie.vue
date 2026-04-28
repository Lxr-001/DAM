<template>
  <div style="height: 400px;padding: 20px 0 0 32px">
    <h4 style="margin-bottom: 20px">{{ title }}</h4>
    <v-chart :forceFit="true" :height="300" :data="data" :scale="scale" :onClick="handleClick">
      <v-tooltip :showTitle="false" dataKey="item*percent"/>
      <v-axis/>
      <v-legend dataKey="item"/>
      <v-pie position="percent" color="item" :v-style="pieStyle" :label="labelConfig"/>
      <v-coord type="theta"/>
    </v-chart>
  </div>
</template>

<script>
  const DataSet = require('@antv/data-set')
  import { ChartEventMixins } from '../mixins/ChartMixins'

  export default {
    name: 'MyPie',
    mixins: [ChartEventMixins],
    props: {
      title: {
        type: String,
        default: ''
      },
      dataSource: {
        type: Array,
        default: () => [
        ]
      }
    },
    data() {
      return {
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
    }
  }
</script>