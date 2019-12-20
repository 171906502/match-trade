/**
 * @filename: MatchEchoController.java 2019年12月20日
 * @project exchange  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.flying.cattle.exchange.model.PushDepth;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: MatchEchoController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author flying-cattle
 * @date 2019年12月20日
 */
@RestController
@Slf4j
public class MatchEchoController {

	String pusb_depth = "";
	
	/**
	 * @Title: pusb_depth
	 * @Description: TODO(盘口深度数据)
	 * @param  echo
	 * @return void 返回类型
	 * @throws
	 */
	@KafkaListener(id = "push_depth", topics = "push_depth")
	public void pusb_depth(String echo) {
		if (!pusb_depth.equals(echo)) {
			log.info("===深度数据："+echo);
			pusb_depth = echo;
		}
		PushDepth pd = JSON.parseObject(echo, PushDepth.class);
		pd.getBuy();
	}
	
	/**
	 * @Title: pusb_depth
	 * @Description: TODO(订单变化)
	 * @param  echo
	 * @return void 返回类型
	 * @throws
	 */
	@KafkaListener(id = "update_order", topics = "update_order")
	public void update_order(String echo) {
		log.info("---订单变化："+echo);
	}
	

	/**
	 * @Title: pusb_depth
	 * @Description: TODO(新的交易记录)
	 * @return void 返回类型
	 * @throws
	 */
	@KafkaListener(id = "new_tarde", topics = "new_tarde")
	public void new_tarde(String echo) {
		log.info("~~~交易信息："+echo);
	}
}
