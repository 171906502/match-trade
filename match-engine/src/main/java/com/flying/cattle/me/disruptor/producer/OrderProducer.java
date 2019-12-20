package com.flying.cattle.me.disruptor.producer;

import org.springframework.beans.BeanUtils;

import com.flying.cattle.me.entity.MatchOrder;
import com.lmax.disruptor.RingBuffer;

public class OrderProducer {

	private final RingBuffer<MatchOrder> ringBuffer;

	public OrderProducer(RingBuffer<MatchOrder> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public void onData(MatchOrder input) {
		long sequence = ringBuffer.next();
		try {
			MatchOrder order = ringBuffer.get(sequence);
			BeanUtils.copyProperties(input, order);
		} finally {
			ringBuffer.publish(sequence);
		}
	}
}
