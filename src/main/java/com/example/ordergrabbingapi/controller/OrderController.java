package com.example.ordergrabbingapi.controller;

import com.example.ordergrabbingapi.entity.Order;
import com.example.ordergrabbingapi.exception.GrabOrderException;
import com.example.ordergrabbingapi.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Create a new order (basic)
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }

    /**
     * Create order with full validation and stock management
     * This is the recommended endpoint for order creation
     */
    @PostMapping("/create-with-validation")
    public ResponseEntity<Map<String, Object>> createOrderWithValidation(
            @RequestParam String phoneNumber,
            @RequestParam Long grabId) {

        try {
            Order order = orderService.createOrderWithValidation(phoneNumber, grabId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order created successfully!");
            response.put("orderId", order.getOrderId());
            response.put("phoneNumber", order.getPhoneNumber());
            response.put("grabId", order.getGrabId());
            response.put("orderStatus", order.getOrderStatus());
            response.put("createTime", order.getCreateTime());

            return ResponseEntity.ok(response);
        } catch (GrabOrderException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("errorCode", e.getErrorCode().name());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all orders
     */
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * Get orders by phone number
     */
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<List<Order>> getOrdersByPhoneNumber(@PathVariable String phoneNumber) {
        List<Order> orders = orderService.getOrdersByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get orders by grab ID
     */
    @GetMapping("/grab/{grabId}")
    public ResponseEntity<List<Order>> getOrdersByGrabId(@PathVariable Long grabId) {
        List<Order> orders = orderService.getOrdersByGrabId(grabId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Update an existing order
     */
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        try {
            Order updatedOrder = orderService.updateOrder(id, orderDetails);
            return ResponseEntity.ok(updatedOrder);
        } catch (GrabOrderException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete an order
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (GrabOrderException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
