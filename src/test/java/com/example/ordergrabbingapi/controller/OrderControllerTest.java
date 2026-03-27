package com.example.ordergrabbingapi.controller;

import com.example.ordergrabbingapi.entity.Order;
import com.example.ordergrabbingapi.service.OrderService;
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
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @WithMockUser(username = "admin", password = "admin123")
    public void testCreateOrder() throws Exception {
        Order order = new Order();
        order.setPhoneNumber("1234567890");
        order.setGrabId(1L);
        order.setOrderStatus("SUCCESS");
        order.setCreateTime(LocalDateTime.now());

        when(orderService.createOrder(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":\"1234567890\",\"grabId\":1,\"orderStatus\":\"SUCCESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin123")
    public void testGetOrderById() throws Exception {
        Order order = new Order();
        order.setOrderId(1L);
        order.setPhoneNumber("1234567890");
        order.setGrabId(1L);
        order.setOrderStatus("SUCCESS");
        order.setCreateTime(LocalDateTime.now());

        when(orderService.getOrderById(1L)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin123")
    public void testGetAllOrders() throws Exception {
        Order order1 = new Order();
        order1.setOrderId(1L);
        order1.setPhoneNumber("1234567890");
        order1.setGrabId(1L);
        order1.setOrderStatus("SUCCESS");
        order1.setCreateTime(LocalDateTime.now());

        Order order2 = new Order();
        order2.setOrderId(2L);
        order2.setPhoneNumber("0987654321");
        order2.setGrabId(2L);
        order2.setOrderStatus("SUCCESS");
        order2.setCreateTime(LocalDateTime.now());

        when(orderService.getAllOrders()).thenReturn(Arrays.asList(order1, order2));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin123")
    public void testUpdateOrder() throws Exception {
        Order order = new Order();
        order.setOrderId(1L);
        order.setPhoneNumber("1234567890");
        order.setGrabId(1L);
        order.setOrderStatus("SUCCESS");
        order.setCreateTime(LocalDateTime.now());

        Order updatedOrder = new Order();
        updatedOrder.setOrderId(1L);
        updatedOrder.setPhoneNumber("1111111111");
        updatedOrder.setGrabId(1L);
        updatedOrder.setOrderStatus("SUCCESS");
        updatedOrder.setCreateTime(LocalDateTime.now());

        when(orderService.updateOrder(eq(1L), any(Order.class))).thenReturn(updatedOrder);

        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":\"1111111111\",\"grabId\":1,\"orderStatus\":\"SUCCESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("1111111111"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin123")
    public void testDeleteOrder() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());
    }
}
