package com.doubao.order.controller;

import com.doubao.order.common.ApiResponse;
import com.doubao.order.dto.OrderRequest;
import com.doubao.order.entity.Order;
import com.doubao.order.entity.Order.OrderStatus;
import com.doubao.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ApiResponse<List<Order>> getAllOrders() {
        List<Order> orders = orderService.findAll();
        return ApiResponse.success(orders);
    }

    @GetMapping("/{id}")
    public ApiResponse<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return ApiResponse.success(order);
    }

    @GetMapping("/phone/{phone}")
    public ApiResponse<List<Order>> getOrdersByPhone(@PathVariable String phone) {
        List<Order> orders = orderService.findByPhone(phone);
        return ApiResponse.success(orders);
    }

    @GetMapping("/grab/{grabId}")
    public ApiResponse<List<Order>> getOrdersByGrabId(@PathVariable Long grabId) {
        List<Order> orders = orderService.findByGrabId(grabId);
        return ApiResponse.success(orders);
    }

    @PostMapping("/grab")
    public ApiResponse<Order> grabOrder(@Valid @RequestBody OrderRequest request) {
        Order order = orderService.grabOrder(request);
        return ApiResponse.success("Order grabbed successfully", order);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Order> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        Order order = orderService.updateStatus(id, status);
        return ApiResponse.success("Order status updated successfully", order);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return ApiResponse.success("Order deleted successfully", null);
    }
}
