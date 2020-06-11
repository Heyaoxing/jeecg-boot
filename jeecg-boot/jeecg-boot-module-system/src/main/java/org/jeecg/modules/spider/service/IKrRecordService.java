package org.jeecg.modules.spider.service;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.spider.entity.KrRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 36氪爬虫抓取
 * @Author: jeecg-boot
 * @Date:   2019-06-18
 * @Version: V1.0
 */
public interface IKrRecordService extends IService<KrRecord> {
    /**
     * 查询36kr最大id
     * @return
     */
    Long selectMaxKrId(Integer oplatformCore);

    /**
     * 查询36kr最小id
     * @return
     */
    Long selectMinKrId(Integer oplatformCore);

    /**
     * 查询未分词kr新闻
     */
    List<KrRecord> selectNotAnalyze(@Param("size") Integer size);

    boolean checkKrId(Long krId,int oplatformCore);
}
