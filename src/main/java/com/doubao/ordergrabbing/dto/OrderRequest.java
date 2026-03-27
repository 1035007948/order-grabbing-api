package com.doubao.ordergrabbing.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class OrderRequest {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotNull(message = "抢单ID不能为空")
    private Long grabOrderId;

    public OrderRequest() {
    }

    public OrderRequest(String phone, Long grabOrderId) {
        this.phone = phone;
        this.grabOrderId = grabOrderId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getGrabOrderId() {
        return grabOrderId;
    }

    public void setGrabOrderId(Long grabOrderId) {
        this.grabOrderId = grabOrderId;
    }
}
