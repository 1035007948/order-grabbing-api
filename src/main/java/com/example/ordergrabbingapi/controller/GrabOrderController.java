package com.example.ordergrabbingapi.controller;

import com.example.ordergrabbingapi.entity.GrabOrder;
import com.example.ordergrabbingapi.exception.GrabOrderException;
import com.example.ordergrabbingapi.service.GrabOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grab-orders")
public class GrabOrderController {

    @Autowired
    private GrabOrderService grabOrderService;

    @PostMapping
    public ResponseEntity<GrabOrder> createGrabOrder(@RequestBody GrabOrder grabOrder) {
        GrabOrder createdGrabOrder = grabOrderService.createGrabOrder(grabOrder);
        return ResponseEntity.ok(createdGrabOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrabOrder> getGrabOrderById(@PathVariable Long id) {
        return grabOrderService.getGrabOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<GrabOrder> getAllGrabOrders() {
        return grabOrderService.getAllGrabOrders();
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrabOrder> updateGrabOrder(@PathVariable Long id, @RequestBody GrabOrder grabOrderDetails) {
        GrabOrder updatedGrabOrder = grabOrderService.updateGrabOrder(id, grabOrderDetails);
        return ResponseEntity.ok(updatedGrabOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrabOrder(@PathVariable Long id) {
        grabOrderService.deleteGrabOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/grab")
    public ResponseEntity<Map<String, Object>> grabOrder(@PathVariable Long id, @RequestParam String phoneNumber) {
        try {
            boolean success = grabOrderService.grabOrder(id, phoneNumber);
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Order grabbed successfully!");
                response.put("grabId", id);
                response.put("phoneNumber", phoneNumber);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (GrabOrderException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("errorCode", e.getErrorCode().name());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
