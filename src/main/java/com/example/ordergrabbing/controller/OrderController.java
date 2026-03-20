package com.example.ordergrabbing.controller;

import com.example.ordergrabbing.dto.ApiResponse;
import com.example.ordergrabbing.dto.GrabRequest;
import com.example.ordergrabbing.dto.GrabResponse;
import com.example.ordergrabbing.dto.OrderResponse;
import com.example.ordergrabbing.service.OrderService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping("/grab")
    public ApiResponse<GrabResponse> grabOrder(@Valid @RequestBody GrabRequest request) {
        GrabResponse response = orderService.grabOrder(request);
        return ApiResponse.success(response);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long id) {
        OrderResponse response = orderService.getOrder(id);
        return ApiResponse.success(response);
    }
    
    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ApiResponse.success(orders);
    }
    
    @GetMapping("/member/{memberId}")
    public ApiResponse<List<OrderResponse>> getOrdersByMember(@PathVariable Long memberId) {
        List<OrderResponse> orders = orderService.getOrdersByMember(memberId);
        return ApiResponse.success(orders);
    }
    
    @GetMapping("/grab-order/{grabOrderId}")
    public ApiResponse<List<OrderResponse>> getOrdersByGrabOrder(@PathVariable Long grabOrderId) {
        List<OrderResponse> orders = orderService.getOrdersByGrabOrder(grabOrderId);
        return ApiResponse.success(orders);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ApiResponse.success();
    }
}
