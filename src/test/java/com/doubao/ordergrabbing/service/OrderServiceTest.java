package com.doubao.ordergrabbing.service;

import com.doubao.ordergrabbing.dto.OrderRequest;
import com.doubao.ordergrabbing.dto.OrderResponse;
import com.doubao.ordergrabbing.entity.GrabOrder;
import com.doubao.ordergrabbing.entity.Order;
import com.doubao.ordergrabbing.entity.OrderStatus;
import com.doubao.ordergrabbing.exception.BusinessException;
import com.doubao.ordergrabbing.repository.GrabOrderRepository;
import com.doubao.ordergrabbing.repository.OrderRepository;
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
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private GrabOrderRepository grabOrderRepository;

    @InjectMocks
    private OrderService orderService;

    private GrabOrder grabOrder;
    private Order order;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        grabOrder = new GrabOrder();
        grabOrder.setId(1L);
        grabOrder.setStartTime(LocalDateTime.now().minusHours(1));
        grabOrder.setEndTime(LocalDateTime.now().plusHours(1));
        grabOrder.setProductName("测试商品");
        grabOrder.setStock(10);

        order = new Order();
        order.setId(1L);
        order.setPhone("13800138000");
        order.setGrabOrderId(1L);
        order.setStatus(OrderStatus.SUCCESS);
        order.setCreateTime(LocalDateTime.now());

        orderRequest = new OrderRequest();
        orderRequest.setPhone("13800138000");
        orderRequest.setGrabOrderId(1L);
    }

    @Test
    void createOrder_Success() {
        when(grabOrderRepository.findById(1L)).thenReturn(Optional.of(grabOrder));
        when(grabOrderRepository.save(any(GrabOrder.class))).thenReturn(grabOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = orderService.createOrder(orderRequest);

        assertNotNull(response);
        assertEquals("13800138000", response.getPhone());
        assertEquals(OrderStatus.SUCCESS, response.getStatus());
        verify(grabOrderRepository, times(1)).save(any(GrabOrder.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_GrabOrderNotFound_ThrowsException() {
        when(grabOrderRepository.findById(999L)).thenReturn(Optional.empty());
        orderRequest.setGrabOrderId(999L);

        assertThrows(BusinessException.class, () -> orderService.createOrder(orderRequest));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_GrabOrderNotStarted_ThrowsException() {
        grabOrder.setStartTime(LocalDateTime.now().plusHours(1));
        when(grabOrderRepository.findById(1L)).thenReturn(Optional.of(grabOrder));

        assertThrows(BusinessException.class, () -> orderService.createOrder(orderRequest));
    }

    @Test
    void createOrder_GrabOrderEnded_ThrowsException() {
        grabOrder.setEndTime(LocalDateTime.now().minusHours(1));
        when(grabOrderRepository.findById(1L)).thenReturn(Optional.of(grabOrder));

        assertThrows(BusinessException.class, () -> orderService.createOrder(orderRequest));
    }

    @Test
    void createOrder_NoStock_ThrowsException() {
        grabOrder.setStock(0);
        when(grabOrderRepository.findById(1L)).thenReturn(Optional.of(grabOrder));

        assertThrows(BusinessException.class, () -> orderService.createOrder(orderRequest));
    }

    @Test
    void getAllOrders_Success() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));

        List<OrderResponse> responses = orderService.getAllOrders();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getOrderById_NotFound_ThrowsException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void getOrdersByPhone_Success() {
        when(orderRepository.findByPhone("13800138000")).thenReturn(Arrays.asList(order));

        List<OrderResponse> responses = orderService.getOrdersByPhone("13800138000");

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void cancelOrder_Success() {
        order.setStatus(OrderStatus.SUCCESS);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(grabOrderRepository.findById(1L)).thenReturn(Optional.of(grabOrder));
        when(grabOrderRepository.save(any(GrabOrder.class))).thenReturn(grabOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, response.getStatus());
        verify(grabOrderRepository, times(1)).save(any(GrabOrder.class));
    }

    @Test
    void cancelOrder_AlreadyCancelled_ThrowsException() {
        order.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    void deleteOrder_Success() {
        order.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).deleteById(1L);

        assertDoesNotThrow(() -> orderService.deleteOrder(1L));
        verify(orderRepository, times(1)).deleteById(1L);
    }
}
