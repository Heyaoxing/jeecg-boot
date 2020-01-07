package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.enums.OplatformEnum;
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
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * @Auther: 002954
 * @Date: 2019/12/3 10:31
 * @Description:
 */
@Slf4j
public class SneezeSpiderJob implements Job {

    @Autowired
    private IKrRecordService krRecordService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long start=System.currentTimeMillis();
        log.info("开始抓取图卦");
        try {
            spider();
        } catch (Exception e) {
            log.error("图卦异常", e);
        }
        log.info("结束抓取图卦 cost:"+(System.currentTimeMillis()-start));
    }

    private void spider() throws IOException {
        Document document = Jsoup.connect("http://www.dapenti.com/blog/blog.asp?subjectid=70&name=xilei").get();
        Elements li = document.getElementsByTag("li");
        if (Objects.isNull(li)) {
            return;
        }

        Map<Integer, String> urls = new HashedMap();
        for (Element item : li) {
            String href = item.select("a").attr("href");
            String idStr = parse(href).get("id");
            if (!StringUtils.isNumeric(idStr)) {
                continue;
            }
            int id = Integer.parseInt(idStr);
            boolean exists = krRecordService.checkKrId(id, OplatformEnum.SNEEZE_NEWS.getCode());
            if (exists) {
                continue;
            }
            urls.put(id, "http://www.dapenti.com/blog/" + href);
        }

        log.info("需要抓取的链接数:"+urls.size());

        urls.forEach((key, value) -> {
            try {
                process(value, key);
            } catch (IOException e) {
                log.error("", e);
            }
        });
    }

    private void process(String url, int id) throws IOException {
        Document newDocument = Jsoup.connect(url).get();
        if (Objects.isNull(newDocument)) {
            return;
        }

        Elements contents = newDocument.getElementsByTag("div").select(".oblog_text");
        if (CollectionUtils.isEmpty(contents)) {
            return;
        }
        Map<String, String> newMap = new HashMap<>();
        int index = 0;
        String type = "descript";
        List<KrRecord> list = new ArrayList<>();
        for (Element item : contents.get(1).getElementsByTag("p")) {
            try {
                Elements img = item.getElementsByTag("img");
                if (!CollectionUtils.isEmpty(img)) {
                    type = "image";
                    append(newMap, index, img.get(0).select("img").attr("src"), type);
                    continue;
                }
                type = "descript";
                if (item.text().contains("【")) {
                    String indexStr = item.text().substring(1, item.text().indexOf("】"));
                    if (StringUtils.isNumeric(indexStr)) {
                        type = "title";
                        index = Integer.parseInt(indexStr);
                    }
                }
                append(newMap, index, item.text(), type);
            } catch (Exception e) {
                log.error("", e);
            }
        }

        newMap.forEach((key, value) -> {
            if (!key.contains("image")) {
                return;
            }
            String indexStr = key.substring(0, key.indexOf("_"));
            String title = newMap.get(indexStr + "_title");
            String image = newMap.get(indexStr + "_image");
            String descript = newMap.get(indexStr + "_descript");
            Date now = new Date();
            KrRecord krRecord = new KrRecord();
            krRecord.setKrId((id));
            krRecord.setOplatformCore(OplatformEnum.SNEEZE_NEWS.getCode());
            krRecord.setTitile(title);
            krRecord.setImageUrl(image);
            krRecord.setDescription(descript);
            krRecord.setUpdatedAt(now);
            krRecord.setPublishedAt(now);
            krRecord.setNewsUrl(url);
            krRecord.setCreateTime(now);
            list.add(krRecord);
            log.info(image);
        });

        if (!CollectionUtils.isEmpty(list)) {
            krRecordService.saveBatch(list);
        }
    }

    private void append(Map<String, String> newMap, int index, String content, String type) {
        if (index == 0) {
            return;
        }

        if (StringUtils.isBlank(content)) {
            return;
        }

        if (type.equals("title")) {
            String key = String.format("%s_%s", index, type);
            add(newMap, key, content);
        }

        if (type.equals("descript")) {
            String key = String.format("%s_%s", index, type);
            add(newMap, key, content);
        }

        if (type.equals("image")) {
            String key = String.format("%s_%s", index, type);
            newMap.put(key, content);
        }
    }

    private void add(Map<String, String> newMap, String key, String content) {
        String value = newMap.get(key);
        if (StringUtils.isBlank(value)) {
            newMap.put(key, content);
        } else {
            newMap.put(key, value + " " + content);
        }
    }

    /**
     * 解析url
     *
     * @param url
     * @return
     */
    private Map<String, String> parse(String url) {
        if (url == null) {
            return null;
        }
        url = url.trim();
        if (url.equals("")) {
            return null;
        }
        String[] urlParts = url.split("\\?");
        //没有参数
        if (urlParts.length == 1) {
            return null;
        }
        //有参数
        String[] params = urlParts[1].split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params) {
            String[] keyValue = param.split("=");
            map.put(keyValue[0], keyValue[1]);
        }
        return map;
    }
}
