/**
 * @filename: OrderMatchInputResources.java 2019年12月19日
 * @project exchange  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.data;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flying.cattle.exchange.entity.Order;
import com.flying.cattle.exchange.model.CancelOrderParam;
import com.flying.cattle.exchange.model.JsonResult;
import com.flying.cattle.exchange.model.OrderParam;
import com.flying.cattle.exchange.util.DataUtil;
import com.flying.cattle.exchange.util.SnowflakeIdWorker;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: OrderMatchInputResources
 * @Description: 撮合订单输入数据源
 * @author flying-cattle
 * @date 2019年12月19日
 */
@Slf4j
@RestController
@RequestMapping("/exchange/order")
@Api(value = "订单服务", tags = {"订单服务"})
public class OrderMatchInputResources {
	
	@Autowired
    private MessageSource messageSource;
	
	@Autowired
	private KafkaTemplate<String, String> template;
	
	/**
	 * @Title: addNewOrder
	 * @Description: TODO(添加新的订单)
	 * @param  参数
	 * @return void 返回类型
	 * @throws
	 */
	@PostMapping("/addNewOrder")
	public JsonResult<Order> addNewOrder(@RequestBody @Valid OrderParam param, BindingResult result) {
		try {
			JsonResult<Order> res=new JsonResult<Order>();
			if (result.hasErrors()) { //参数校验失败
				return res.error(result, messageSource);
			}
			Order order = DataUtil.paramToOrder(param);
			order.setId(SnowflakeIdWorker.generateId());
			order.setUid(SnowflakeIdWorker.generateId());
			//关联用户，资产校验，或其他校验
			
			//校验完成推送消息
			template.send("new_order", order.toJsonString());
			return res.success(order);
		} catch (Exception e) {
			log.error("添加新的订单错误："+e);
			e.printStackTrace();
			return new JsonResult<Order>(e);
		}
	}
	

	/**
	 * @Title: addNewOrder
	 * @Description: TODO(添加新的订单)
	 * @param  参数
	 * @return void 返回类型
	 * @throws
	 */
	@PostMapping("/cancelOrder")
	public JsonResult<Object> cancelOrder(@RequestBody @Valid CancelOrderParam param, BindingResult result) {
		try {
			JsonResult<Object> res=new JsonResult<Object>();
			if (result.hasErrors()) { //参数校验失败
				return res.error(result, messageSource);
			}
			//查看是否是可以撤销的状态
			template.send("cancel_order", param.toJsonString());
			return res.success("操作成功！");
		} catch (Exception e) {
			log.error("添加新的订单错误："+e);
			e.printStackTrace();
			return new JsonResult<Object>(e);
		}
	}
}
