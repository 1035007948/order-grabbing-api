package com.doubao.ordergrabbing.controller;

import com.doubao.ordergrabbing.dto.GrabOrderRequest;
import com.doubao.ordergrabbing.dto.GrabOrderResponse;
import com.doubao.ordergrabbing.service.GrabOrderService;
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

@WebMvcTest(GrabOrderController.class)
class GrabOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GrabOrderService grabOrderService;

    private GrabOrderRequest request;
    private GrabOrderResponse response;

    @BeforeEach
    void setUp() {
        request = new GrabOrderRequest();
        request.setStartTime(LocalDateTime.now().minusHours(1));
        request.setEndTime(LocalDateTime.now().plusHours(1));
        request.setProductName("测试商品");
        request.setStock(10);

        response = new GrabOrderResponse(
                1L,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                "测试商品",
                10
        );
    }

    @Test
    @WithMockUser
    void createGrabOrder_Success() throws Exception {
        when(grabOrderService.createGrabOrder(any(GrabOrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/grab-orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productName").value("测试商品"));
    }

    @Test
    @WithMockUser
    void getAllGrabOrders_Success() throws Exception {
        List<GrabOrderResponse> responses = Arrays.asList(response);
        when(grabOrderService.getAllGrabOrders()).thenReturn(responses);

        mockMvc.perform(get("/api/grab-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser
    void getGrabOrderById_Success() throws Exception {
        when(grabOrderService.getGrabOrderById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/grab-orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser
    void getActiveGrabOrders_Success() throws Exception {
        List<GrabOrderResponse> responses = Arrays.asList(response);
        when(grabOrderService.getActiveGrabOrders()).thenReturn(responses);

        mockMvc.perform(get("/api/grab-orders/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void updateGrabOrder_Success() throws Exception {
        when(grabOrderService.updateGrabOrder(eq(1L), any(GrabOrderRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/grab-orders/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
