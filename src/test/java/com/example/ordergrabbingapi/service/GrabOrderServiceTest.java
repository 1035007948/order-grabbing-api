package com.example.ordergrabbingapi.service;

import com.example.ordergrabbingapi.entity.GrabOrder;
import com.example.ordergrabbingapi.exception.GrabOrderException;
import com.example.ordergrabbingapi.repository.GrabOrderRepository;
import com.example.ordergrabbingapi.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class GrabOrderServiceTest {

    @Autowired
    private GrabOrderService grabOrderService;

    @MockBean
    private GrabOrderRepository grabOrderRepository;

    @MockBean
    private OrderRepository orderRepository;

    private GrabOrder activeGrabOrder;
    private GrabOrder notStartedGrabOrder;
    private GrabOrder endedGrabOrder;

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
    }

    @Test
    void grabOrder_Success() {
        when(grabOrderRepository.findById(1L)).thenReturn(Optional.of(activeGrabOrder));
        when(orderRepository.existsByPhoneNumberAndGrabId(anyString(), eq(1L))).thenReturn(false);
        when(grabOrderRepository.decreaseStock(1L)).thenReturn(1);

        boolean result = grabOrderService.grabOrder(1L, "13800138000");

        assertTrue(result);
        verify(grabOrderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).existsByPhoneNumberAndGrabId("13800138000", 1L);
        verify(grabOrderRepository, times(1)).decreaseStock(1L);
        verify(orderRepository, times(1)).save(any());
    }

    @Test
    void grabOrder_InvalidPhoneNumber() {
        assertThrows(GrabOrderException.class, () ->
                grabOrderService.grabOrder(1L, "invalid-phone"));

        verify(grabOrderRepository, never()).findById(anyLong());
        verify(orderRepository, never()).existsByPhoneNumberAndGrabId(anyString(), anyLong());
        verify(grabOrderRepository, never()).decreaseStock(anyLong());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void grabOrder_GrabOrderNotFound() {
        when(grabOrderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(GrabOrderException.class, () ->
                grabOrderService.grabOrder(999L, "13800138000"));

        verify(grabOrderRepository, times(1)).findById(999L);
        verify(orderRepository, never()).existsByPhoneNumberAndGrabId(anyString(), anyLong());
        verify(grabOrderRepository, never()).decreaseStock(anyLong());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void grabOrder_GrabNotStarted() {
        when(grabOrderRepository.findById(2L)).thenReturn(Optional.of(notStartedGrabOrder));

        GrabOrderException exception = assertThrows(GrabOrderException.class, () ->
                grabOrderService.grabOrder(2L, "13800138000"));

        assertEquals(GrabOrderException.ErrorCode.GRAB_NOT_STARTED, exception.getErrorCode());
        verify(grabOrderRepository, times(1)).findById(2L);
        verify(orderRepository, never()).existsByPhoneNumberAndGrabId(anyString(), anyLong());
        verify(grabOrderRepository, never()).decreaseStock(anyLong());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void grabOrder_GrabEnded() {
        when(grabOrderRepository.findById(3L)).thenReturn(Optional.of(endedGrabOrder));

        GrabOrderException exception = assertThrows(GrabOrderException.class, () ->
                grabOrderService.grabOrder(3L, "13800138000"));

        assertEquals(GrabOrderException.ErrorCode.GRAB_ENDED, exception.getErrorCode());
        verify(grabOrderRepository, times(1)).findById(3L);
        verify(orderRepository, never()).existsByPhoneNumberAndGrabId(anyString(), anyLong());
        verify(grabOrderRepository, never()).decreaseStock(anyLong());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void grabOrder_DuplicateOrder() {
        when(grabOrderRepository.findById(1L)).thenReturn(Optional.of(activeGrabOrder));
        when(orderRepository.existsByPhoneNumberAndGrabId("13800138000", 1L)).thenReturn(true);

        GrabOrderException exception = assertThrows(GrabOrderException.class, () ->
                grabOrderService.grabOrder(1L, "13800138000"));

        assertEquals(GrabOrderException.ErrorCode.DUPLICATE_ORDER, exception.getErrorCode());
        verify(grabOrderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).existsByPhoneNumberAndGrabId("13800138000", 1L);
        verify(grabOrderRepository, never()).decreaseStock(anyLong());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void grabOrder_InsufficientStock() {
        when(grabOrderRepository.findById(1L)).thenReturn(Optional.of(activeGrabOrder));
        when(orderRepository.existsByPhoneNumberAndGrabId(anyString(), eq(1L))).thenReturn(false);
        when(grabOrderRepository.decreaseStock(1L)).thenReturn(0);

        GrabOrderException exception = assertThrows(GrabOrderException.class, () ->
                grabOrderService.grabOrder(1L, "13800138000"));

        assertEquals(GrabOrderException.ErrorCode.INSUFFICIENT_STOCK, exception.getErrorCode());
        verify(grabOrderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).existsByPhoneNumberAndGrabId("13800138000", 1L);
        verify(grabOrderRepository, times(1)).decreaseStock(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createGrabOrder_Success() {
        GrabOrder newGrabOrder = new GrabOrder();
        newGrabOrder.setProductName("New Product");
        newGrabOrder.setStartTime(LocalDateTime.now());
        newGrabOrder.setEndTime(LocalDateTime.now().plusHours(2));
        newGrabOrder.setStock(50);

        when(grabOrderRepository.save(any(GrabOrder.class))).thenReturn(newGrabOrder);

        GrabOrder result = grabOrderService.createGrabOrder(newGrabOrder);

        assertNotNull(result);
        assertEquals("New Product", result.getProductName());
        verify(grabOrderRepository, times(1)).save(newGrabOrder);
    }

    @Test
    void createGrabOrder_InvalidTimeRange() {
        GrabOrder invalidGrabOrder = new GrabOrder();
        invalidGrabOrder.setProductName("Invalid Product");
        invalidGrabOrder.setStartTime(LocalDateTime.now().plusHours(2));
        invalidGrabOrder.setEndTime(LocalDateTime.now());
        invalidGrabOrder.setStock(50);

        assertThrows(IllegalArgumentException.class, () ->
                grabOrderService.createGrabOrder(invalidGrabOrder));

        verify(grabOrderRepository, never()).save(any());
    }

    @Test
    void createGrabOrder_NegativeStock() {
        GrabOrder invalidGrabOrder = new GrabOrder();
        invalidGrabOrder.setProductName("Invalid Product");
        invalidGrabOrder.setStartTime(LocalDateTime.now());
        invalidGrabOrder.setEndTime(LocalDateTime.now().plusHours(2));
        invalidGrabOrder.setStock(-10);

        assertThrows(IllegalArgumentException.class, () ->
                grabOrderService.createGrabOrder(invalidGrabOrder));

        verify(grabOrderRepository, never()).save(any());
    }
}
