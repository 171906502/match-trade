package com.flying.cattle.me.disruptor.handler;

import com.flying.cattle.me.entity.MatchOrder;
import com.flying.cattle.me.enums.OrderType;
import com.flying.cattle.me.match.factory.MatchStrategyFactory;
import com.flying.cattle.me.match.service.OrderMatchService;
import com.lmax.disruptor.WorkHandler;

public class MatchHandler implements WorkHandler<MatchOrder>{

	@Override
	public void onEvent(MatchOrder order) throws Exception {
		// 撮合处理器，线性消费
		OrderType orderType = order.getIsMarket()?OrderType.MARKET:OrderType.LIMIT;
		OrderMatchService service = MatchStrategyFactory.getByOrderType(orderType);
		service.startMatch(order);
	}
}
