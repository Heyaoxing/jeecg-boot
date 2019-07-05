package org.jeecg.modules.title.service;

import org.jeecg.modules.title.entity.TestUserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 标题用户
 * @Author: jeecg-boot
 * @Date:   2019-06-14
 * @Version: V1.0
 */
public interface ITestUserInfoService extends IService<TestUserInfo> {

	public List<TestUserInfo> selectByMainId(String mainId);
}
