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
- ***match-trade***是以水平价格为独立撮合逻辑，相比于别的**订单队列**为撮合队列的交易引擎来说，价格区间越小订单数越大时，性能越明显。
- ***match-trade***没个价格下的订单都是**异步**完成被撮合。独立价格下订单不影响下一个新发生的撮合。
- ***match-trade***每个价格撮合都是**独立的**，与下一个价格没的关系，实现快速吃单。
- ***match-trade***每个新的订单经历撮合处理器后，后续逻辑采用**并行**处理，能更快速反馈数据撮合结果。
- ***match-trade***撤单走独立的逻辑，不用和下单在一个处理序列。

## 描述
**用户输入包括：**
- 创建新的委托单（NewOrder）：一个新的委托单可以作为交易撮合引擎的输入，引擎会尝试将其与已有的 委托单进行撮合。
- 取消已有的委托单（CancelOrder）：用户也可以取消一个之前输入的委托单，如果它还没有执行的话，即开口订单。

**委托单：**
- 限价委托单    
    限价委托单是在当前的加密货币交易环境中最常用的委托类型。这种委托单允许用户指定一个价格，只有当撮合引擎找到同样价格甚至更好价格的对手单时才执行交易。
- 市价委托单   
    市价委托单的撮合会完全忽略价格因素，而致力于有限完成指定数量的成交。市价委托单在交易委托账本中有较高的优先级，在流动性充足的市场中市价单可以保证成交。
-止损委托单   
    止损委托单尽在市场价格到达指定价位时才被激活，因此它的执行方式与市价委托单相反。一旦止损委托单激活，它们可以自动转化为市价委托单或限价委托单。