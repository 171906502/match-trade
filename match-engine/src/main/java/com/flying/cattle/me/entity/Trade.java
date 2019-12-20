/**
 * @filename:Trade 2019年9月24日
 * @project BuyBit  V1.0
 * Copyright(c) 2020 BianPeng Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.entity;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 自动生成工具：mybatis-dsc-generator
 * </p>
 * 
 * <p>
 * 说明： 交易记录实体类
 * </P>
 * 
 * @version: V1.0
 * @author: BianPeng
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Trade implements Serializable {

	private static final long serialVersionUID = 10001L;

	//@ApiModelProperty(name = "id", value = "ID")
	private Long id;

	//@ApiModelProperty(name = "uid", value = "用户ID")
	private Long uid;

	//@ApiModelProperty(name = "oid", value = "订单ID")
	private Long oid;

	//@ApiModelProperty(name = "isBuy", value = "是买交易")
	private Boolean isBuy;

	//@ApiModelProperty(name = "number", value = "交易数量")
	private BigDecimal number;

	//@ApiModelProperty(name = "price", value = "实际成交价")
	private BigDecimal price;

	//@ApiModelProperty(name = "totalPrice", value = "成交总价")
	private BigDecimal totalPrice;

	//@ApiModelProperty(name = "coinTeam", value = "交易队")
	private String coinTeam;

	//@ApiModelProperty(name = "poundage", value = "手续费")
	private BigDecimal poundage;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	//@ApiModelProperty(name = "createTime", value = "成交时间")
	private Date createTime;

	//@ApiModelProperty(name = "dealWay" , value = "成交方式（taker市价，maker限价）")
    private String dealWay;
	
	//@ApiModelProperty(name = "remark", value = "备用字段<用法①委托均价>")
	private String remark;

	public String toJsonString() {
		return JSON.toJSONString(this);
	}
}
