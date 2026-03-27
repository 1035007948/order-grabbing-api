package com.doubao.ordergrabbing.service;

import com.doubao.ordergrabbing.dto.GrabOrderRequest;
import com.doubao.ordergrabbing.dto.GrabOrderResponse;
import com.doubao.ordergrabbing.entity.GrabOrder;
import com.doubao.ordergrabbing.exception.BusinessException;
import com.doubao.ordergrabbing.repository.GrabOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrabOrderServiceTest {

    @Mock
    private GrabOrderRepository grabOrderRepository;

    @InjectMocks
    private GrabOrderService grabOrderService;

    private GrabOrder grabOrder;
    private GrabOrderRequest request;

    @BeforeEach
    void setUp() {
        grabOrder = new GrabOrder();
        grabOrder.setId(1L);
        grabOrder.setStartTime(LocalDateTime.now().minusHours(1));
        grabOrder.setEndTime(LocalDateTime.now().plusHours(1));
        grabOrder.setProductName("测试商品");
        grabOrder.setStock(10);

        request = new GrabOrderRequest();
        request.setStartTime(LocalDateTime.now().minusHours(1));
        request.setEndTime(LocalDateTime.now().plusHours(1));
        request.setProductName("测试商品");
        request.setStock(10);
    }

    @Test
    void createGrabOrder_Success() {
        when(grabOrderRepository.save(any(GrabOrder.class))).thenReturn(grabOrder);

        GrabOrderResponse response = grabOrderService.createGrabOrder(request);

        assertNotNull(response);
        assertEquals("测试商品", response.getProductName());
        assertEquals(10, response.getStock());
        verify(grabOrderRepository, times(1)).save(any(GrabOrder.class));
    }

    @Test
    void createGrabOrder_EndTimeBeforeStartTime_ThrowsException() {
        request.setEndTime(LocalDateTime.now().minusHours(2));

        assertThrows(BusinessException.class, () -> grabOrderService.createGrabOrder(request));
        verify(grabOrderRepository, never()).save(any(GrabOrder.class));
    }

    @Test
    void createGrabOrder_InvalidStock_ThrowsException() {
        request.setStock(0);

        assertThrows(BusinessException.class, () -> grabOrderService.createGrabOrder(request));
        verify(grabOrderRepository, never()).save(any(GrabOrder.class));
    }

    @Test
    void getAllGrabOrders_Success() {
        when(grabOrderRepository.findAll()).thenReturn(Arrays.asList(grabOrder));

        List<GrabOrderResponse> responses = grabOrderService.getAllGrabOrders();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(grabOrderRepository, times(1)).findAll();
    }

    @Test
    void getGrabOrderById_Success() {
        when(grabOrderRepository.findById(1L)).thenReturn(Optional.of(grabOrder));

        GrabOrderResponse response = grabOrderService.getGrabOrderById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(grabOrderRepository, times(1)).findById(1L);
    }

    @Test
    void getGrabOrderById_NotFound_ThrowsException() {
        when(grabOrderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> grabOrderService.getGrabOrderById(999L));
    }

    @Test
    void deleteGrabOrder_Success() {
        when(grabOrderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(grabOrderRepository).deleteById(1L);

        assertDoesNotThrow(() -> grabOrderService.deleteGrabOrder(1L));
        verify(grabOrderRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteGrabOrder_NotFound_ThrowsException() {
        when(grabOrderRepository.existsById(999L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> grabOrderService.deleteGrabOrder(999L));
        verify(grabOrderRepository, never()).deleteById(any());
    }
}
