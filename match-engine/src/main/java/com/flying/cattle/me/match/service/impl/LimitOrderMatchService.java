/**
 * @filename: LimitOrderMatchService.java 2019年12月4日
 * @project match-engine  V1.0
 * Copyright(c) 2020 FlyCattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.match.service.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flying.cattle.me.entity.MatchOrder;
import com.flying.cattle.me.enums.OrderType;
import com.flying.cattle.me.match.MatchExecutor;
import com.flying.cattle.me.match.factory.MatchStrategyFactory;
import com.flying.cattle.me.match.service.OrderMatchService;

/**
 * @ClassName: LimitOrderMatchService
 * @Description: TODO(限价撮合服务)
 * @author flying-cattle
 * @date 2019年12月4日
 */
@Service
public class LimitOrderMatchService implements OrderMatchService, InitializingBean {

	@Autowired
	MatchExecutor matchExecutors;
	
	@Override
	public MatchOrder startMatch(MatchOrder order) {
		return matchExecutors.doMatch(order);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		MatchStrategyFactory.register(OrderType.LIMIT, this);
	}
}
