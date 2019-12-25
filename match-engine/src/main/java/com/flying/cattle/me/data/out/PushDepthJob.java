/**
 * @filename: PushDetphJob.java 2019-12-13
 * @project power-web  V1.0
 * Copyright(c) 2018 BianPeng Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.data.out;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.flying.cattle.me.entity.Depth;
import com.flying.cattle.me.util.HazelcastUtil;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import lombok.extern.slf4j.Slf4j;

/**
 * Copyright: Copyright (c) 2019
 * 
 * <p>
 * 说明：深度推送
 * </P>
 * 
 * @version: V1.0
 * @author: BianPeng
 * 
 */
@Component
@EnableScheduling
@Slf4j
public class PushDepthJob {

	@Autowired
	public HazelcastInstance hzInstance;

	@Autowired
	private KafkaTemplate<String, String> template;

	/**
	 * -推送深度
	 */
	@Scheduled(fixedDelay = 1000)
	public void pushDepth() {
		try {
			// XBIT-USDT 买盘
			List<Depth> buyList = this.getMarketDepth("XBIT-USDT", Boolean.TRUE);
			// XBIT-USDT 卖盘
			List<Depth> sellList = this.getMarketDepth("XBIT-USDT", Boolean.FALSE);
			// 盘口过大处理
			if (buyList.size() > 100) {
				buyList.subList(0, 100);
			}
			if (sellList.size() > 100) {
				sellList.subList(0, 100);
			}
			// 发送数据处理
			Map<String, List<Depth>> map = new HashMap<String, List<Depth>>();
			map.put("buy", buyList);
			map.put("sell", sellList);
			// 推送深度
			template.send("push_depth", JSON.toJSONString(map));
		} catch (Exception e) {
			log.error("深度数据处理错误：" + e);
			e.printStackTrace();
		}
	}

	/**
	 * -★ -获取行情深度
	 * 
	 * @param coinTeam 交易队
	 * @param isBuy    是否是买
	 * @return List<Depth>
	 */
	public List<Depth> getMarketDepth(String coinTeam, Boolean isBuy) {
		// XBIT-USDT 买盘
		IMap<BigDecimal, BigDecimal> buyMap = hzInstance.getMap(HazelcastUtil.getMatchKey(coinTeam, isBuy));
		List<Depth> depths = new ArrayList<Depth>();
		if (buyMap.size() > 0) {
			List<Depth> list = new ArrayList<Depth>();
			if (isBuy) {
				list = buyMap.entrySet().stream().sorted(Entry.<BigDecimal, BigDecimal>comparingByKey().reversed())
						.map(obj -> new Depth(obj.getKey().toString(), obj.getValue().toString(),obj.getValue().toString(), 1, coinTeam, isBuy))
						.collect(Collectors.toList());
			}else {
				list = buyMap.entrySet().stream().sorted(Entry.<BigDecimal, BigDecimal>comparingByKey())
						.map(obj -> new Depth(obj.getKey().toString(), obj.getValue().toString(),obj.getValue().toString(), 1, coinTeam, isBuy))
						.collect(Collectors.toList());
			}
			list.stream().reduce(new Depth("0", "0", "0", 1, coinTeam, isBuy), (one, two) -> {
				one.setTotal((new BigDecimal(one.getTotal()).add(new BigDecimal(two.getNumber()))).toString());
				depths.add(new Depth(two.getPrice(), two.getNumber(), one.getTotal(), two.getPlatform(),two.getCoinTeam(), two.getIsBuy()));
				return one;
			});
		} else {
			Depth depth = new Depth("0.00", "0.0000", "0.0000", 1, coinTeam, isBuy);
			depths.add(depth);
		}
		return depths;
	}
}
