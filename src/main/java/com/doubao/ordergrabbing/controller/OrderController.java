package com.doubao.ordergrabbing.controller;

import com.doubao.ordergrabbing.dto.ApiResponse;
import com.doubao.ordergrabbing.dto.GrabRequest;
import com.doubao.ordergrabbing.entity.Order;
import com.doubao.ordergrabbing.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/grab/{grabOrderId}")
    public ResponseEntity<ApiResponse<Order>> grabOrder(
            @PathVariable Long grabOrderId,
            @RequestBody GrabRequest request) {
        try {
            Order order = orderService.grabOrder(request.getMemberId(), grabOrderId, request.getAmount());
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok(ApiResponse.success(order)))
                .orElse(ResponseEntity.ok(ApiResponse.error("订单不存在")));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByMemberId(@PathVariable Long memberId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrdersByMemberId(memberId)));
    }

    @GetMapping("/grab-order/{grabOrderId}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByGrabOrderId(@PathVariable Long grabOrderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrdersByGrabOrderId(grabOrderId)));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<Order>> payOrder(@PathVariable Long id) {
        try {
            Order order = orderService.payOrder(id);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(@PathVariable Long id) {
        try {
            Order order = orderService.cancelOrder(id);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
