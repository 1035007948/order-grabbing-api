package com.example.ordergrabbing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrabRequest {
    
    @NotNull(message = "会员ID不能为空")
    private Long memberId;
    
    @NotNull(message = "抢单ID不能为空")
    private Long grabOrderId;
}
