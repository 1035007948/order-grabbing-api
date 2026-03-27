package com.doubao.order.service;

import com.doubao.order.dto.OrderRequest;
import com.doubao.order.entity.GrabOrder;
import com.doubao.order.entity.Order;
import com.doubao.order.entity.Order.OrderStatus;
import com.doubao.order.exception.BusinessException;
import com.doubao.order.repository.GrabOrderRepository;
import com.doubao.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private GrabOrderRepository grabOrderRepository;

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
    }

    @Test
    void testFindAll() {
        List<Order> orders = orderService.findAll();
        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
    }

    @Test
    void testFindById() {
        Order found = orderService.findById(testOrder.getId());
        assertNotNull(found);
        assertEquals("13800138000", found.getPhone());
    }

    @Test
    void testFindByIdNotFound() {
        assertThrows(BusinessException.class, () -> orderService.findById(999L));
    }

    @Test
    void testFindByPhone() {
        List<Order> orders = orderService.findByPhone("13800138000");
        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
    }

    @Test
    void testFindByGrabId() {
        List<Order> orders = orderService.findByGrabId(testGrabOrder.getId());
        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
    }

    @Test
    void testGrabOrder() {
        OrderRequest request = new OrderRequest();
        request.setPhone("13900139000");
        request.setGrabId(testGrabOrder.getId());

        Order grabbed = orderService.grabOrder(request);
        assertNotNull(grabbed.getId());
        assertEquals("13900139000", grabbed.getPhone());
        assertEquals(OrderStatus.GRABBED, grabbed.getStatus());
    }

    @Test
    void testGrabOrderOutOfStock() {
        testGrabOrder.setStock(0);
        grabOrderRepository.save(testGrabOrder);

        OrderRequest request = new OrderRequest();
        request.setPhone("13900139000");
        request.setGrabId(testGrabOrder.getId());

        assertThrows(BusinessException.class, () -> orderService.grabOrder(request));
    }

    @Test
    void testUpdateStatus() {
        Order updated = orderService.updateStatus(testOrder.getId(), OrderStatus.COMPLETED);
        assertEquals(OrderStatus.COMPLETED, updated.getStatus());
    }

    @Test
    void testDelete() {
        Long id = testOrder.getId();
        orderService.delete(id);
        assertFalse(orderRepository.existsById(id));
    }
}
