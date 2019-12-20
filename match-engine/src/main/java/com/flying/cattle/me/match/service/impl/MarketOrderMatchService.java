/**
 * @filename: MarketOrderMatchService.java 2019年12月4日
 * @project match-engine  V1.0
 * Copyright(c) 2020 FlyCattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.match.service.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flying.cattle.me.entity.MatchOrder;
import com.flying.cattle.me.enums.OrderState;
import com.flying.cattle.me.enums.OrderType;
import com.flying.cattle.me.match.MatchExecutor;
import com.flying.cattle.me.match.factory.MatchStrategyFactory;
import com.flying.cattle.me.match.service.OrderMatchService;

/**
 * @ClassName: LimitOrderMatchService
 * @Description: TODO(市价撮合服务)
 * @author flying-cattle
 * @date 2019年12月4日
 */
@Service
public class MarketOrderMatchService implements OrderMatchService,InitializingBean{

	@Autowired
	MatchExecutor matchExecutors;
	
	@Override
	public MatchOrder startMatch(MatchOrder order) {
		//不同的订单，可以选择不同的撮合逻辑，和不同处理
		MatchOrder result = matchExecutors.doMatch(order);
		if (result.getState().intValue()!=OrderState.ALL.value) {
			//市价没吃完，直接撤销
			result.setState(OrderState.CANCEL.value);
		}
		return result;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		MatchStrategyFactory.register(OrderType.MARKET, this);
	}

}
