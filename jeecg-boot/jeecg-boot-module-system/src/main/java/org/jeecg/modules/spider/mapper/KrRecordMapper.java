package org.jeecg.modules.spider.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.spider.entity.KrRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 36氪爬虫抓取
 * @Author: jeecg-boot
 * @Date:   2019-06-18
 * @Version: V1.0
 */
public interface KrRecordMapper extends BaseMapper<KrRecord> {
    /**
     * 查询36kr最大id
     * @return
     */
    Integer selectMaxKrId(@Param("oplatformCore") Integer oplatformCore);

    /**
     * 查询36kr最小id
     * @return
     */
    Integer selectMinKrId(@Param("oplatformCore") Integer oplatformCore);

    /**
     * 查询未分词kr新闻
     */
    List<KrRecord> selectNotAnalyze(@Param("size") Integer size);
}
