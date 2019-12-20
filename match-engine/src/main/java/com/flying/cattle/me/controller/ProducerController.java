/**
 * @filename: ProducerController.java 2019年12月18日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.flying.cattle.me.disruptor.producer.OrderProducer;
import com.flying.cattle.me.entity.MatchOrder;
import com.flying.cattle.me.util.SnowflakeIdWorker;
import com.hazelcast.core.HazelcastInstance;
import com.lmax.disruptor.RingBuffer;

/**
 * @ClassName: ProducerController
 * @Description: TODO(生产者实例)
 * @author flying-cattle
 * @date 2019年12月18日
 */
@Controller
@RequestMapping("/producer")
public class ProducerController {

	@Autowired
	RingBuffer<MatchOrder> ringBuffer;

	@Autowired
	HazelcastInstance hzInstance;

	@GetMapping("/one")
	@ResponseBody
	public String addOrder(long size) {
		long start = System.currentTimeMillis();
		for (long i = 0; i < size; i++) {
			OrderProducer producer = new OrderProducer(ringBuffer);
			long max = 100, min = 1;
			long num = (long) (Math.random() * (max - min) + min);
			MatchOrder or = new MatchOrder();
			or.setId(i);
			or.setUid(SnowflakeIdWorker.generateId());
			or.setIsBuy(true);
			or.setIsMarket(false);
			or.setPrice(new BigDecimal(num));
			or.setNumber(BigDecimal.ONE);
			or.setState(0);
			or.setTotalPrice(BigDecimal.ONE.multiply(new BigDecimal(num)));
			or.setDecimalNumber(2);
			or.setUnFinishNumber(BigDecimal.ONE);
			or.setFinishNumber(BigDecimal.ZERO);
			or.setCreateTime(new Date());
			or.setCoinTeam("XBIT-USDT");
			or.setSurplusFrozen(or.getTotalPrice());
			producer.onData(or);
		}
		MatchOrder or = new MatchOrder();
		or.setId(size + 1);
		or.setUid(SnowflakeIdWorker.generateId());
		or.setIsBuy(true);
		or.setIsMarket(false);
		or.setPrice(new BigDecimal(101));
		or.setNumber(BigDecimal.ONE);
		or.setState(0);
		or.setTotalPrice(BigDecimal.ONE.multiply(new BigDecimal(101)));
		or.setDecimalNumber(2);
		or.setUnFinishNumber(BigDecimal.ONE);
		or.setFinishNumber(BigDecimal.ZERO);
		or.setCreateTime(new Date());
		or.setCoinTeam("XBIT-USDT");
		or.setSurplusFrozen(or.getTotalPrice());
		new OrderProducer(ringBuffer).onData(or);
		long end = System.currentTimeMillis();
		return "===单生产者，本次添加：" + (size + 1) + "条数据，耗时：" + (end - start);
	}

	@GetMapping("/more")
	@ResponseBody
	public String newOrder(long size) {
		long start = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch(1);
		for (long i = 0; i < size; i++) {
			OrderProducer producer = new OrderProducer(ringBuffer);
			final long a = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					long max = 100, min = 1;
					long num = (long) (Math.random() * (max - min) + min);
					MatchOrder or = new MatchOrder();
					or.setId(a);
					or.setUid(SnowflakeIdWorker.generateId());
					or.setIsBuy(true);
					or.setIsMarket(false);
					or.setPrice(new BigDecimal(num));
					or.setNumber(BigDecimal.ONE);
					or.setState(0);
					or.setTotalPrice(BigDecimal.ONE.multiply(new BigDecimal(num)));
					or.setDecimalNumber(2);
					or.setUnFinishNumber(BigDecimal.ONE);
					or.setFinishNumber(BigDecimal.ZERO);
					or.setCreateTime(new Date());
					or.setCoinTeam("XBIT-USDT");
					or.setSurplusFrozen(or.getTotalPrice());
					try {
						latch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					producer.onData(or);
				}
			}).start();
		}
		MatchOrder or = new MatchOrder();
		or.setId(size + 1);
		or.setUid(SnowflakeIdWorker.generateId());
		or.setIsBuy(true);
		or.setIsMarket(false);
		or.setPrice(new BigDecimal(101));
		or.setNumber(BigDecimal.ONE);
		or.setState(0);
		or.setTotalPrice(BigDecimal.ONE.multiply(new BigDecimal(101)));
		or.setDecimalNumber(2);
		or.setUnFinishNumber(BigDecimal.ONE);
		or.setFinishNumber(BigDecimal.ZERO);
		or.setCreateTime(new Date());
		or.setCoinTeam("XBIT-USDT");
		or.setSurplusFrozen(or.getTotalPrice());
		new OrderProducer(ringBuffer).onData(or);
		long end = System.currentTimeMillis();
		return "===多生产者，本次添加：" + (size + 1) + "条数据，耗时：" + (end - start);
	}
	
	@GetMapping("/addOneMarketSell")
	@ResponseBody
	public Boolean addOneMarketSell(BigDecimal number) {
		OrderProducer producer = new OrderProducer(ringBuffer);
		MatchOrder or = new MatchOrder();
		or.setId(SnowflakeIdWorker.generateId());
		or.setUid(SnowflakeIdWorker.generateId());
		or.setIsBuy(false);
		or.setIsMarket(true);
		or.setNumber(number);
		or.setState(0);
		or.setDecimalNumber(2);
		or.setUnFinishNumber(number);
		or.setFinishNumber(BigDecimal.ZERO);
		or.setCreateTime(new Date());
		or.setCoinTeam("XBIT-USDT");
		or.setSurplusFrozen(number);
		producer.onData(or);
		System.err.println("===开始撮合：" + System.currentTimeMillis());
		return true;
	}
}
