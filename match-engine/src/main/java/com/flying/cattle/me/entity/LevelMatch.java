package com.flying.cattle.me.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.alibaba.fastjson.JSON;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LevelMatch implements Serializable {
	
	private static final long serialVersionUID = -4911741995736837242L;

	private BigDecimal price;
	
	private BigDecimal number;
	
	private Boolean eatUp;
	
	public String toJsonString() {
		return JSON.toJSONString(this);
	}



	public LevelMatch(BigDecimal price, BigDecimal number) {
		super();
		this.price = price;
		this.number = number;
	}
}
