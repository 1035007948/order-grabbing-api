package com.example.ordergrabbing.controller;

import com.example.ordergrabbing.dto.ApiResponse;
import com.example.ordergrabbing.dto.GrabOrderRequest;
import com.example.ordergrabbing.dto.GrabOrderResponse;
import com.example.ordergrabbing.service.GrabOrderService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grab-orders")
@RequiredArgsConstructor
public class GrabOrderController {
    
    private final GrabOrderService grabOrderService;
    
    @PostMapping
    public ApiResponse<GrabOrderResponse> createGrabOrder(@Valid @RequestBody GrabOrderRequest request) {
        GrabOrderResponse response = grabOrderService.createGrabOrder(request);
        return ApiResponse.success(response);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<GrabOrderResponse> getGrabOrder(@PathVariable Long id) {
        GrabOrderResponse response = grabOrderService.getGrabOrder(id);
        return ApiResponse.success(response);
    }
    
    @GetMapping
    public ApiResponse<List<GrabOrderResponse>> getAllGrabOrders() {
        List<GrabOrderResponse> grabOrders = grabOrderService.getAllGrabOrders();
        return ApiResponse.success(grabOrders);
    }
    
    @GetMapping("/active")
    public ApiResponse<List<GrabOrderResponse>> getActiveGrabOrders() {
        List<GrabOrderResponse> grabOrders = grabOrderService.getActiveGrabOrders();
        return ApiResponse.success(grabOrders);
    }
    
    @GetMapping("/available")
    public ApiResponse<List<GrabOrderResponse>> getAvailableGrabOrders() {
        List<GrabOrderResponse> grabOrders = grabOrderService.getAvailableGrabOrders();
        return ApiResponse.success(grabOrders);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<GrabOrderResponse> updateGrabOrder(@PathVariable Long id, @Valid @RequestBody GrabOrderRequest request) {
        GrabOrderResponse response = grabOrderService.updateGrabOrder(id, request);
        return ApiResponse.success(response);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteGrabOrder(@PathVariable Long id) {
        grabOrderService.deleteGrabOrder(id);
        return ApiResponse.success();
    }
}
