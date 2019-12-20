package com.flying.cattle.me.enums;

public enum DealWay {
    TAKER("taker", "市价"),
    MAKER("maker", "限价"),
    CANCEL("cancel", "系统撤单"),
    ;

    public final String value;
    public final String name;

    DealWay(String value, String name) {
        this.value = value;
        this.name = name;
    }
}
