package com.flying.cattle.exchange.model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.JSON;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderParam implements Serializable{
	
	private static final long serialVersionUID = -9200646942759105003L;

	// 是买还是卖
	@ApiModelProperty(name = "isBuy" , value = "是买挂单")
	@NotNull(message = "buy不能为空")
	private Boolean isBuy;
	
	// 交易的价格
	@ApiModelProperty(name = "price" , value = "买单价")
	@Digits(integer=10, fraction=2)
	@DecimalMin("0.00")
	private BigDecimal price;

	// 交易的数量
	@ApiModelProperty(name = "number" , value = "买数量")
	@Digits(integer=10, fraction=4)
	@DecimalMin("0.0000")
	private BigDecimal number;
	
	@ApiModelProperty(name = "total" , value = "买总价")
	@Digits(integer=10, fraction=6)
	@DecimalMin("0.000000")
	private BigDecimal total;
	
	// 交易类型（用USDT-BTC）
	@ApiModelProperty(name = "coinTeam" , value = "交易队")
	@NotNull(message = "coinTeam不能为空")
	private String coinTeam;
	
	// 是市价交易
	@NotNull(message = "是否为市价交易不能为空!")
	@ApiModelProperty(name = "isMarket" , value = "是市价交易")
	private Boolean isMarket;
	
	public String toJsonString() {
		return JSON.toJSONString(this);
	}
}
