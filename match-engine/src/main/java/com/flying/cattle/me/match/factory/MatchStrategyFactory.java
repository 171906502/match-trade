/**
 * @filename: MatchStrategyFactory.java 2019年12月4日
 * @project match-engine  V1.0
 * Copyright(c) 2020 FlyCattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.match.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.Assert;
import com.flying.cattle.me.enums.OrderType;
import com.flying.cattle.me.match.service.OrderMatchService;

/**
 * @ClassName: MatchStrategyFactory
 * @Description: TODO(撮合策略工厂)
 * @author flying-cattle
 * @date 2019年12月4日
 */
public class MatchStrategyFactory {

	private static Map<OrderType,OrderMatchService> services = new ConcurrentHashMap<OrderType,OrderMatchService>();

    public  static OrderMatchService getByOrderType(OrderType orderType){
        return services.get(orderType);
    }

    public static void register(OrderType orderType,OrderMatchService orderMatchService){
        Assert.notNull(orderType,"userType can't be null");
        services.put(orderType,orderMatchService);
    }
}
