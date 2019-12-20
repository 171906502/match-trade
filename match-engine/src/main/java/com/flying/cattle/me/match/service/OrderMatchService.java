/**
 * @filename: OrderMatchService.java 2019年12月4日
 * @project match-engine  V1.0
 * Copyright(c) 2020 FlyCattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.match.service;

import com.flying.cattle.me.entity.MatchOrder;

/**
 * @ClassName: OrderMatchService
 * @Description: TODO(撮合服务)
 * @author flying-cattle
 * @date 2019年12月4日
 *
 */
public interface OrderMatchService {
 
	/**
	 * @Title: startMatch
	 * @Description: TODO(开始撮合)
	 * @param matchOrder 撮合订单
	 * @return MatchOrder 撮合订单
	 * @throws
	 */
	MatchOrder startMatch(MatchOrder matchOrder);
}
