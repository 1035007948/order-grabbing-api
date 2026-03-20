package com.example.ordergrabbing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrabOrderRequest {
    
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    @Future(message = "结束时间必须是未来时间")
    private LocalDateTime endTime;
    
    @NotBlank(message = "商品名称不能为空")
    private String productName;
    
    @NotNull(message = "库存不能为空")
    @Positive(message = "库存必须大于0")
    private Integer stock;
}
