package org.jeecg.common.enums;

import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @Auther: 002954
 * @Date: 2019/7/5 17:45
 * @Description:平台代码
 */
public enum OplatformEnum {
    KR_NEWS(1, "36kr"),
    CENTRAL_NEWS(2, "新闻联播"),
    SNEEZE_NEWS(3, "喷嚏图挂");

    private Integer code;
    private String descript;

    public Integer getCode() {
        return code;
    }

    public String getDescript() {
        return descript;
    }

    OplatformEnum(Integer code, String descript) {
        this.code = code;
        this.descript=descript;
    }
}
