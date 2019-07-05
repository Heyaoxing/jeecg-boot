package org.jeecg.modules.title.service;

import org.jeecg.modules.title.entity.TestUserInfo;
import org.jeecg.modules.title.entity.TestTitleOrder;
import org.jeecg.modules.title.entity.TestTitleInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 标题
 * @Author: jeecg-boot
 * @Date:   2019-06-14
 * @Version: V1.0
 */
public interface ITestTitleInfoService extends IService<TestTitleInfo> {

	/**
	 * 添加一对多
	 * 
	 */
	public void saveMain(TestTitleInfo testTitleInfo, List<TestUserInfo> testUserInfoList, List<TestTitleOrder> testTitleOrderList) ;
	
	/**
	 * 修改一对多
	 * 
	 */
	public void updateMain(TestTitleInfo testTitleInfo, List<TestUserInfo> testUserInfoList, List<TestTitleOrder> testTitleOrderList);
	
	/**
	 * 删除一对多
	 */
	public void delMain(String id);
	
	/**
	 * 批量删除一对多
	 */
	public void delBatchMain(Collection<? extends Serializable> idList);
	
}
