package com.doubao.order.controller;

import com.doubao.order.dto.GrabOrderRequest;
import com.doubao.order.entity.GrabOrder;
import com.doubao.order.repository.GrabOrderRepository;
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
class GrabOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GrabOrderRepository grabOrderRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String token;
    private GrabOrder testGrabOrder;

    @BeforeEach
    void setUp() {
        grabOrderRepository.deleteAll();

        testGrabOrder = new GrabOrder();
        testGrabOrder.setProductName("Test Product");
        testGrabOrder.setStock(10);
        testGrabOrder.setStartTime(LocalDateTime.now().minusHours(1));
        testGrabOrder.setEndTime(LocalDateTime.now().plusHours(1));
        testGrabOrder = grabOrderRepository.save(testGrabOrder);

        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        token = jwtTokenUtil.generateToken(userDetails);
    }

    @Test
    void testGetAllGrabOrders() throws Exception {
        mockMvc.perform(get("/api/grab-orders")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void testGetGrabOrderById() throws Exception {
        mockMvc.perform(get("/api/grab-orders/" + testGrabOrder.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.productName").value("Test Product"));
    }

    @Test
    void testCreateGrabOrder() throws Exception {
        GrabOrderRequest request = new GrabOrderRequest();
        request.setProductName("New Product");
        request.setStock(20);
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusHours(2));

        mockMvc.perform(post("/api/grab-orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.productName").value("New Product"));
    }

    @Test
    void testUpdateGrabOrder() throws Exception {
        GrabOrderRequest request = new GrabOrderRequest();
        request.setProductName("Updated Product");
        request.setStock(30);
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusHours(3));

        mockMvc.perform(put("/api/grab-orders/" + testGrabOrder.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.productName").value("Updated Product"));
    }

    @Test
    void testDeleteGrabOrder() throws Exception {
        mockMvc.perform(delete("/api/grab-orders/" + testGrabOrder.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
