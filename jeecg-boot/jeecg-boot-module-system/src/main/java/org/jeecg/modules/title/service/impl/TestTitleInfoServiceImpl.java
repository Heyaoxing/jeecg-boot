package org.jeecg.modules.title.service.impl;

import org.jeecg.modules.title.entity.TestTitleInfo;
import org.jeecg.modules.title.entity.TestUserInfo;
import org.jeecg.modules.title.entity.TestTitleOrder;
import org.jeecg.modules.title.mapper.TestUserInfoMapper;
import org.jeecg.modules.title.mapper.TestTitleOrderMapper;
import org.jeecg.modules.title.mapper.TestTitleInfoMapper;
import org.jeecg.modules.title.service.ITestTitleInfoService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 标题
 * @Author: jeecg-boot
 * @Date:   2019-06-14
 * @Version: V1.0
 */
@Service
public class TestTitleInfoServiceImpl extends ServiceImpl<TestTitleInfoMapper, TestTitleInfo> implements ITestTitleInfoService {

	@Autowired
	private TestTitleInfoMapper testTitleInfoMapper;
	@Autowired
	private TestUserInfoMapper testUserInfoMapper;
	@Autowired
	private TestTitleOrderMapper testTitleOrderMapper;
	
	@Override
	@Transactional
	public void saveMain(TestTitleInfo testTitleInfo, List<TestUserInfo> testUserInfoList,List<TestTitleOrder> testTitleOrderList) {
		testTitleInfoMapper.insert(testTitleInfo);
		for(TestUserInfo entity:testUserInfoList) {
			//外键设置
			entity.setTitleId(testTitleInfo.getId());
			testUserInfoMapper.insert(entity);
		}
		for(TestTitleOrder entity:testTitleOrderList) {
			//外键设置
			entity.setTitleId(testTitleInfo.getId());
			testTitleOrderMapper.insert(entity);
		}
	}

	@Override
	@Transactional
	public void updateMain(TestTitleInfo testTitleInfo,List<TestUserInfo> testUserInfoList,List<TestTitleOrder> testTitleOrderList) {
		testTitleInfoMapper.updateById(testTitleInfo);
		
		//1.先删除子表数据
		testUserInfoMapper.deleteByMainId(testTitleInfo.getId());
		testTitleOrderMapper.deleteByMainId(testTitleInfo.getId());
		
		//2.子表数据重新插入
		for(TestUserInfo entity:testUserInfoList) {
			//外键设置
			entity.setTitleId(testTitleInfo.getId());
			testUserInfoMapper.insert(entity);
		}
		for(TestTitleOrder entity:testTitleOrderList) {
			//外键设置
			entity.setTitleId(testTitleInfo.getId());
			testTitleOrderMapper.insert(entity);
		}
	}

	@Override
	@Transactional
	public void delMain(String id) {
		testUserInfoMapper.deleteByMainId(id);
		testTitleOrderMapper.deleteByMainId(id);
		testTitleInfoMapper.deleteById(id);
	}

	@Override
	@Transactional
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			testUserInfoMapper.deleteByMainId(id.toString());
			testTitleOrderMapper.deleteByMainId(id.toString());
			testTitleInfoMapper.deleteById(id);
		}
	}
	
}
