package com.doubao.ordergrabbing.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "grab_order")
public class GrabOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    public GrabOrder() {
    }

    public GrabOrder(Long id, LocalDateTime startTime, LocalDateTime endTime, String productName, Integer stock) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.productName = productName;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
