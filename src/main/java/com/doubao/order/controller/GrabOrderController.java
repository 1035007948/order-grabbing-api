package com.doubao.order.controller;

import com.doubao.order.common.ApiResponse;
import com.doubao.order.dto.GrabOrderRequest;
import com.doubao.order.entity.GrabOrder;
import com.doubao.order.service.GrabOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/grab-orders")
public class GrabOrderController {

    @Autowired
    private GrabOrderService grabOrderService;

    @GetMapping
    public ApiResponse<List<GrabOrder>> getAllGrabOrders() {
        List<GrabOrder> grabOrders = grabOrderService.findAll();
        return ApiResponse.success(grabOrders);
    }

    @GetMapping("/{id}")
    public ApiResponse<GrabOrder> getGrabOrderById(@PathVariable Long id) {
        GrabOrder grabOrder = grabOrderService.findById(id);
        return ApiResponse.success(grabOrder);
    }

    @PostMapping
    public ApiResponse<GrabOrder> createGrabOrder(@Valid @RequestBody GrabOrderRequest request) {
        GrabOrder grabOrder = grabOrderService.create(request);
        return ApiResponse.success("Grab order created successfully", grabOrder);
    }

    @PutMapping("/{id}")
    public ApiResponse<GrabOrder> updateGrabOrder(@PathVariable Long id, @Valid @RequestBody GrabOrderRequest request) {
        GrabOrder grabOrder = grabOrderService.update(id, request);
        return ApiResponse.success("Grab order updated successfully", grabOrder);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteGrabOrder(@PathVariable Long id) {
        grabOrderService.delete(id);
        return ApiResponse.success("Grab order deleted successfully", null);
    }
}
