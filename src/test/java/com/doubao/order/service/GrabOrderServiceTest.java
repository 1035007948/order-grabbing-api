package com.doubao.order.service;

import com.doubao.order.dto.GrabOrderRequest;
import com.doubao.order.entity.GrabOrder;
import com.doubao.order.exception.BusinessException;
import com.doubao.order.repository.GrabOrderRepository;
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
class GrabOrderServiceTest {

    @Autowired
    private GrabOrderService grabOrderService;

    @Autowired
    private GrabOrderRepository grabOrderRepository;

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
    }

    @Test
    void testFindAll() {
        List<GrabOrder> grabOrders = grabOrderService.findAll();
        assertFalse(grabOrders.isEmpty());
        assertEquals(1, grabOrders.size());
    }

    @Test
    void testFindById() {
        GrabOrder found = grabOrderService.findById(testGrabOrder.getId());
        assertNotNull(found);
        assertEquals("Test Product", found.getProductName());
    }

    @Test
    void testFindByIdNotFound() {
        assertThrows(BusinessException.class, () -> grabOrderService.findById(999L));
    }

    @Test
    void testCreate() {
        GrabOrderRequest request = new GrabOrderRequest();
        request.setProductName("New Product");
        request.setStock(5);
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusHours(2));

        GrabOrder created = grabOrderService.create(request);
        assertNotNull(created.getId());
        assertEquals("New Product", created.getProductName());
        assertEquals(5, created.getStock());
    }

    @Test
    void testCreateInvalidTimeRange() {
        GrabOrderRequest request = new GrabOrderRequest();
        request.setProductName("Invalid Product");
        request.setStock(5);
        request.setStartTime(LocalDateTime.now().plusHours(2));
        request.setEndTime(LocalDateTime.now());

        assertThrows(BusinessException.class, () -> grabOrderService.create(request));
    }

    @Test
    void testUpdate() {
        GrabOrderRequest request = new GrabOrderRequest();
        request.setProductName("Updated Product");
        request.setStock(20);
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusHours(3));

        GrabOrder updated = grabOrderService.update(testGrabOrder.getId(), request);
        assertEquals("Updated Product", updated.getProductName());
        assertEquals(20, updated.getStock());
    }

    @Test
    void testDelete() {
        Long id = testGrabOrder.getId();
        grabOrderService.delete(id);
        assertFalse(grabOrderRepository.existsById(id));
    }

    @Test
    void testDecreaseStock() {
        int initialStock = testGrabOrder.getStock();
        boolean result = grabOrderService.decreaseStock(testGrabOrder.getId());
        assertTrue(result);
        
        GrabOrder updated = grabOrderRepository.findById(testGrabOrder.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(initialStock - 1, updated.getStock());
    }

    @Test
    void testDecreaseStockWhenZero() {
        testGrabOrder.setStock(0);
        grabOrderRepository.save(testGrabOrder);
        
        boolean result = grabOrderService.decreaseStock(testGrabOrder.getId());
        assertFalse(result);
    }
}
