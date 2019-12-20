/**
 * @filename: PushDepth.java 2019年12月20日
 * @project exchange  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: PushDepth
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author flying-cattle
 * @date 2019年12月20日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushDepth {
	
	List<Depth> buy;
	
	List<Depth> sell;
}
