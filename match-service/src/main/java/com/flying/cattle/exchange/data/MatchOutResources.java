/**
 * @filename: MatchOutResources.java 2019年12月20日
 * @project exchange  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.data;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.flying.cattle.exchange.model.PushDepth;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: MatchOutResources
 * @Description: 撮合输出数据源
 * @author flying-cattle
 * @date 2019年12月20日
 */
@RestController
@Slf4j
public class MatchOutResources {

	String push_depth = "";
	
	/**
	 * @Title: push_depth
	 * @Description: TODO(盘口深度数据)
	 * @param  echo
	 * @return void 返回类型
	 * @throws
	 */
	@KafkaListener(id = "push_depth", topics = "push_depth")
	public void push_depth(String echo) {
		if (!push_depth.equals(echo)) {
			log.info("===深度数据："+echo);
			push_depth = echo;
		}
		PushDepth pd = JSON.parseObject(echo, PushDepth.class);
		pd.getBuy();
	}
	
	/**
	 * @Title: push_depth
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
	 * @Title: push_depth
	 * @Description: TODO(新的交易记录)
	 * @return void 返回类型
	 * @throws
	 */
	@KafkaListener(id = "new_trade", topics = "new_trade")
	public void new_trade(String echo) {
		log.info("~~~交易信息："+echo);
	}
}
