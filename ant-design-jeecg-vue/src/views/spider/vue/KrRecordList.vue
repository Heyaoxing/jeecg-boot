<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="平台推文id">
              <a-input placeholder="请输入平台推文id" v-model="queryParam.krId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="标题">
              <a-input placeholder="请输入标题" v-model="queryParam.titile"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="描述">
              <a-input placeholder="请输入描述" v-model="queryParam.description"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="更新日期">
              <a-input placeholder="请输入更新日期" v-model="queryParam.updatedAt"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="原文链接">
              <a-input placeholder="请输入原文链接" v-model="queryParam.newsUrl"></a-input>
            </a-form-item>
          </a-col>
        </template>
          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('36氪爬虫抓取')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>


        <span slot="titleformat" slot-scope="text, record">
          <div v-if="record.newsUrl.length>0">
            <a v-bind:href="record.newsUrl" target="_bank">{{record.titile}}</a>
          </div>  
           <div v-else-if="record.newsUrl.length<=0">
            <span>{{record.titile}}</span>
          </div>  
        </span>

         <span slot="wordtip" slot-scope="text, record">
          <span v-for="word in record.words">
              <a-tag color="#FF9999" @click="loadCloud(word.value)">{{word.key}}</a-tag>
          </span>
        </span>
      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <krRecord-modal ref="modalForm" @ok="modalFormOk"></krRecord-modal>
    
    
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="cloudOk"
    @cancel="cloudOk"
    cancelText="关闭">
    
     <div>
      <v-chart :width="640" :height="400" :padding="pad" :data="data" :scale="scale">
        <v-tooltip :show-title="false" />
        <v-coord type="rect" direction="TL" />
        <v-point position="x*y" color="category" shape="cloud" tooltip="value*category" />
      </v-chart>
    </div>
  </a-modal>  
  </a-card>

  
</template>




<script>
  import KrRecordModal from './modules/KrRecordModal'
  import KrWordCloud from './modules/KrWordCloud'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { registerShape } from 'viser-vue';
  import * as $ from 'jquery';
  const DataSet = require('@antv/data-set');
  const scale = [
  { dataKey: 'x', nice: false },
  { dataKey: 'y', nice: false },
];

  export default {
    name: "KrRecordList",
    mixins:[JeecgListMixin],
    components: {
      KrRecordModal,KrWordCloud
    },
    data () {
      return {
         pad:[0],
        data: [],
        scale,
        confirmLoading: false,
        visible: false,
        title:'词云',
        description: '36氪爬虫抓取管理页面',
        // 表头
        columns: [
     
		   {
            title: '平台推文id',
            align:"center",
            dataIndex: 'krId'
           },
		   {
            title: '标题',
            align:"center",
            dataIndex: 'titleformat',
            scopedSlots: { customRender: 'titleformat' },
           },
		   {
            title: '描述',
            align:"left",
            dataIndex: 'description'
           },
            {
            title: '标签',
            align:"center",
            dataIndex: 'wordtip',
            scopedSlots: { customRender: 'wordtip' },
           },
		   {
            title: '更新日期',
            align:"center",
            dataIndex: 'updatedAt'
           },
          {
            title: '操作',
            dataIndex: 'action',
            width:120,
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/spider/krRecord/list",
          delete: "/spider/krRecord/delete",
          deleteBatch: "/spider/krRecord/deleteBatch",
          exportXlsUrl: "spider/krRecord/exportXls",
          importExcelUrl: "spider/krRecord/importExcel",
       },
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
    methods: {
        loadCloud(wordId) {
          this.$data.data =[];
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
      this.title='热度'+range;
      this.$data.data = dv.rows;
    })
    
    },
    close () {
        this.$emit('close');
        this.visible = false;
      },
      cloudOk(){
        this.visible = false;

      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>