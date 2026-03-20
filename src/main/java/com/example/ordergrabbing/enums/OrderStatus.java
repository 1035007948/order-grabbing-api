package com.example.ordergrabbing.enums;

public enum OrderStatus {
    PENDING("待处理"),
    SUCCESS("成功"),
    FAILED("失败"),
    CANCELLED("已取消");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
