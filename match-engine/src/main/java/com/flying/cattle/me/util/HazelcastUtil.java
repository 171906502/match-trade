/**
 * @filename: HazelcastUtil.java 2019-11-12
 * @project power-trade  V1.0
 * Copyright(c) 2020 BianPeng Co. Ltd.
 * All right reserved.
 */
package com.flying.cattle.me.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import com.flying.cattle.me.entity.MatchOrder;

/**
 * -说明：Hazelcast 相关静态方法工具类
 *
 * @version: V1.0
 * @author: flying-cattle
 */
public class HazelcastUtil {

	// 买单撮合集合
	private static final String MATCH_BID_MAP = "-MATCH-BID";
	// 卖单撮合集合
	private static final String MATCH_ASK_MAP = "-MATCH-ASK";

	// 买单1盘口map
	private static final String ORDER_BOOK_BID_MAP = "-ORDER-BOOK-BID";
	// 卖单1盘口map
	private static final String ORDER_BOOK_ASK_MAP = "-ORDER-BOOK-ASK";
	
	// 买单单个价格数量key
	private static final String PRICE_NUM_BID_MAP = "-PRICE-NUM-BID";
	// 卖单单个价格数量key
	private static final String PRICE_NUM_ASK_MAP = "-PRICE-NUM-ASK";

	/**
	 * -买 撮 合（imap）: "交易队名字"+"-MATCH-BID"<br>
	 * -卖 撮 合（imap）: "交易队名字"+"-MATCH-ASK"
	 *
	 * @param coinTeam 交易队
	 * @param isBuy    是买挂单
	 */
	public static String getMatchKey(String coinTeam, boolean isBuy) {
		return coinTeam + (isBuy ? MATCH_BID_MAP : MATCH_ASK_MAP);
	}

	/**
	 * -买水平价格（imap）: "交易队名字"+"-MATCH-BID"<br>
	 * -卖水平价格（imap）: "交易队名字"+"-MATCH-ASK"
	 *
	 * @param coinTeam 交易队
	 * @param isBuy    是买挂单
	 */
	public static String getOrderBookKey(String coinTeam, boolean isBuy) {
		return coinTeam + (isBuy ? ORDER_BOOK_BID_MAP : ORDER_BOOK_ASK_MAP);
	}
	
	
	/**
	 * -买水平价格（imap）: "交易队名字"+"-MATCH-BID"<br>
	 * -卖水平价格（imap）: "交易队名字"+"-MATCH-ASK"
	 *
	 * @param coinTeam 交易队
	 * @param isBuy    是买挂单
	 * @param price    交易价格
	 */
	public static String getPriceLevelNumber(MatchOrder order) {
		return order.getCoinTeam() + (order.getIsBuy() ? PRICE_NUM_BID_MAP : PRICE_NUM_ASK_MAP) + order.getPrice().toString();
	}
	
	/**
	 * -判断是否可以撮合
	 * -并处理好市价，的价格和数量
	 * @param outMap 对手盘口数据
	 * @param isBuy 是来的买单
	 */
	public static Boolean canMatch(MatchOrder input,BigDecimal outPrice) {
		if (input.getIsMarket()) {
			input.setPrice(outPrice);
			if (input.getIsBuy()) {
				input.setUnFinishNumber(input.getSurplusFrozen().divide(outPrice, input.getDecimalNumber(), RoundingMode.DOWN));
			}
		}
		if (input.getIsBuy()) {
			if (input.getPrice().compareTo(outPrice)>-1) {
				return true;
			}
		}else {
			if (input.getPrice().compareTo(outPrice)<1) {
				return true;
			}
		}
		return false;
	}
	

	/**
	 * -获取最优匹配数据
	 * @param outMap 对手盘口数据
	 * @param isBuy 是来的买单
	 */
	public static BigDecimal getOptimalMatch(Map<BigDecimal, BigDecimal> outMap,Boolean isBuy) {
		if (isBuy) {//是买，获取卖，取最小
			return outMap.keySet().parallelStream().min(BigDecimal::compareTo).get();
		}else {
			return outMap.keySet().parallelStream().max(BigDecimal::compareTo).get();
		}
	}
	
	/**
	 * -数量相加
	 * @param old 原数量
	 * @param num 增加量
	 */
	public static BigDecimal numberAdd(BigDecimal old, BigDecimal num) {
		if (null==old) {
			return num;
		}else {
			return old.add(num);
		}
	}
	
	/**
	 * -数量相减
	 * @param old 原数量
	 * @param num 减去量
	 * @throws Exception 
	 */
	public static BigDecimal numberMinus(BigDecimal old, BigDecimal num) throws Exception {
		BigDecimal newNum = old.subtract(num);
		if (newNum.compareTo(BigDecimal.ZERO)<0) {
			throw new Exception("减超过总数量！");
		}
		return newNum;
	}
}
