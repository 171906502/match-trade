package com.flying.cattle.me.enums;

public enum OrderType {
	MARKET("market", "市价"),
    LIMIT("limit", "限价"),
    ;

    public final String value;
    public final String name;

    OrderType(String value, String name) {
        this.value = value;
        this.name = name;
    }
}
