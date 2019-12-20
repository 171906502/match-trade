/**
 * @filename: OrderData.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.data.input;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.flying.cattle.me.data.out.PushData;
import com.flying.cattle.me.disruptor.producer.OrderProducer;
import com.flying.cattle.me.entity.CancelOrderParam;
import com.flying.cattle.me.entity.MatchOrder;
import com.flying.cattle.me.util.HazelcastUtil;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;
import com.lmax.disruptor.RingBuffer;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: OrderData
 * @Description: TODO(订单数据请求)
 * @author flying-cattle
 * @date 2019年12月19日
 */
@Component
@Slf4j
public class OrderData {
	
	@Autowired
	RingBuffer<MatchOrder> ringBuffer;
	
	@Autowired
	HazelcastInstance hzInstance;
	
	@Autowired
	PushData pushData;
	/**
	 * @Title: new_order
	 * @Description: TODO(接收委托订单数据)
	 * @param  参数
	 * @return void 返回类型
	 * @throws
	 */
	@KafkaListener(id = "new_order", topics = "new_order")
	public void new_order(String param) {
		log.info("===收到new_order:"+param);
		OrderProducer producer = new OrderProducer(ringBuffer);
		MatchOrder order = JSON.parseObject(param, MatchOrder.class);
		producer.onData(order);
	}
	
	/**
	 * @Title: new_order
	 * @Description: TODO(接收撤销订单数据)
	 * @param  参数
	 * @return void 返回类型
	 * @throws
	 */
	@KafkaListener(id = "cancel_order", topics = "cancel_order")
	public void cancel_order(String param) {
		log.info("===收到cancel_order:"+param);
		CancelOrderParam cancel = JSON.parseObject(param, CancelOrderParam.class);
		IMap<Long, MatchOrder> order_map = hzInstance.getMap(HazelcastUtil.getOrderBookKey(cancel.getCoinTeam(), cancel.getIsBuy()));
		if (order_map.containsKey(cancel.getId())) {
			TransactionOptions options = new TransactionOptions().setTransactionType(TransactionOptions.TransactionType.ONE_PHASE);
			TransactionContext context = hzInstance.newTransactionContext(options);
			context.beginTransaction();
			try {
				IMap<BigDecimal, BigDecimal> map = hzInstance.getMap(HazelcastUtil.getMatchKey(cancel.getCoinTeam(), cancel.getIsBuy()));
				MatchOrder cmo = order_map.remove(cancel.getId());
				map.compute(cmo.getPrice(), (k,v) -> v.subtract(cmo.getUnFinishNumber()));
				if (map.get(cmo.getPrice()).compareTo(BigDecimal.ZERO) >-1) {
					context.commitTransaction();
					pushData.updateOrder(cmo); //推送撤销成功结果
				}else {
					throw new Exception();
				}
			} catch (Exception e) {
				context.rollbackTransaction();
			}
		}
	}
}
