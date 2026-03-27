package com.doubao.ordergrabbing.controller;

import com.doubao.ordergrabbing.dto.OrderRequest;
import com.doubao.ordergrabbing.dto.OrderResponse;
import com.doubao.ordergrabbing.entity.OrderStatus;
import com.doubao.ordergrabbing.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderRequest request;
    private OrderResponse response;

    @BeforeEach
    void setUp() {
        request = new OrderRequest();
        request.setPhone("13800138000");
        request.setGrabOrderId(1L);

        response = new OrderResponse(
                1L,
                "13800138000",
                1L,
                OrderStatus.SUCCESS,
                LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser
    void createOrder_Success() throws Exception {
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.phone").value("13800138000"));
    }

    @Test
    @WithMockUser
    void getAllOrders_Success() throws Exception {
        List<OrderResponse> responses = Arrays.asList(response);
        when(orderService.getAllOrders()).thenReturn(responses);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser
    void getOrderById_Success() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser
    void getOrdersByPhone_Success() throws Exception {
        List<OrderResponse> responses = Arrays.asList(response);
        when(orderService.getOrdersByPhone("13800138000")).thenReturn(responses);

        mockMvc.perform(get("/api/orders/phone/13800138000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void cancelOrder_Success() throws Exception {
        response.setStatus(OrderStatus.CANCELLED);
        when(orderService.cancelOrder(1L)).thenReturn(response);

        mockMvc.perform(put("/api/orders/1/cancel")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void deleteOrder_Success() throws Exception {
        mockMvc.perform(delete("/api/orders/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
