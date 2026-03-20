package com.doubao.ordergrabbing.service;

import com.doubao.ordergrabbing.entity.GrabOrder;
import com.doubao.ordergrabbing.repository.GrabOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GrabOrderService {

    private final GrabOrderRepository grabOrderRepository;

    public GrabOrder createGrabOrder(GrabOrder grabOrder) {
        return grabOrderRepository.save(grabOrder);
    }

    public List<GrabOrder> getAllGrabOrders() {
        return grabOrderRepository.findAll();
    }

    public Optional<GrabOrder> getGrabOrderById(Long id) {
        return grabOrderRepository.findById(id);
    }

    public List<GrabOrder> getActiveGrabOrders() {
        LocalDateTime now = LocalDateTime.now();
        return grabOrderRepository.findByStartTimeBeforeAndEndTimeAfter(now, now);
    }

    public GrabOrder updateGrabOrder(Long id, GrabOrder grabOrderDetails) {
        GrabOrder grabOrder = grabOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("抢单活动不存在: " + id));
        grabOrder.setStartTime(grabOrderDetails.getStartTime());
        grabOrder.setEndTime(grabOrderDetails.getEndTime());
        grabOrder.setProductName(grabOrderDetails.getProductName());
        grabOrder.setStock(grabOrderDetails.getStock());
        return grabOrderRepository.save(grabOrder);
    }

    public void deleteGrabOrder(Long id) {
        grabOrderRepository.deleteById(id);
    }

    public boolean decreaseStock(Long id) {
        GrabOrder grabOrder = grabOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("抢单活动不存在: " + id));
        if (grabOrder.getStock() > 0) {
            grabOrder.setStock(grabOrder.getStock() - 1);
            grabOrderRepository.save(grabOrder);
            return true;
        }
        return false;
    }

    public boolean isActive(Long id) {
        GrabOrder grabOrder = grabOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("抢单活动不存在: " + id));
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(grabOrder.getStartTime()) && now.isBefore(grabOrder.getEndTime());
    }
}
