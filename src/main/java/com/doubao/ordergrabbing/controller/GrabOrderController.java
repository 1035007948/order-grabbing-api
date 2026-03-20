package com.doubao.ordergrabbing.controller;

import com.doubao.ordergrabbing.dto.ApiResponse;
import com.doubao.ordergrabbing.dto.GrabOrderRequest;
import com.doubao.ordergrabbing.entity.GrabOrder;
import com.doubao.ordergrabbing.service.GrabOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/grab-orders")
@RequiredArgsConstructor
public class GrabOrderController {

    private final GrabOrderService grabOrderService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping
    public ResponseEntity<ApiResponse<GrabOrder>> createGrabOrder(@RequestBody GrabOrderRequest request) {
        GrabOrder grabOrder = new GrabOrder();
        grabOrder.setProductName(request.getProductName());
        grabOrder.setStock(request.getStock());
        grabOrder.setStartTime(LocalDateTime.parse(request.getStartTime(), FORMATTER));
        grabOrder.setEndTime(LocalDateTime.parse(request.getEndTime(), FORMATTER));
        GrabOrder savedGrabOrder = grabOrderService.createGrabOrder(grabOrder);
        return ResponseEntity.ok(ApiResponse.success(savedGrabOrder));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GrabOrder>>> getAllGrabOrders() {
        return ResponseEntity.ok(ApiResponse.success(grabOrderService.getAllGrabOrders()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<GrabOrder>>> getActiveGrabOrders() {
        return ResponseEntity.ok(ApiResponse.success(grabOrderService.getActiveGrabOrders()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GrabOrder>> getGrabOrderById(@PathVariable Long id) {
        return grabOrderService.getGrabOrderById(id)
                .map(grabOrder -> ResponseEntity.ok(ApiResponse.success(grabOrder)))
                .orElse(ResponseEntity.ok(ApiResponse.error("抢单活动不存在")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GrabOrder>> updateGrabOrder(@PathVariable Long id, @RequestBody GrabOrderRequest request) {
        GrabOrder grabOrder = new GrabOrder();
        grabOrder.setProductName(request.getProductName());
        grabOrder.setStock(request.getStock());
        grabOrder.setStartTime(LocalDateTime.parse(request.getStartTime(), FORMATTER));
        grabOrder.setEndTime(LocalDateTime.parse(request.getEndTime(), FORMATTER));
        GrabOrder updatedGrabOrder = grabOrderService.updateGrabOrder(id, grabOrder);
        return ResponseEntity.ok(ApiResponse.success(updatedGrabOrder));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGrabOrder(@PathVariable Long id) {
        grabOrderService.deleteGrabOrder(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
