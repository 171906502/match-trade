/**
 * @filename: Order.java 2019-09-23
 * @project power-base  V1.0
 * Copyright(c) 2018 BianPeng Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.DecimalMin;
import org.springframework.format.annotation.DateTimeFormat;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copyright: Copyright (c) 2019
 * 
 * <p>
 * 说明： 订单实体类
 * </P>
 * 
 * @version: V1.0
 * @author: BianPeng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

	private static final long serialVersionUID = 10000L;

	// @ApiModelProperty(name = "id" , value = "主键")
	private Long id;

	// @ApiModelProperty(name = "uid" , value = "用户ID")
	private Long uid;

	// @ApiModelProperty(name = "isBuy" , value = "是否是买单")
	private Boolean isBuy;

	// @ApiModelProperty(name = "isMarket" , value = "是市价交易")
	private Boolean isMarket;

	@DecimalMin("0.00")
	// @ApiModelProperty(name = "number" , value = "交易数量")
	private BigDecimal number;

	@DecimalMin("0.00")
	// @ApiModelProperty(name = "price" , value = "单价")
	private BigDecimal price;

	@DecimalMin("0.0000")
	// @ApiModelProperty(name = "totalPrice" , value = "总价")
	private BigDecimal totalPrice;

	@DecimalMin("0.00")
	// @ApiModelProperty(name = "finishNumber" , value = "已完成数量")
	private BigDecimal finishNumber;

	@DecimalMin("0.00")
	// @ApiModelProperty(name = "unFinishNumber" , value = "未完成数量")
	private BigDecimal unFinishNumber;

	@DecimalMin("0.0000")
	// @ApiModelProperty(name = "surplusFrozen" , value = "剩余冻结")
	private BigDecimal surplusFrozen;

	// @ApiModelProperty(name = "priority" , value = "优先级（越大越优先）")
	private Integer priority;

	// @ApiModelProperty(name = "state" , value =
	// "状态（0挂单，1部分成交，2全部成交，3已撤销，4已结算，其他异常）")
	private Integer state;

	// @ApiModelProperty(name = "decimalNumber" , value = "数量的小数位数，其他以传入为准")
	private Integer decimalNumber;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	// @ApiModelProperty(name = "alterTime" , value = "修改时间")
	private Date alterTime;

	// @ApiModelProperty(name = "coinTeam" , value = "交易队")
	private String coinTeam;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	// @ApiModelProperty(name = "createTime" , value = "创建时间")
	private Date createTime;

	// @ApiModelProperty(name = "dealWay" , value = "成交方式（taker市价，maker限价）")
	private String dealWay;

	// @ApiModelProperty(name = "remark" , value = "备用字段JSON格式")
	private String remark;

	public String toJsonString() {
		return JSON.toJSONString(this);
	}
}
