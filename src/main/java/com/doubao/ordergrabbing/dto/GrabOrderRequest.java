package com.doubao.ordergrabbing.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class GrabOrderRequest {

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @NotBlank(message = "商品名称不能为空")
    private String productName;

    @NotNull(message = "库存不能为空")
    private Integer stock;

    public GrabOrderRequest() {
    }

    public GrabOrderRequest(LocalDateTime startTime, LocalDateTime endTime, String productName, Integer stock) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.productName = productName;
        this.stock = stock;
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
