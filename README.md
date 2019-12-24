<p align="center">
    <img alt="version" src="https://img.shields.io/badge/version-0.0.1--SNAPSHOT-blue">
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.html">
        <img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" ></img>
    </a>
    <a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
        <img src="https://img.shields.io/badge/JDK-1.8+-green.svg" ></img>
    </a>
</p>

# match-engine

#### 介绍
match-trade超高效的交易所撮合引擎，采用伦敦外汇交易所LMAX开源的Disruptor框架，用Hazelcast进行分布式内存存取，以及原子性操作。使用数据流的方式进行计算撮合序列，才用价格水平独立撮合逻辑，实现高效大数据撮合。

#### 优势
- ***match-engine***是以水平价格为独立撮合逻辑，相比于别的**订单队列**为撮合队列的交易引擎来说，价格区间越小订单数越大时，性能越明显。
- ***match-engine***没个价格下的订单都是**异步**完成被撮合。独立价格下订单不影响下一个新发生的撮合。
- ***match-engine***每个价格撮合都是**独立的**，与下一个价格没的关系，实现快速吃单。
- ***match-engine***每个新的订单经历撮合处理器后，后续逻辑采用**并行计算**，能更快速反馈数据撮合结果。
- ***match-engine***撤单走独立的逻辑，不用和下单在一个处理序列。

#### 技术选择
- ***Disruptor：*** 号称每秒钟承载600万订单级别的无锁并行计算框架，主要选择原因还是并行计算。
- ***Hazelcast：*** 能很好进行内存处理，有很强原子性保障的操作能力。同时分布式内存实现很简单，能自动内存集群。
- ***Kafka：*** 比较适合大吞吐量的消息，有事务机制，同时能保证消息顺序消费。
- ***WebFlux：*** 它能够充分利用多核 CPU 的硬件资源去处理大量的并发请求。

## 描述
**用户输入包括：**
- 创建新的委托单（NewOrder）：一个新的委托单可以作为交易撮合引擎的输入，引擎会尝试将其与已有的 委托单进行撮合。
- 取消已有的委托单（CancelOrder）：用户也可以取消一个之前输入的委托单，如果它还没有执行的话，即开口订单。

**委托单：**
- 限价委托单    
    限价委托单是在当前的加密货币交易环境中最常用的委托类型。这种委托单允许用户指定一个价格，只有当撮合引擎找到同样价格甚至更好价格的对手单时才执行交易。
- 市价委托单   
    市价委托单的撮合会完全忽略价格因素，而致力于有限完成指定数量的成交。市价委托单在交易委托账本中有较高的优先级，在流动性充足的市场中市价单可以保证成交。不充足时，撮合完最后一条撤销。
- 止损委托单   
    止损委托单尽在市场价格到达指定价位时才被激活，因此它的执行方式与市价委托单相反。一旦止损委托单激活，它们可以自动转化为市价委托单或限价委托单。（未实现）

## 撮合流程
限价撮合：
![输入图片说明](https://images.gitee.com/uploads/images/2019/1223/093137_a98aa989_538536.jpeg "limit.jpg")

市价撮合：
![输入图片说明](https://images.gitee.com/uploads/images/2019/1223/093204_e3020309_538536.jpeg "market.jpg")
目前就实现这两种订单撮合

## 订单簿为撮合簿时代码解析
这个是一个简单流盘口计算demo
```
//获取匹配的订单薄数据
IMap<Long, Order> outMap = hzInstance.getMap(HzltUtil.getMatchKey(coinTeam, isBuy));
/**
 * -★
 * -使用Java 8 Stream API中的并行流来计算最优
 * -能快速的拿到撮合对象，不用排序取值，降低性能消耗
 */
Order outOrder = outMap.values().parallelStream().min(HzltUtil::compareOrder).get();

//这种方式最难的，就是整理盘口深度数据了

    /**
     * -★
	 * -获取行情深度
	 * 
	 * @param coinTeam 交易队
	 * @param isBuy    是否是买
	 * @return List<Depth>
	 */
	public List<Depth> getMarketDepth(String coinTeam, Boolean isBuy) {
		List<Depth> depths = new ArrayList<Depth>();
		IMap<Long, Order> map = hzInstance.getMap(HzltUtil.getMatchKey(coinTeam, isBuy));
		if (map.size() > 0) {
			/**
			 * -这个流：主要是安价格分组和统计，使用并行流快速归集。
			 */ 
			List<Depth> list = map.entrySet().parallelStream().map(mo -> mo.getValue())
					.collect(Collectors.groupingBy(Order::getPrice)).entrySet().parallelStream()
					.map(ml -> new Depth(ml.getKey().toString(),
							ml.getValue().stream().map(o -> o.getUnFinishNumber()).reduce(BigDecimal.ZERO, BigDecimal::add)
									.toString(),
							"0", 1, coinTeam, isBuy))
					.sorted((d1, d2) -> HzltUtil.compareTo(d1, d2)).collect(Collectors.toList());
			/**
			 * -这个流：主要是盘口的累计计算，因涉及排序选择串行流
			 */
			list.stream().reduce(new Depth("0", "0", "0", 1, coinTeam, isBuy), (one, two) -> {
				one.setTotal((new BigDecimal(one.getTotal()).add(new BigDecimal(two.getNumber()))).toString());
				depths.add(new Depth(two.getPrice(), two.getNumber(), one.getTotal(), two.getPlatform(),
						two.getCoinTeam(), two.getIsBuy()));
				return one;
			});
		} else {
			Depth depth = new Depth("0", "0", "0", 1, coinTeam, isBuy);
			depths.add(depth);
		}
		return depths;
	}
```
## 测试结果
在我8cpu，16G内存的开发win10系统上测试结果：
- Disruptor单生产者初始化10万不能撮合的订单耗时：约700毫秒    
- Disruptor多生产者初始化10万不能撮合的订单耗时：约20秒    
- 实际单吃完1-100价格内随机数量的10万订单耗时：约400毫秒    