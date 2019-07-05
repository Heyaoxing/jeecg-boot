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
 * 爬取36氪快讯文章
 *
 * @Author Scott
 */
@Slf4j
public class KrSpiderJob implements Job {
    @Autowired
    private IKrRecordService krRecordService;
    /**
     * 若参数变量名修改 QuartzJobController中也需对应修改
     */
    private String parameter = "1000000";

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    String temple="{\"msgtype\":\"news\",\"news\":{\"articles\":[{\"title\":\"%s\",\"description\":\"%s\",\"url\":\"%s\",\"picurl\":\"https://b-ssl.duitang.com/uploads/item/201401/09/20140109032732_R8yNw.thumb.1900_0.jpeg\"}]}}";
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        int krId = Integer.parseInt(parameter);
        log.info(String.format("开是36氪爬取,参数:%s 时间:%s", krId, DateUtils.now()));


        spider(krId);

     //   spiderHistory(minKrId);
    }

    private void spider(int krId) {
        CloseableHttpClient httpClient = null;
        String result = null;
        try {
            Header contentTypeHeader = new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            httpClient = HttpRequestExecutor.getHttpsClient();
            HttpResponse httpResponse = HttpRequestExecutor.doGet(httpClient, String.format("https://36kr.com/pp/api/newsflash?b_id=%s&per_page=10", krId), contentTypeHeader);
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
            KrRecord krRecord = new KrRecord();
            JSONObject itemJsonObj = itemsJsonArray.getJSONObject(i);
            krRecord.setKrId(Integer.parseInt(itemJsonObj.getString("id")));
            krRecord.setOplatformCore(OplatformEnum.KR_NEWS.getCode());
            krRecord.setTitile(itemJsonObj.getString("title"));
            krRecord.setDescription(itemJsonObj.getString("description"));
            krRecord.setUpdatedAt(DateUtils.str2Date(itemJsonObj.getString("updated_at"), DateUtils.datetimeFormat));
            krRecord.setPublishedAt(DateUtils.str2Date(itemJsonObj.getString("published_at"), DateUtils.datetimeFormat));
            krRecord.setNewsUrl(itemJsonObj.getString("news_url"));
            krRecord.setCreateTime(new Date());
            list.add(krRecord);
        }
        Integer maxKrId = krRecordService.selectMaxKrId(OplatformEnum.KR_NEWS.getCode());
        List<KrRecord> krRecords=list;
        if(Objects.nonNull(maxKrId)){
             krRecords = list.stream().filter(p -> p.getKrId() > maxKrId).collect(Collectors.toList());
        }

        if(CollectionUtils.isEmpty(krRecords)){
            log.info("--采集完成,采集总数:{} 入库数:{}" , list.size(),krRecords.size());
            return;
        }
        boolean isSave = krRecordService.saveBatch(krRecords);

        for (KrRecord item:krRecords){
            long start=System.currentTimeMillis();
            log.info("开始推送到企业微信机器人");
            String url=StringUtils.isEmpty(item.getNewsUrl())?"url":item.getNewsUrl();
            String content=String.format(temple,item.getTitile(),item.getDescription(),url);
            String res=  HttpRequestExecutor.doPostByJson("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=305df922-5ab5-4870-9d13-6664f340c65b",content);
            log.info("推送到企业微信机器人 结果{} 耗时:{} content:{} ",res,System.currentTimeMillis()-start,content);
        }

        if (isSave) {
            log.info("采集完成,采集总数:{} 入库数:{}" , list.size(),krRecords.size());
        }
    }

    private void spiderHistory(int krId) {

        if(krId<=0){
            log.info("采集完成");
            return;
        }

        CloseableHttpClient httpClient = null;
        String result = null;
        try {
            Header contentTypeHeader = new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            httpClient = HttpRequestExecutor.getHttpsClient();
            HttpResponse httpResponse = HttpRequestExecutor.doGet(httpClient, String.format("https://36kr.com/pp/api/newsflash?b_id=%s&per_page=30", krId), contentTypeHeader);
            String response=httpResponse.getStringResult();
            result = Native2AsciiUtils.ascii2Native(response);
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
            KrRecord krRecord = new KrRecord();
            JSONObject itemJsonObj = itemsJsonArray.getJSONObject(i);
            krRecord.setKrId(Integer.parseInt(itemJsonObj.getString("id")));
            krRecord.setTitile(itemJsonObj.getString("title"));
            krRecord.setDescription(itemJsonObj.getString("description"));
            krRecord.setOplatformCore(OplatformEnum.KR_NEWS.getCode());
            krRecord.setUpdatedAt(DateUtils.str2Date(itemJsonObj.getString("updated_at"), DateUtils.datetimeFormat));
            krRecord.setPublishedAt(DateUtils.str2Date(itemJsonObj.getString("published_at"), DateUtils.datetimeFormat));
            krRecord.setNewsUrl(itemJsonObj.getString("news_url"));
            krRecord.setCreateTime(new Date());
            list.add(krRecord);
        }
        List<KrRecord> krRecords=list;
        if(Objects.nonNull(krId)){
            krRecords = list.stream().filter(p -> p.getKrId() < krId).collect(Collectors.toList());
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
