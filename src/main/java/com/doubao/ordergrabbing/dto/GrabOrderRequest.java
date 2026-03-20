package com.doubao.ordergrabbing.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GrabOrderRequest {
    private String productName;
    private Integer stock;
    private String startTime;
    private String endTime;
}
