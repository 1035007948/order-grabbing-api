package com.example.ordergrabbing.dto;

import com.example.ordergrabbing.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private Long id;
    private Long memberId;
    private String memberNickname;
    private Long grabOrderId;
    private String productName;
    private BigDecimal amount;
    private OrderStatus status;
    private String statusDescription;
    private LocalDateTime createTime;
}
