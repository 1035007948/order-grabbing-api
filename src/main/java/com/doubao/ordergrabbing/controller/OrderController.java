package com.doubao.ordergrabbing.controller;

import com.doubao.ordergrabbing.dto.ApiResponse;
import com.doubao.ordergrabbing.dto.OrderRequest;
import com.doubao.ordergrabbing.dto.OrderResponse;
import com.doubao.ordergrabbing.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(ApiResponse.success("抢单成功", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByPhone(@PathVariable String phone) {
        List<OrderResponse> orders = orderService.getOrdersByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/grab-order/{grabOrderId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByGrabOrderId(@PathVariable Long grabOrderId) {
        List<OrderResponse> orders = orderService.getOrdersByGrabOrderId(grabOrderId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
        OrderResponse response = orderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success("订单取消成功", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success("订单删除成功", null));
    }
}
