package org.jeecg.modules.spider.service.impl;

import org.jeecg.modules.spider.entity.KrRecord;
import org.jeecg.modules.spider.mapper.KrRecordMapper;
import org.jeecg.modules.spider.service.IKrRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 36氪爬虫抓取
 * @Author: jeecg-boot
 * @Date: 2019-06-18
 * @Version: V1.0
 */
@Service
public class KrRecordServiceImpl extends ServiceImpl<KrRecordMapper, KrRecord> implements IKrRecordService {

    @Autowired
    private KrRecordMapper krRecordMapper;

    @Override
    public Long selectMaxKrId(Integer oplatformCore) {
        return krRecordMapper.selectMaxKrId(oplatformCore);
    }

    @Override
    public Long selectMinKrId(Integer oplatformCore) {
        return krRecordMapper.selectMinKrId(oplatformCore);
    }

    @Override
    public List<KrRecord> selectNotAnalyze(Integer size) {
        if (Objects.isNull(size) || size <= 0) size = 10;
        return krRecordMapper.selectNotAnalyze(size);
    }

    @Override
    public boolean checkKrId(Long krId,int oplatformCore){
        return krRecordMapper.selectByKrId(krId,oplatformCore)>0;
    }

}
