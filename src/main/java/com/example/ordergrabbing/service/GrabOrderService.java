package com.example.ordergrabbing.service;

import com.example.ordergrabbing.dto.GrabOrderRequest;
import com.example.ordergrabbing.dto.GrabOrderResponse;
import com.example.ordergrabbing.entity.GrabOrder;
import com.example.ordergrabbing.repository.GrabOrderRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrabOrderService {
    
    private final GrabOrderRepository grabOrderRepository;
    
    @Transactional
    public GrabOrderResponse createGrabOrder(GrabOrderRequest request) {
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("结束时间必须晚于开始时间");
        }
        
        GrabOrder grabOrder = GrabOrder.builder()
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .productName(request.getProductName())
                .stock(request.getStock())
                .remainingStock(request.getStock())
                .build();
        
        GrabOrder savedGrabOrder = grabOrderRepository.save(grabOrder);
        return convertToResponse(savedGrabOrder);
    }
    
    @Transactional(readOnly = true)
    public GrabOrderResponse getGrabOrder(Long id) {
        GrabOrder grabOrder = grabOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("抢单活动不存在: " + id));
        return convertToResponse(grabOrder);
    }
    
    @Transactional(readOnly = true)
    public List<GrabOrderResponse> getAllGrabOrders() {
        return grabOrderRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<GrabOrderResponse> getActiveGrabOrders() {
        LocalDateTime now = LocalDateTime.now();
        return grabOrderRepository.findByStartTimeBeforeAndEndTimeAfter(now, now).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<GrabOrderResponse> getAvailableGrabOrders() {
        return grabOrderRepository.findByRemainingStockGreaterThan(0).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public GrabOrderResponse updateGrabOrder(Long id, GrabOrderRequest request) {
        GrabOrder grabOrder = grabOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("抢单活动不存在: " + id));
        
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("结束时间必须晚于开始时间");
        }
        
        grabOrder.setStartTime(request.getStartTime());
        grabOrder.setEndTime(request.getEndTime());
        grabOrder.setProductName(request.getProductName());
        
        int stockDiff = request.getStock() - grabOrder.getStock();
        grabOrder.setStock(request.getStock());
        grabOrder.setRemainingStock(Math.max(0, grabOrder.getRemainingStock() + stockDiff));
        
        GrabOrder updatedGrabOrder = grabOrderRepository.save(grabOrder);
        return convertToResponse(updatedGrabOrder);
    }
    
    @Transactional
    public void deleteGrabOrder(Long id) {
        if (!grabOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("抢单活动不存在: " + id);
        }
        grabOrderRepository.deleteById(id);
    }
    
    private GrabOrderResponse convertToResponse(GrabOrder grabOrder) {
        LocalDateTime now = LocalDateTime.now();
        boolean isActive = now.isAfter(grabOrder.getStartTime()) && now.isBefore(grabOrder.getEndTime());
        
        return GrabOrderResponse.builder()
                .id(grabOrder.getId())
                .startTime(grabOrder.getStartTime())
                .endTime(grabOrder.getEndTime())
                .productName(grabOrder.getProductName())
                .stock(grabOrder.getStock())
                .remainingStock(grabOrder.getRemainingStock())
                .active(isActive && grabOrder.getRemainingStock() > 0)
                .build();
    }
}
