/**
 * @filename: Depth.java 2019-09-23
 * @project power-base  V1.0
 * Copyright(c) 2018 BianPeng Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.model;

import java.io.Serializable;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**   
 * Copyright: Copyright (c) 2019 
 * 
 * <p>说明： </P>
 * @version: V1.0
 * @author: BianPeng
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Depth implements Serializable{
	
	private static final long serialVersionUID = 3591550780934377417L;

	//@ApiModelProperty(name = "price" , value = "单价")
	private String price;
	
	//@ApiModelProperty(name = "number" , value = "交易数量")
	private String number;
	
	//@ApiModelProperty(name = "total" , value = "累计")
	private String total;
	
	//@ApiModelProperty(name = "platform" , value = "平台")
	private Integer platform;
	
	//@ApiModelProperty(name = "coinTeam" , value = "交易队")
	private String coinTeam;
	
	//@ApiModelProperty(name = "isBuy" , value = "是否是买单")
	private Boolean isBuy;
	
	public String toJsonString() {
		return JSON.toJSONString(this);
	}
}
