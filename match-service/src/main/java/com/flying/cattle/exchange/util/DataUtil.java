/**
 * @filename: DataUtil.java 2019年12月19日
 * @project exchange  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.util;

import java.math.BigDecimal;
import java.util.Date;

import com.flying.cattle.exchange.entity.Order;
import com.flying.cattle.exchange.model.OrderParam;

/**
 * @ClassName: DataUtil
 * @Description: TODO(数据处理工具)
 * @author flying-cattle
 * @date 2019年12月19日
 */
public class DataUtil {

	/**
	 * @Title: paramToOrder
	 * @Description: TODO(添加等等参数转order)
	 * @param  OrderParam entity
	 * @return Order
	 * @throws
	 */
	public static Order paramToOrder(OrderParam entity) {
		Order order = new Order();
		order.setIsBuy(entity.getIsBuy());
		order.setCoinTeam(entity.getCoinTeam());
		order.setNumber(entity.getNumber());
		order.setPrice(entity.getPrice());
		if (entity.getIsMarket()) {
			order.setTotalPrice(entity.getTotal());
		}else {
			order.setTotalPrice(order.getPrice().multiply(order.getNumber()));
		}
		order.setFinishNumber(BigDecimal.ZERO);
		order.setUnFinishNumber(entity.getNumber());
		order.setIsMarket(entity.getIsMarket());
		order.setState(0);
		order.setCreateTime(new Date());
		if (order.getIsBuy()) {
			order.setSurplusFrozen(order.getTotalPrice());//剩余冻结
		}else {
			order.setSurplusFrozen(order.getUnFinishNumber());//剩余冻结
		}
		return order;
	}
}
