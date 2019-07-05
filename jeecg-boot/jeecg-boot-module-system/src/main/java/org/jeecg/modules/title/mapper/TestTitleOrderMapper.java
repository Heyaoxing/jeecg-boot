package org.jeecg.modules.title.mapper;

import java.util.List;
import org.jeecg.modules.title.entity.TestTitleOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 订单标题
 * @Author: jeecg-boot
 * @Date:   2019-06-14
 * @Version: V1.0
 */
public interface TestTitleOrderMapper extends BaseMapper<TestTitleOrder> {

	public boolean deleteByMainId(String mainId);
    
	public List<TestTitleOrder> selectByMainId(String mainId);
}
