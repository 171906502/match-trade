package com.flying.cattle.me.disruptor.handler;

import com.flying.cattle.me.entity.MatchOrder;
import com.flying.cattle.me.match.MatchDetailHandler;
import com.flying.cattle.me.util.SpringContextUtils;
import com.lmax.disruptor.EventHandler;

public class OutDepthHandler implements EventHandler<MatchOrder> {

	// EventHandler的方法 - 都消费
	public void onEvent(MatchOrder order, long sequence, boolean endOfBatch) throws Exception {
		//当订单是1或2时，说明吃过单，造成了出单深度变化
		if (order.getState().intValue()==1||order.getState().intValue()==2) {
			MatchDetailHandler matchDetailHandler = (MatchDetailHandler) SpringContextUtils.getBean("matchDetailHandler");
			matchDetailHandler.outMatchDepth(order);
		}
	}
}
