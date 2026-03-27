package com.example.ordergrabbingapi.controller;

import com.example.ordergrabbingapi.entity.GrabOrder;
import com.example.ordergrabbingapi.service.GrabOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GrabOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GrabOrderService grabOrderService;

    @Test
    @WithMockUser(username = "admin", password = "admin123")
    public void testCreateGrabOrder() throws Exception {
        GrabOrder grabOrder = new GrabOrder();
        grabOrder.setStartTime(LocalDateTime.now());
        grabOrder.setEndTime(LocalDateTime.now().plusHours(1));
        grabOrder.setProductName("Test Product");
        grabOrder.setStock(100);

        when(grabOrderService.createGrabOrder(any(GrabOrder.class))).thenReturn(grabOrder);

        mockMvc.perform(post("/api/grab-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startTime\":\"2023-10-01T10:00:00\",\"endTime\":\"2023-10-01T11:00:00\",\"productName\":\"Test Product\",\"stock\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Test Product"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin123")
    public void testGetGrabOrderById() throws Exception {
        GrabOrder grabOrder = new GrabOrder();
        grabOrder.setGrabId(1L);
        grabOrder.setStartTime(LocalDateTime.now());
        grabOrder.setEndTime(LocalDateTime.now().plusHours(1));
        grabOrder.setProductName("Test Product");
        grabOrder.setStock(100);

        when(grabOrderService.getGrabOrderById(1L)).thenReturn(Optional.of(grabOrder));

        mockMvc.perform(get("/api/grab-orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grabId").value(1));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin123")
    public void testGetAllGrabOrders() throws Exception {
        GrabOrder grabOrder1 = new GrabOrder();
        grabOrder1.setGrabId(1L);
        grabOrder1.setStartTime(LocalDateTime.now());
        grabOrder1.setEndTime(LocalDateTime.now().plusHours(1));
        grabOrder1.setProductName("Product 1");
        grabOrder1.setStock(100);

        GrabOrder grabOrder2 = new GrabOrder();
        grabOrder2.setGrabId(2L);
        grabOrder2.setStartTime(LocalDateTime.now());
        grabOrder2.setEndTime(LocalDateTime.now().plusHours(2));
        grabOrder2.setProductName("Product 2");
        grabOrder2.setStock(200);

        when(grabOrderService.getAllGrabOrders()).thenReturn(Arrays.asList(grabOrder1, grabOrder2));

        mockMvc.perform(get("/api/grab-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin123")
    public void testUpdateGrabOrder() throws Exception {
        GrabOrder grabOrder = new GrabOrder();
        grabOrder.setGrabId(1L);
        grabOrder.setStartTime(LocalDateTime.now());
        grabOrder.setEndTime(LocalDateTime.now().plusHours(1));
        grabOrder.setProductName("Test Product");
        grabOrder.setStock(100);

        GrabOrder updatedGrabOrder = new GrabOrder();
        updatedGrabOrder.setGrabId(1L);
        updatedGrabOrder.setStartTime(LocalDateTime.now());
        updatedGrabOrder.setEndTime(LocalDateTime.now().plusHours(2));
        updatedGrabOrder.setProductName("Updated Product");
        updatedGrabOrder.setStock(200);

        when(grabOrderService.updateGrabOrder(eq(1L), any(GrabOrder.class))).thenReturn(updatedGrabOrder);

        mockMvc.perform(put("/api/grab-orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startTime\":\"2023-10-01T10:00:00\",\"endTime\":\"2023-10-01T12:00:00\",\"productName\":\"Updated Product\",\"stock\":200}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Updated Product"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin123")
    public void testDeleteGrabOrder() throws Exception {
        doNothing().when(grabOrderService).deleteGrabOrder(1L);

        mockMvc.perform(delete("/api/grab-orders/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin123")
    public void testGrabOrder() throws Exception {
        when(grabOrderService.grabOrder(1L, "1234567890")).thenReturn(true);

        mockMvc.perform(post("/api/grab-orders/1/grab?phoneNumber=1234567890"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order grabbed successfully!"));
    }
}
