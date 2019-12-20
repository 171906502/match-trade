package com.flying.cattle.me.disruptor;

import java.util.concurrent.ThreadFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.flying.cattle.me.config.HazelcastConfig;
import com.flying.cattle.me.disruptor.exception.MyHandlerException;
import com.flying.cattle.me.disruptor.factory.OrderFactory;
import com.flying.cattle.me.disruptor.handler.InputDepthHandler;
import com.flying.cattle.me.disruptor.handler.MatchHandler;
import com.flying.cattle.me.disruptor.handler.OutDepthHandler;
import com.flying.cattle.me.entity.MatchOrder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

@Configuration
@ConditionalOnBean(HazelcastConfig.class)
public class DisruptorConfig {

	@Bean
	public RingBuffer<MatchOrder> ringBuffer() {
		EventFactory<MatchOrder> factory = new OrderFactory();
		int ringBufferSize = 1024 * 1024;
		ThreadFactory disruptorThreadPool = new ThreadFactoryBuilder().setNameFormat("DisruptorThreadPool").build();
		Disruptor<MatchOrder> disruptor = new Disruptor<MatchOrder>(factory, ringBufferSize, disruptorThreadPool,
				ProducerType.MULTI, new YieldingWaitStrategy());
		disruptor.setDefaultExceptionHandler(new MyHandlerException());// Disruptor异常统计
		// 单线处理撮合, 并行处理盘口和订单薄
		disruptor.handleEventsWithWorkerPool(new MatchHandler(),new MatchHandler()).then(new InputDepthHandler(),new OutDepthHandler());
		disruptor.start();
		return disruptor.getRingBuffer();
	}
}
