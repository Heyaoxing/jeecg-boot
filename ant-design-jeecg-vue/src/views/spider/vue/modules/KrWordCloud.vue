<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    
    <div>
      <v-chart :width="640" :height="400" :padding="pad" :data="data" :scale="scale">
        <v-tooltip :show-title="false" />
        <v-coord type="rect" direction="TL" />
        <v-point position="x*y" color="category" shape="cloud" tooltip="value*category" />
      </v-chart>
    </div>
  </a-modal>
</template>

<script>
import { registerShape } from 'viser-vue';
import * as $ from 'jquery';
const DataSet = require('@antv/data-set');

const scale = [
  { dataKey: 'x', nice: false },
  { dataKey: 'y', nice: false },
];

registerShape('point', 'cloud', {
  draw(cfg, container) {
    return container.addShape('text', {
      attrs: {
        fillOpacity: cfg.opacity,
        fontSize: cfg.origin._origin.size,
        rotate: cfg.origin._origin.rotate,
        text: cfg.origin._origin.text,
        textAlign: 'center',
        fontFamily: cfg.origin._origin.font,
        fill: cfg.color,
        textBaseline: 'Alphabetic',
        ...cfg.style,
        x: cfg.x,
        y: cfg.y,
      },
    });
  }
});


export default {
  name: "KrWordCloud",
   created () {
    },
    handleCancel () {
        this.close()
    },
  methods: {
    loadCloud(wordId) {
    this.visible = true;
    this.$http.get('/news/newsWord/newWordCloud', {
         params:{wordId: wordId}
        }).then(data => {
           const dv = new DataSet.View().source(data);
      const range = dv.range('value');
      const min = range[0];
      const max = range[1];
      dv.transform({
        type: 'tag-cloud',
        fields: ['x', 'value'],
        size: [540, 400],
        font: 'Verdana',
        padding: 0,
        timeInterval: 5000, // max execute time
        rotate() {
          let random = ~~(Math.random() * 4) % 4;
          if (random == 2) {
            random = 0;
          }
          return random * 90; // 0, 90, 270
        },
        fontSize(d) {
          if (d.value) {
            return ((d.value - min) / (max - min)) * (80 - 24) + 24;
          }
          return 0;
        }
      });
      this.$data.data = dv.rows;
    })
    
  },
    close () {
        this.$emit('close');
        this.visible = false;
      },
  },
  
  data() {
    return {
      pad:[0],
      data: [],
      scale,
    };
  }
};
</script>

<style lang="less" scoped>

</style>