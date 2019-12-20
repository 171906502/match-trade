package com.flying.cattle.me.disruptor.factory;

import com.flying.cattle.me.entity.MatchOrder;
import com.lmax.disruptor.EventFactory;

public class OrderFactory implements EventFactory<MatchOrder>{

	@Override
	public MatchOrder newInstance() {
		return new MatchOrder();
	}

}
