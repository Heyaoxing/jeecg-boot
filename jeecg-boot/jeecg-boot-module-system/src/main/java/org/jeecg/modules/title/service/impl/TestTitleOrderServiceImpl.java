package org.jeecg.modules.title.service.impl;

import org.jeecg.modules.title.entity.TestTitleOrder;
import org.jeecg.modules.title.mapper.TestTitleOrderMapper;
import org.jeecg.modules.title.service.ITestTitleOrderService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 订单标题
 * @Author: jeecg-boot
 * @Date:   2019-06-14
 * @Version: V1.0
 */
@Service
public class TestTitleOrderServiceImpl extends ServiceImpl<TestTitleOrderMapper, TestTitleOrder> implements ITestTitleOrderService {
	
	@Autowired
	private TestTitleOrderMapper testTitleOrderMapper;
	
	@Override
	public List<TestTitleOrder> selectByMainId(String mainId) {
		return testTitleOrderMapper.selectByMainId(mainId);
	}
}
