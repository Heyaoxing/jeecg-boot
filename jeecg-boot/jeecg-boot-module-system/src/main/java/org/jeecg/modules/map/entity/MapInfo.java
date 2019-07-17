package org.jeecg.modules.map.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 基础地图
 * @Author: jeecg-boot
 * @Date:   2019-07-10
 * @Version: V1.0
 */
@Data
@TableName("map_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="map_info对象", description="基础地图")
public class MapInfo {
    
	/**id*/
	@TableId(type = IdType.UUID)
	private java.lang.String id;
	/**x轴*/
	@Excel(name = "x轴", width = 15)
    @ApiModelProperty(value = "x轴")
	private java.lang.Integer xaxis;
	/**y轴*/
	@Excel(name = "y轴", width = 15)
    @ApiModelProperty(value = "y轴")
	private java.lang.Integer yaxis;
	/**地质类型 1海洋 2陆地*/
	@Excel(name = "地质类型 1海洋 2陆地", width = 15)
    @ApiModelProperty(value = "地质类型 1海洋 2陆地")
	private java.lang.Integer geologyType;
	/**createBy*/
	@Excel(name = "createBy", width = 15)
    @ApiModelProperty(value = "createBy")
	private java.lang.String createBy;
	/**createTime*/
	@Excel(name = "createTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "createTime")
	private java.util.Date createTime;
	/**updateBy*/
	@Excel(name = "updateBy", width = 15)
    @ApiModelProperty(value = "updateBy")
	private java.lang.String updateBy;
	/**updateTime*/
	@Excel(name = "updateTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "updateTime")
	private java.util.Date updateTime;
}
