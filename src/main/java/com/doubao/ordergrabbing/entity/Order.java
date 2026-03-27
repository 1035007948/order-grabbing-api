package com.doubao.ordergrabbing.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "grab_order_id", nullable = false)
    private Long grabOrderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grab_order_id", insertable = false, updatable = false)
    private GrabOrder grabOrder;

    public Order() {
    }

    public Order(Long id, String phone, Long grabOrderId, OrderStatus status, LocalDateTime createTime) {
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

    public GrabOrder getGrabOrder() {
        return grabOrder;
    }

    public void setGrabOrder(GrabOrder grabOrder) {
        this.grabOrder = grabOrder;
    }
}
