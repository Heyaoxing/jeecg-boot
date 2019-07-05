package org.jeecg.modules.spider.dto;

import lombok.Data;
import org.jeecg.common.api.vo.KeyValue;
import org.jeecg.modules.spider.entity.KrRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: 002954
 * @Date: 2019/6/19 18:18
 * @Description:
 */
@Data
public class KrRecordResDto  extends KrRecord implements Serializable  {

    public KrRecordResDto(){
        words=new ArrayList<>();
    }
    /**
     * 分词列表
     */
    private List<KeyValue> words;
}
