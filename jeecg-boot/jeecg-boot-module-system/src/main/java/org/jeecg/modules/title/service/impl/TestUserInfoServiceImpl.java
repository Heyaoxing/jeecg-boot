package org.jeecg.modules.title.service.impl;

import org.jeecg.modules.title.entity.TestUserInfo;
import org.jeecg.modules.title.mapper.TestUserInfoMapper;
import org.jeecg.modules.title.service.ITestUserInfoService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 标题用户
 * @Author: jeecg-boot
 * @Date:   2019-06-14
 * @Version: V1.0
 */
@Service
public class TestUserInfoServiceImpl extends ServiceImpl<TestUserInfoMapper, TestUserInfo> implements ITestUserInfoService {
	
	@Autowired
	private TestUserInfoMapper testUserInfoMapper;
	
	@Override
	public List<TestUserInfo> selectByMainId(String mainId) {
		return testUserInfoMapper.selectByMainId(mainId);
	}
}
