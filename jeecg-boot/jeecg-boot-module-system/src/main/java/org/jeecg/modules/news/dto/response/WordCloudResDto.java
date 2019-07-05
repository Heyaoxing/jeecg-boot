package org.jeecg.modules.news.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: 002954
 * @Date: 2019/6/19 17:54
 * @Description:
 */
@Data
public class WordCloudResDto implements Serializable {
    private String x;
    private String value;
    private String category;
}
