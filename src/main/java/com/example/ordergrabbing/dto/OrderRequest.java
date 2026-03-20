package com.example.ordergrabbing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    
    @NotNull(message = "会员ID不能为空")
    private Long memberId;
    
    @NotNull(message = "抢单ID不能为空")
    private Long grabOrderId;
    
    @NotNull(message = "订单金额不能为空")
    @Positive(message = "订单金额必须大于0")
    private BigDecimal amount;
}
