package com.doubao.ordergrabbing.dto;

import com.doubao.ordergrabbing.entity.OrderStatus;

import java.time.LocalDateTime;

public class OrderResponse {

    private Long id;
    private String phone;
    private Long grabOrderId;
    private OrderStatus status;
    private LocalDateTime createTime;

    public OrderResponse() {
    }

    public OrderResponse(Long id, String phone, Long grabOrderId, OrderStatus status, LocalDateTime createTime) {
        this.id = id;
        this.phone = phone;
        this.grabOrderId = grabOrderId;
        this.status = status;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getGrabOrderId() {
        return grabOrderId;
    }

    public void setGrabOrderId(Long grabOrderId) {
        this.grabOrderId = grabOrderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
