package org.jeecg;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * @Auther: 002954
 * @Date: 2019/12/3 10:49
 * @Description:
 */
public class HtmlTest {

    @Test
    public void spider() throws IOException {
        Document document = Jsoup.connect("http://www.dapenti.com/blog/blog.asp?subjectid=70&name=xilei").get();
        Elements li = document.getElementsByTag("li");
        if (Objects.isNull(li)) {
            return;
        }

        List<String> urls = new ArrayList<>();
        for (Element item : li) {
            String href = item.select("a").attr("href");
            System.out.println(href);
            System.out.println(parse(href).get("id"));
            System.out.println(item.text());
            urls.add("http://www.dapenti.com/blog/"+href);
        }


        for (String url : urls) {
            Document newDocument = Jsoup.connect(url).get();
            if (Objects.isNull(newDocument)) {
                continue;
            }

            Elements contents = newDocument.getElementsByTag("div").select(".oblog_text");
            if (CollectionUtils.isEmpty(contents)) {
                return;
            }
            Map<String, String> newMap = new HashMap<>();
            int index = 0;
            String type = "descript";
            for (Element item : contents.get(1).getElementsByTag("p")) {
                Elements img = item.getElementsByTag("img");
                if (!CollectionUtils.isEmpty(img)) {
                    type = "image";
                    append(newMap, index, img.get(0).select("img").attr("src"), type);
                    continue;
                }
                if (!item.text().contains("【")) {
                    type = "descript";
                } else {
                    String indexStr = item.text().substring(1, item.text().indexOf("】"));
                    if (StringUtils.isNumeric(indexStr)) {
                        type = "title";
                        index = Integer.parseInt(indexStr);
                    }
                }
                append(newMap, index, item.text(), type);
            }

            newMap.forEach((key, value) -> {
                if (!key.contains("image")) {
                    return;
                }
                String indexStr = key.substring(0, key.indexOf("_"));
                String title = newMap.get(indexStr + "_title");
                String image = newMap.get(indexStr + "_image");
                System.out.println(title+":"+image);
            });
        }
    }

    public static void append(Map<String, String> newMap, int index, String content, String type) {
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
            add(newMap, key, content);
        }
    }

    public static void add(Map<String, String> newMap, String key, String content) {
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
    public static Map<String, String> parse(String url) {
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
