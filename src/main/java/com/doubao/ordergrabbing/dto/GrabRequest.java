package com.doubao.ordergrabbing.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GrabRequest {
    private Long memberId;
    private BigDecimal amount;
}
