package org.jeecg.modules.title.service;

import org.jeecg.modules.title.entity.TestTitleOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 订单标题
 * @Author: jeecg-boot
 * @Date:   2019-06-14
 * @Version: V1.0
 */
public interface ITestTitleOrderService extends IService<TestTitleOrder> {

	public List<TestTitleOrder> selectByMainId(String mainId);
}
