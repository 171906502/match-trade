package com.flying.cattle.me.match;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.flying.cattle.me.data.out.PushData;
import com.flying.cattle.me.entity.LevelMatch;
import com.flying.cattle.me.entity.MatchOrder;
import com.flying.cattle.me.entity.Trade;
import com.flying.cattle.me.enums.DealWay;
import com.flying.cattle.me.enums.OrderState;
import com.flying.cattle.me.util.HazelcastUtil;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

import lombok.extern.slf4j.Slf4j;

@Component
@EnableAsync
@Slf4j
public class MatchDetailHandler {

	@Autowired
	HazelcastInstance hzInstance;

	@Autowired
	ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

	@Autowired
	PushData pushData;
	/**
	 * -发送记录，并记录到数据库
	 * 
	 * @param order   交易订单
	 * @param price   成交价格
	 * @param number  成交数量
	 * @param dealWay 交易方式
	 */
	@Async
	public void sendTradeRecord(MatchOrder order, BigDecimal price, BigDecimal number, DealWay dealWay) {
		Trade tarde = new Trade(null, order.getUid(), order.getId(), order.getIsBuy(), number, price,
				number.multiply(price), order.getCoinTeam(), null, null, dealWay.value, null);
		pushData.addTrade(tarde);
	
	}

	/**
	 * -推送订单变化
	 * 
	 * @param order 变化后的订单对象
	 */
	public void sendOrderChange(MatchOrder order) {
		pushData.updateOrder(order);
	}

	/**
	 * @Title: inputMatchDepth 有事务处理
	 * @Description: TODO(买入队列)
	 * @param  input 入单
	 * @return void 返回类型
	 * @throws
	 */
	public void inputMatchDepth(MatchOrder input) {
//		TransactionOptions options = new TransactionOptions().setTransactionType(TransactionOptions.TransactionType.ONE_PHASE);
//		TransactionContext context = hzInstance.newTransactionContext(options);
//		context.beginTransaction();
		try {
			IMap<BigDecimal, BigDecimal> map = hzInstance.getMap(HazelcastUtil.getMatchKey(input.getCoinTeam(), input.getIsBuy()));
			IMap<Long, MatchOrder> order_map = hzInstance.getMap(HazelcastUtil.getOrderBookKey(input.getCoinTeam(), input.getIsBuy()));
			map.compute(input.getPrice(),(k, v) -> HazelcastUtil.numberAdd(v, input.getUnFinishNumber()));
			input.setList(null);//清空吃单记录，减小内存。
			order_map.put(input.getId(), input);
			//context.commitTransaction();//提交事务
		} catch (Exception e) {
			//context.rollbackTransaction();
			log.error("===入单数据处理异常,数据原型："+input.toJsonString()+"   本次异常："+e);
		}
	}
	
	/**
	 * @Title: outMatchDepth 保证了原子操作，无需事务
	 * @Description: TODO(out订单处理)
	 * @param @param order 入单
	 * @return void 返回类型
	 * @throws
	 */
	@Async
	public void outMatchDepth(MatchOrder order) {
		List<LevelMatch> list = order.getList();
		try {
			if (null!=list&&list.size()>0) {
				Iterator<LevelMatch> itr = list.iterator();
				while (itr.hasNext()){
					LevelMatch lm = itr.next();
					itr.remove();
					BigDecimal dealNumber = lm.getNumber();
					while (dealNumber.compareTo(BigDecimal.ZERO)>0) {
						//对手盘
						IMap<Long, MatchOrder> order_map = hzInstance.getMap(HazelcastUtil.getOrderBookKey(order.getCoinTeam(), !order.getIsBuy()));
						@SuppressWarnings("rawtypes")
						Predicate pricePredicate = Predicates.equal("price", lm.getPrice());
						Collection<MatchOrder> orders = order_map.values(pricePredicate);
						for (MatchOrder mor : orders) {
							MatchOrder out = order_map.remove(mor.getId());
							if (null!=out) {
								int cpr = dealNumber.compareTo(out.getUnFinishNumber());
								if (cpr>0) {
									dealNumber=dealNumber.subtract(out.getUnFinishNumber());
									this.updateOutOder(out, OrderState.ALL, out.getUnFinishNumber());
								}else if (cpr==0) {
									this.updateOutOder(out, OrderState.ALL, dealNumber);
									dealNumber = BigDecimal.ZERO;
									break;
								}else {
									out = this.updateOutOder(out, OrderState.PART, dealNumber);
									order_map.put(out.getId(), out);
									dealNumber = BigDecimal.ZERO;
									break;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("===出单数据处理异常,数据原型："+order.toJsonString()+"   本次异常："+e);
		}
	}
	/**
	 * @Title: updateOutOder
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param  out 出订单
	 * @param  dealNumber 交易数量
	 * @return void 返回类型
	 * @throws
	 */
	private MatchOrder updateOutOder(MatchOrder out,OrderState orderState ,BigDecimal dealNumber) {
		out.setState(orderState.value);
		out.setFinishNumber(out.getFinishNumber().add(dealNumber));
		out.setUnFinishNumber(out.getUnFinishNumber().subtract(dealNumber));
		if (out.getIsBuy()) {
			out.setSurplusFrozen(out.getSurplusFrozen().subtract(dealNumber.multiply(out.getPrice())));
		}else {
			out.setSurplusFrozen(out.getSurplusFrozen().subtract(dealNumber));
		}
		this.sendOrderChange(out);
		this.sendTradeRecord(out, out.getPrice(), dealNumber, DealWay.MAKER);
		return out;
	}
}
