package com.example.ordergrabbing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrabOrderResponse {
    
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String productName;
    private Integer stock;
    private Integer remainingStock;
    private boolean active;
}
