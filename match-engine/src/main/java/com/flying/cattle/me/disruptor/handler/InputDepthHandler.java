package com.flying.cattle.me.disruptor.handler;

import com.flying.cattle.me.entity.MatchOrder;
import com.flying.cattle.me.match.MatchDetailHandler;
import com.flying.cattle.me.util.SpringContextUtils;
import com.lmax.disruptor.EventHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InputDepthHandler implements EventHandler<MatchOrder> {

	// EventHandler的方法 - 都消费
	public void onEvent(MatchOrder order, long sequence, boolean endOfBatch) throws Exception {
		try {
			MatchDetailHandler matchDetailHandler = (MatchDetailHandler) SpringContextUtils.getBean("matchDetailHandler");
			if (order.getState().intValue()!=0) {
				matchDetailHandler.sendOrderChange(order);
			}
			//当状态大于1时，不造成深度变化
			if (order.getState().intValue()==0||order.getState().intValue()==1) {
				matchDetailHandler.inputMatchDepth(order);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("===入单消费错误："+e);
		}
	}
}
