package com.doubao.order.controller;

import com.doubao.order.dto.GrabOrderRequest;
import com.doubao.order.dto.OrderRequest;
import com.doubao.order.entity.GrabOrder;
import com.doubao.order.entity.Order;
import com.doubao.order.entity.Order.OrderStatus;
import com.doubao.order.repository.GrabOrderRepository;
import com.doubao.order.repository.OrderRepository;
import com.doubao.order.security.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private GrabOrderRepository grabOrderRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String token;
    private GrabOrder testGrabOrder;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        grabOrderRepository.deleteAll();

        testGrabOrder = new GrabOrder();
        testGrabOrder.setProductName("Test Product");
        testGrabOrder.setStock(10);
        testGrabOrder.setStartTime(LocalDateTime.now().minusHours(1));
        testGrabOrder.setEndTime(LocalDateTime.now().plusHours(1));
        testGrabOrder = grabOrderRepository.save(testGrabOrder);

        testOrder = new Order();
        testOrder.setPhone("13800138000");
        testOrder.setGrabId(testGrabOrder.getId());
        testOrder.setStatus(OrderStatus.GRABBED);
        testOrder = orderRepository.save(testOrder);

        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        token = jwtTokenUtil.generateToken(userDetails);
    }

    @Test
    void testGetAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void testGetOrderById() throws Exception {
        mockMvc.perform(get("/api/orders/" + testOrder.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phone").value("13800138000"));
    }

    @Test
    void testGetOrdersByPhone() throws Exception {
        mockMvc.perform(get("/api/orders/phone/13800138000")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void testGrabOrder() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setPhone("13900139000");
        request.setGrabId(testGrabOrder.getId());

        mockMvc.perform(post("/api/orders/grab")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phone").value("13900139000"));
    }

    @Test
    void testUpdateOrderStatus() throws Exception {
        mockMvc.perform(put("/api/orders/" + testOrder.getId() + "/status")
                .header("Authorization", "Bearer " + token)
                .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testDeleteOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/" + testOrder.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
