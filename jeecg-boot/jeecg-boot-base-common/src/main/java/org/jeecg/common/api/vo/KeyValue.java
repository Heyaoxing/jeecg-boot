package org.jeecg.common.api.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: 002954
 * @Date: 2019/6/19 18:34
 * @Description:
 */
@Data
public class KeyValue implements Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    private String value;

    public KeyValue(){}
    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
