package org.jeecg.modules.title.mapper;

import java.util.List;
import org.jeecg.modules.title.entity.TestUserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 标题用户
 * @Author: jeecg-boot
 * @Date:   2019-06-14
 * @Version: V1.0
 */
public interface TestUserInfoMapper extends BaseMapper<TestUserInfo> {

	public boolean deleteByMainId(String mainId);
    
	public List<TestUserInfo> selectByMainId(String mainId);
}
