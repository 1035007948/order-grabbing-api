package com.doubao.order.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class OrderRequest {

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull(message = "Grab order ID is required")
    private Long grabId;
}
