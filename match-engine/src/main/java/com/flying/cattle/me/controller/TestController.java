package com.flying.cattle.me.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.flying.cattle.me.disruptor.producer.OrderProducer;
import com.flying.cattle.me.entity.Depth;
import com.flying.cattle.me.entity.MatchOrder;
import com.flying.cattle.me.util.HazelcastUtil;
import com.flying.cattle.me.util.SnowflakeIdWorker;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.lmax.disruptor.RingBuffer;

import reactor.core.publisher.Mono;

//@Profile("local")
@Controller
@RequestMapping("/test")
public class TestController {
	@Autowired
	RingBuffer<MatchOrder> ringBuffer;

	@Autowired
	HazelcastInstance hzInstance;

	@GetMapping("/addOne")
	@ResponseBody
	public Boolean addOrderOne(boolean isBuy, BigDecimal price, BigDecimal number) {
		OrderProducer producer = new OrderProducer(ringBuffer);
		MatchOrder or = new MatchOrder();
		or.setId(SnowflakeIdWorker.generateId());
		or.setUid(SnowflakeIdWorker.generateId());
		or.setIsBuy(isBuy);
		or.setIsMarket(false);
		or.setPrice(price);
		or.setNumber(number);
		or.setState(0);
		or.setTotalPrice(price.multiply(number));
		or.setDecimalNumber(2);
		or.setUnFinishNumber(number);
		or.setFinishNumber(BigDecimal.ZERO);
		or.setCreateTime(new Date());
		or.setCoinTeam("XBIT-USDT");
		if (isBuy) {
			or.setSurplusFrozen(number.multiply(price));
		} else {
			or.setSurplusFrozen(number);
		}
		producer.onData(or);
		return true;
	}

	@GetMapping("/getDepth")
	@ResponseBody
	public Map<String, List<Depth>> addOrderOne() {
		// 获取对手盘口
		IMap<BigDecimal, BigDecimal> buyMap = hzInstance.getMap(HazelcastUtil.getMatchKey("XBIT-USDT", Boolean.TRUE));
		List<Depth> buyList = new ArrayList<Depth>();
		List<Depth> sellList = new ArrayList<Depth>();
		if (buyMap.size() > 0) {
			buyList = buyMap.entrySet().stream().sorted(Entry.<BigDecimal, BigDecimal>comparingByKey().reversed())
					.map(obj -> new Depth(obj.getKey().toString(), obj.getValue().toString(), obj.getValue().toString(),
							1, "XBIT-USDT", Boolean.TRUE))
					.collect(Collectors.toList());
		} else {
			Depth depth = new Depth("0.00", "0.0000", "0.0000", 1, "XBIT-USDT", Boolean.TRUE);
			buyList.add(depth);
		}

		IMap<BigDecimal, BigDecimal> sellMap = hzInstance.getMap(HazelcastUtil.getMatchKey("XBIT-USDT", Boolean.FALSE));
		if (sellMap.size() > 0) {
			sellList = sellMap.entrySet().stream().sorted(Entry.<BigDecimal, BigDecimal>comparingByKey())
					.map(obj -> new Depth(obj.getKey().toString(), obj.getValue().toString(), obj.getValue().toString(),
							1, "XBIT-USDT", Boolean.TRUE))
					.collect(Collectors.toList());
		} else {
			Depth depth = new Depth("0.00", "0.0000", "0.0000", 1, "XBIT-USDT", Boolean.FALSE);
			sellList.add(depth);
		}
		// 过大不全部传
		if (buyList.size() > 60) {
			buyList.subList(0, 60);
		}
		if (sellList.size() > 60) {
			sellList.subList(0, 60);
		}
		Map<String, List<Depth>> map = new HashMap<String, List<Depth>>();
		map.put("BUY", buyList);
		map.put("SELL", sellList);
		return map;
	}

	@GetMapping("/index")
	public Mono<String> index(final Model model) {
		String path = "index";
		return Mono.create(monoSink -> monoSink.success(path));
	}
}
