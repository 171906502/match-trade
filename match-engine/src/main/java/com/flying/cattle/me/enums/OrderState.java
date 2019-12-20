package com.flying.cattle.me.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * -订单状态
 */
public enum OrderState {
    PUTUP(0, "挂单"),
    PART(1, "部分成交"),
    ALL(2, "全部成交"),
    CANCEL(3, "已撤销"),
    FINISH(4, "已结算"),
    ;

    public final int value;
    public final String name;

    OrderState(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static Optional<OrderState> of(int value) {
        return Arrays.stream(values()).filter(i -> i.value == value).findFirst();
    }
}

