package org.jeecg.modules.quartz.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.spider.entity.KrRecord;
import org.jeecg.modules.spider.service.IKrRecordService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 示例不带参定时任务
 * 
 * @Author Scott
 */
@Slf4j
public class HaohuaTestJob implements Job {
	@Autowired
	private IKrRecordService krRecordService;
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log.info(String.format(" 开始执行更新状态操作"));
		long start=System.currentTimeMillis();
		List<KrRecord> records = krRecordService.selectNotAnalyze(10);
        for (KrRecord item:records){
			//更新分析状态
			krRecordService.update(new KrRecord().setIsAnalyze(1), new LambdaQueryWrapper<KrRecord>().eq(KrRecord::getId, item.getId()));
		}
		log.info(String.format(" 结束更新操作,耗时" +(System.currentTimeMillis()-start)));
	}
}
