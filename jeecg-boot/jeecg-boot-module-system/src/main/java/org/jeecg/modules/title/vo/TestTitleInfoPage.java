package org.jeecg.modules.title.vo;

import java.util.List;
import org.jeecg.modules.title.entity.TestTitleInfo;
import org.jeecg.modules.title.entity.TestUserInfo;
import org.jeecg.modules.title.entity.TestTitleOrder;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

/**
 * @Description: 标题
 * @Author: jeecg-boot
 * @Date:   2019-06-14
 * @Version: V1.0
 */
@Data
public class TestTitleInfoPage {
	
	/**id*/
	private String id;
	/**标题*/
  	@Excel(name = "标题", width = 15)
	private String title;
	/**createBy*/
  	@Excel(name = "createBy", width = 15)
	private String createBy;
	/**createTime*/
  	@Excel(name = "createTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
  	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**updateBy*/
  	@Excel(name = "updateBy", width = 15)
	private String updateBy;
	/**updateTime*/
  	@Excel(name = "updateTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
  	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	
	@ExcelCollection(name="标题用户")
	private List<TestUserInfo> testUserInfoList;
	@ExcelCollection(name="订单标题")
	private List<TestTitleOrder> testTitleOrderList;
	
}
