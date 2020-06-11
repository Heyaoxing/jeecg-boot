package org.jeecg.modules.quartz.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.jeecg.common.enums.OplatformEnum;
import org.jeecg.common.util.*;
import org.jeecg.modules.spider.entity.KrRecord;
import org.jeecg.modules.spider.service.IKrRecordService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
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
    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        int krId = Integer.parseInt(parameter);
        log.info(String.format("开是36氪爬取,参数:%s 时间:%s", krId, DateUtils.now()));

        spider(krId);
    }

    private void spider(int krId) throws IOException {
        Document document = Jsoup.connect("https://36kr.com/newsflashes").timeout(3000).get();
        Elements e = document.getElementsByTag("script");
        String pageCallback="";
        for (Element element : e) {
            if(element.data().startsWith("window.initialState=")){
                Gson gson=new Gson();

                JSONObject jsonObject= JSON.parseObject(element.data().substring("window.initialState=".length()));
                Map<String, Object> resultMap=new HashMap<>();
                CompareJson.convertJsonToMap(jsonObject,"",resultMap);
                for (String item : resultMap.keySet()) {
                    if(item.contains("pageCallback")){
                        pageCallback=resultMap.get(item).toString();
                        System.out.println(pageCallback);
                        break;
                    }
                }
            }
        }

        CloseableHttpClient httpClient = null;
        String result = null;
        String body=String.format("{\"partner_id\":\"web\",\"timestamp\":1591854656831,\"param\":{\"pageSize\":20,\"pageEvent\":0,\"pageCallback\":\"%s\",\"siteId\":1,\"platformId\":2}}\n",pageCallback);
        try {
            Header contentTypeHeader = new BasicHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            httpClient = HttpRequestExecutor.getHttpsClient();
            HttpResponse httpResponse = HttpRequestExecutor.doPost(httpClient, String.format("https://gateway.36kr.com/api/mis/nav/newsflash/flow"),body, contentTypeHeader);
            String response=httpResponse.getStringResult();
            result = Native2AsciiUtils.ascii2Native(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("异常" + ex.getMessage());
        } finally {
            try {
                if (Objects.nonNull(httpClient)){
                    httpClient.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (StringUtils.isEmpty(result)) {
            System.out.println("返回数据为空");
            return;
        }

        JSONObject jsonObject = JSON.parseObject(result);
        JSONObject dataJsonObj = jsonObject.getJSONObject("data");
        if (Objects.isNull(dataJsonObj)) {
            System.out.println("未找到数据");
            return;
        }

        JSONArray itemsJsonArray = dataJsonObj.getJSONArray("itemList");
        if (CollectionUtils.isEmpty(itemsJsonArray)) {
            System.out.println("数据列表中未包含数据");
            return;
        }

        List<KrRecord> list = new ArrayList<>(itemsJsonArray.size());
        for (int i = 0; i < itemsJsonArray.size(); i++) {
            KrRecord krRecord = new KrRecord();
            JSONObject itemJsonObj = itemsJsonArray.getJSONObject(i);
            krRecord.setKrId(Long.parseLong(itemJsonObj.getString("itemId")));
            krRecord.setOplatformCore(OplatformEnum.KR_NEWS.getCode());

            JSONObject JsonObj=itemJsonObj.getJSONObject("templateMaterial");
            krRecord.setTitile(JsonObj.getString("widgetTitle"));
            krRecord.setDescription(JsonObj.getString("widgetContent"));
            krRecord.setUpdatedAt(JsonObj.getTimestamp("publishTime"));
            krRecord.setPublishedAt(JsonObj.getTimestamp("publishTime"));
            String newUrl = JsonObj.getString("sourceUrlRoute");
            if(StringUtils.isNotBlank(newUrl)){
                krRecord.setNewsUrl( java.net.URLDecoder.decode(newUrl.substring("webview?url=".length()), "UTF-8"));
            }
            krRecord.setCreateTime(new Date());
            list.add(krRecord);
        }


        Long maxKrId = krRecordService.selectMaxKrId(OplatformEnum.KR_NEWS.getCode());
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
}
