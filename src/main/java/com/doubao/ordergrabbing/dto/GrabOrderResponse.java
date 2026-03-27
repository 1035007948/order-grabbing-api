package com.doubao.ordergrabbing.dto;

import java.time.LocalDateTime;

public class GrabOrderResponse {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String productName;
    private Integer stock;

    public GrabOrderResponse() {
    }

    public GrabOrderResponse(Long id, LocalDateTime startTime, LocalDateTime endTime, String productName, Integer stock) {
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
