package org.jeecg.modules.quartz.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.jeecg.common.enums.OplatformEnum;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.HttpRequestExecutor;
import org.jeecg.common.util.HttpResponse;
import org.jeecg.common.util.Native2AsciiUtils;
import org.jeecg.modules.spider.entity.KrRecord;
import org.jeecg.modules.spider.service.IKrRecordService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Auther: 002954
 * @Date: 2019/7/5 18:01
 * @Description:
 */
@Slf4j
public class CentralSpiderJob implements Job {
    /**
     * 若参数变量名修改 QuartzJobController中也需对应修改
     */
    private String parameter = "1000000";

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Autowired
    private IKrRecordService krRecordService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        int dateId = Integer.parseInt(parameter);
        spider(dateId);
    }

    private void spider(int dateId) {

        CloseableHttpClient httpClient = null;
        String result = null;
        try {
            Header contentTypeHeader = new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            httpClient = HttpRequestExecutor.getHttpsClient();
            String body = String.format("{\"api_name\":\"cctv_news\",\"token\":\"69d2e92ce5222e7d919e90a0fcac506aa182c4e3701c3fb415c5469a\",\"params\":{\"date\":\"%s\"}}", dateId);
            HttpResponse httpResponse = HttpRequestExecutor.doPost(httpClient, "http://api.waditu.com", body, contentTypeHeader);
            result = Native2AsciiUtils.ascii2Native(httpResponse.getStringResult());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("异常" + e.getMessage());
        } finally {
            try {
                if (Objects.nonNull(httpClient))
                    httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        }


        if (StringUtils.isEmpty(result)) {
            log.info("返回数据为空");
            return;
        }

        JSONObject jsonObject = JSON.parseObject(result);
        JSONObject dataJsonObj = jsonObject.getJSONObject("data");
        if (Objects.isNull(dataJsonObj)) {
            log.info("未找到数据");
            return;
        }
        JSONArray itemsJsonArray = dataJsonObj.getJSONArray("items");
        if (CollectionUtils.isEmpty(itemsJsonArray)) {
            log.info("数据列表中未包含数据");
            return;
        }
        List<KrRecord> list = new ArrayList<>(itemsJsonArray.size());

        for (int i = 0; i < itemsJsonArray.size(); i++) {
            JSONArray itemJsonArray = itemsJsonArray.getJSONArray(i);
            KrRecord krRecord = new KrRecord();
            krRecord.setKrId(dateId);
            krRecord.setOplatformCore(OplatformEnum.CENTRAL_NEWS.getCode());
            krRecord.setTitile(itemJsonArray.get(1).toString());
            krRecord.setDescription(itemJsonArray.get(2).toString());
            krRecord.setUpdatedAt(DateUtils.str2Date(itemJsonArray.get(0).toString(), DateUtils.yyyyMMdd));
            krRecord.setPublishedAt(DateUtils.str2Date(itemJsonArray.get(0).toString(), DateUtils.yyyyMMdd));
            krRecord.setCreateTime(new Date());
            list.add(krRecord);
        }

        Integer maxKrId = krRecordService.selectMaxKrId(OplatformEnum.CENTRAL_NEWS.getCode());
        List<KrRecord> krRecords=list;
        if(Objects.nonNull(maxKrId)){
            krRecords = list.stream().filter(p -> p.getKrId() > maxKrId).collect(Collectors.toList());
        }

        if(CollectionUtils.isEmpty(krRecords)){
            log.info("--采集完成,采集总数:{} 入库数:{}" , list.size(),krRecords.size());
            return;
        }
        boolean isSave = krRecordService.saveBatch(krRecords);
        if (isSave) {
            log.info("采集完成,采集总数:{} 入库数:{}" , list.size(),krRecords.size());
        }
    }
}
