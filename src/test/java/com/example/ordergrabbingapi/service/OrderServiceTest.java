package com.example.ordergrabbingapi.service;

import com.example.ordergrabbingapi.entity.GrabOrder;
import com.example.ordergrabbingapi.entity.Order;
import com.example.ordergrabbingapi.exception.GrabOrderException;
import com.example.ordergrabbingapi.repository.GrabOrderRepository;
import com.example.ordergrabbingapi.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private GrabOrderRepository grabOrderRepository;

    private GrabOrder activeGrabOrder;
    private GrabOrder notStartedGrabOrder;
    private GrabOrder endedGrabOrder;
    private GrabOrder outOfStockGrabOrder;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        activeGrabOrder = new GrabOrder();
        activeGrabOrder.setGrabId(1L);
        activeGrabOrder.setProductName("Test Product");
        activeGrabOrder.setStartTime(now.minusHours(1));
        activeGrabOrder.setEndTime(now.plusHours(1));
        activeGrabOrder.setStock(100);

        notStartedGrabOrder = new GrabOrder();
        notStartedGrabOrder.setGrabId(2L);
        notStartedGrabOrder.setProductName("Future Product");
        notStartedGrabOrder.setStartTime(now.plusHours(1));
        notStartedGrabOrder.setEndTime(now.plusHours(2));
        notStartedGrabOrder.setStock(100);

        endedGrabOrder = new GrabOrder();
        endedGrabOrder.setGrabId(3L);
        endedGrabOrder.setProductName("Expired Product");
        endedGrabOrder.setStartTime(now.minusHours(2));
        endedGrabOrder.setEndTime(now.minusHours(1));
        endedGrabOrder.setStock(100);

        outOfStockGrabOrder = new GrabOrder();
        outOfStockGrabOrder.setGrabId(4L);
        outOfStockGrabOrder.setProductName("Out of Stock Product");
        outOfStockGrabOrder.setStartTime(now.minusHours(1));
        outOfStockGrabOrder.setEndTime(now.plusHours(1));
        outOfStockGrabOrder.setStock(0);
    }

    @Test
    void createOrderWithValidation_Success() {
        when(grabOrderRepository.findById(1L)).thenReturn(Optional.of(activeGrabOrder));
        when(orderRepository.existsByPhoneNumberAndGrabId("13800138000", 1L)).thenReturn(false);
        when(grabOrderRepository.decreaseStock(1L)).thenReturn(1);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setOrderId(1L);
            order.setCreateTime(LocalDateTime.now());
            return order;
        });

        Order result = orderService.createOrderWithValidation("13800138000", 1L);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals("13800138000", result.getPhoneNumber());
        assertEquals(1L, result.getGrabId());
        assertEquals("SUCCESS", result.getOrderStatus());
        assertNotNull(result.getCreateTime());

        verify(grabOrderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).existsByPhoneNumberAndGrabId("13800138000", 1L);
        verify(grabOrderRepository, times(1)).decreaseStock(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrderWithValidation_InvalidPhoneNumber() {
        assertThrows(GrabOrderException.class, () ->
                orderService.createOrderWithValidation("invalid-phone", 1L));

        verify(grabOrderRepository, never()).findById(anyLong());
        verify(orderRepository, never()).existsByPhoneNumberAndGrabId(anyString(), anyLong());
        verify(grabOrderRepository, never()).decreaseStock(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrderWithValidation_GrabOrderNotFound() {
        when(grabOrderRepository.findById(999L)).thenReturn(Optional.empty());

        GrabOrderException exception = assertThrows(GrabOrderException.class, () ->
                orderService.createOrderWithValidation("13800138000", 999L));

        assertEquals(GrabOrderException.ErrorCode.GRAB_ORDER_NOT_FOUND, exception.getErrorCode());
        verify(grabOrderRepository, times(1)).findById(999L);
        verify(orderRepository, never()).existsByPhoneNumberAndGrabId(anyString(), anyLong());
        verify(grabOrderRepository, never()).decreaseStock(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrderWithValidation_GrabNotStarted() {
        when(grabOrderRepository.findById(2L)).thenReturn(Optional.of(notStartedGrabOrder));

        GrabOrderException exception = assertThrows(GrabOrderException.class, () ->
                orderService.createOrderWithValidation("13800138000", 2L));

        assertEquals(GrabOrderException.ErrorCode.GRAB_NOT_STARTED, exception.getErrorCode());
        verify(grabOrderRepository, times(1)).findById(2L);
        verify(orderRepository, never()).existsByPhoneNumberAndGrabId(anyString(), anyLong());
        verify(grabOrderRepository, never()).decreaseStock(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrderWithValidation_GrabEnded() {
        when(grabOrderRepository.findById(3L)).thenReturn(Optional.of(endedGrabOrder));

        GrabOrderException exception = assertThrows(GrabOrderException.class, () ->
                orderService.createOrderWithValidation("13800138000", 3L));

        assertEquals(GrabOrderException.ErrorCode.GRAB_ENDED, exception.getErrorCode());
        verify(grabOrderRepository, times(1)).findById(3L);
        verify(orderRepository, never()).existsByPhoneNumberAndGrabId(anyString(), anyLong());
        verify(grabOrderRepository, never()).decreaseStock(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrderWithValidation_DuplicateOrder() {
        when(grabOrderRepository.findById(1L)).thenReturn(Optional.of(activeGrabOrder));
        when(orderRepository.existsByPhoneNumberAndGrabId("13800138000", 1L)).thenReturn(true);

        GrabOrderException exception = assertThrows(GrabOrderException.class, () ->
                orderService.createOrderWithValidation("13800138000", 1L));

        assertEquals(GrabOrderException.ErrorCode.DUPLICATE_ORDER, exception.getErrorCode());
        verify(grabOrderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).existsByPhoneNumberAndGrabId("13800138000", 1L);
        verify(grabOrderRepository, never()).decreaseStock(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrderWithValidation_InsufficientStock_CheckFirst() {
        when(grabOrderRepository.findById(4L)).thenReturn(Optional.of(outOfStockGrabOrder));
        when(orderRepository.existsByPhoneNumberAndGrabId("13800138000", 4L)).thenReturn(false);

        GrabOrderException exception = assertThrows(GrabOrderException.class, () ->
                orderService.createOrderWithValidation("13800138000", 4L));

        assertEquals(GrabOrderException.ErrorCode.INSUFFICIENT_STOCK, exception.getErrorCode());
        verify(grabOrderRepository, times(1)).findById(4L);
        verify(orderRepository, times(1)).existsByPhoneNumberAndGrabId("13800138000", 4L);
        verify(grabOrderRepository, never()).decreaseStock(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrderWithValidation_InsufficientStock_AtomicUpdate() {
        when(grabOrderRepository.findById(1L)).thenReturn(Optional.of(activeGrabOrder));
        when(orderRepository.existsByPhoneNumberAndGrabId("13800138000", 1L)).thenReturn(false);
        when(grabOrderRepository.decreaseStock(1L)).thenReturn(0);

        GrabOrderException exception = assertThrows(GrabOrderException.class, () ->
                orderService.createOrderWithValidation("13800138000", 1L));

        assertEquals(GrabOrderException.ErrorCode.INSUFFICIENT_STOCK, exception.getErrorCode());
        verify(grabOrderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).existsByPhoneNumberAndGrabId("13800138000", 1L);
        verify(grabOrderRepository, times(1)).decreaseStock(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_Basic_Success() {
        Order order = new Order();
        order.setPhoneNumber("13800138000");
        order.setGrabId(1L);
        order.setOrderStatus("SUCCESS");

        when(grabOrderRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setOrderId(1L);
            savedOrder.setCreateTime(LocalDateTime.now());
            return savedOrder;
        });

        Order result = orderService.createOrder(order);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_Basic_InvalidPhoneNumber() {
        Order order = new Order();
        order.setPhoneNumber("invalid");
        order.setGrabId(1L);
        order.setOrderStatus("SUCCESS");

        assertThrows(GrabOrderException.class, () -> orderService.createOrder(order));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_Basic_GrabOrderNotExist() {
        Order order = new Order();
        order.setPhoneNumber("13800138000");
        order.setGrabId(999L);
        order.setOrderStatus("SUCCESS");

        when(grabOrderRepository.existsById(999L)).thenReturn(false);

        assertThrows(GrabOrderException.class, () -> orderService.createOrder(order));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrdersByPhoneNumber_Success() {
        Order order1 = new Order();
        order1.setOrderId(1L);
        order1.setPhoneNumber("13800138000");
        order1.setGrabId(1L);

        Order order2 = new Order();
        order2.setOrderId(2L);
        order2.setPhoneNumber("13800138000");
        order2.setGrabId(2L);

        when(orderRepository.findByPhoneNumber("13800138000")).thenReturn(Arrays.asList(order1, order2));

        List<Order> result = orderService.getOrdersByPhoneNumber("13800138000");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findByPhoneNumber("13800138000");
    }

    @Test
    void getOrdersByGrabId_Success() {
        Order order1 = new Order();
        order1.setOrderId(1L);
        order1.setPhoneNumber("13800138000");
        order1.setGrabId(1L);

        Order order2 = new Order();
        order2.setOrderId(2L);
        order2.setPhoneNumber("13800138001");
        order2.setGrabId(1L);

        when(orderRepository.findByGrabId(1L)).thenReturn(Arrays.asList(order1, order2));

        List<Order> result = orderService.getOrdersByGrabId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findByGrabId(1L);
    }
}
