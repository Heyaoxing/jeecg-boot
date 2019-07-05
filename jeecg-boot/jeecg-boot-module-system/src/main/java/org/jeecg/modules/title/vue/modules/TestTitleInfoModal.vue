<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :maskClosable="false"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel">
    <a-spin :spinning="confirmLoading">
      <!-- 主表单区域 -->
      <a-form :form="form">
        <a-row>
          <a-col :span="12" :gutter="8">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="标题">
              <a-input placeholder="请输入标题" v-decorator="['title', {}]"/>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>

      <!-- 子表单区域 -->
      <a-tabs v-model="activeKey" @change="handleChangeTabs">
        <a-tab-pane tab="标题用户" :key="refKeys[0]" :forceRender="true">
          <j-editable-table
            :ref="refKeys[0]"
            :loading="testUserInfoTable.loading"
            :columns="testUserInfoTable.columns"
            :dataSource="testUserInfoTable.dataSource"
            :maxHeight="300"
            :rowNumber="true"
            :rowSelection="true"
            :actionButton="true"/>
        </a-tab-pane>
        <a-tab-pane tab="订单标题" :key="refKeys[1]" :forceRender="true">
          <j-editable-table
            :ref="refKeys[1]"
            :loading="testTitleOrderTable.loading"
            :columns="testTitleOrderTable.columns"
            :dataSource="testTitleOrderTable.dataSource"
            :maxHeight="300"
            :rowNumber="true"
            :rowSelection="true"
            :actionButton="true"/>
        </a-tab-pane>
      </a-tabs>

    </a-spin>
  </a-modal>
</template>

<script>

  import moment from 'moment'
  import pick from 'lodash.pick'
  import { FormTypes } from '@/utils/JEditableTableUtil'
  import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'

  export default {
    name: 'TestTitleInfoModal',
    mixins: [JEditableTableMixin],
    data() {
      return {
        // 新增时子表默认添加几行空数据
        addDefaultRowNum: 1,
        validatorRules: {
        },
        refKeys: ['testUserInfo', 'testTitleOrder', ],
        activeKey: 'testUserInfo',
        // 标题用户
        testUserInfoTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '用户名',
              key: 'user',
              type: FormTypes.input,
              defaultValue: '',
              placeholder: '请输入${title}',
            },
            {
              title: 'titleId',
              key: 'titleId',
              type: FormTypes.input,
              defaultValue: '',
              placeholder: '请输入${title}',
            },
          ]
        },
        // 订单标题
        testTitleOrderTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '订单号',
              key: 'order',
              type: FormTypes.input,
              defaultValue: '',
              placeholder: '请输入${title}',
            },
            {
              title: 'titleId',
              key: 'titleId',
              type: FormTypes.input,
              defaultValue: '',
              placeholder: '请输入${title}',
            },
          ]
        },
        url: {
          add: "/title/testTitleInfo/add",
          edit: "/title/testTitleInfo/edit",
          testUserInfo: {
            list: '/title/testTitleInfo/queryTestUserInfoByMainId'
          },
          testTitleOrder: {
            list: '/title/testTitleInfo/queryTestTitleOrderByMainId'
          },
        }
      }
    },
    methods: {
 
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'title', ))
          // 时间格式化
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.testUserInfo.list, params, this.testUserInfoTable)
          this.requestSubTableData(this.url.testTitleOrder.list, params, this.testTitleOrderTable)
        }
      },
 
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        //时间格式化
        return {
          ...main, // 展开
          testUserInfoList: allValues.tablesValue[0].values,
          testTitleOrderList: allValues.tablesValue[1].values,
        }
      }
    }
  }
</script>

<style scoped>
</style>