package com.doubao.ordergrabbing.controller;

import com.doubao.ordergrabbing.dto.ApiResponse;
import com.doubao.ordergrabbing.dto.GrabOrderRequest;
import com.doubao.ordergrabbing.dto.GrabOrderResponse;
import com.doubao.ordergrabbing.service.GrabOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/grab-orders")
public class GrabOrderController {

    private final GrabOrderService grabOrderService;

    public GrabOrderController(GrabOrderService grabOrderService) {
        this.grabOrderService = grabOrderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GrabOrderResponse>> createGrabOrder(@Valid @RequestBody GrabOrderRequest request) {
        GrabOrderResponse response = grabOrderService.createGrabOrder(request);
        return ResponseEntity.ok(ApiResponse.success("抢单活动创建成功", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GrabOrderResponse>>> getAllGrabOrders() {
        List<GrabOrderResponse> grabOrders = grabOrderService.getAllGrabOrders();
        return ResponseEntity.ok(ApiResponse.success(grabOrders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GrabOrderResponse>> getGrabOrderById(@PathVariable Long id) {
        GrabOrderResponse grabOrder = grabOrderService.getGrabOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(grabOrder));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<GrabOrderResponse>>> getActiveGrabOrders() {
        List<GrabOrderResponse> grabOrders = grabOrderService.getActiveGrabOrders();
        return ResponseEntity.ok(ApiResponse.success(grabOrders));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<GrabOrderResponse>>> getAvailableGrabOrders() {
        List<GrabOrderResponse> grabOrders = grabOrderService.getAvailableGrabOrders();
        return ResponseEntity.ok(ApiResponse.success(grabOrders));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GrabOrderResponse>> updateGrabOrder(
            @PathVariable Long id,
            @Valid @RequestBody GrabOrderRequest request) {
        GrabOrderResponse response = grabOrderService.updateGrabOrder(id, request);
        return ResponseEntity.ok(ApiResponse.success("抢单活动更新成功", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGrabOrder(@PathVariable Long id) {
        grabOrderService.deleteGrabOrder(id);
        return ResponseEntity.ok(ApiResponse.success("抢单活动删除成功", null));
    }
}
