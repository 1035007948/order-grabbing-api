package com.doubao.ordergrabbing.service;

import com.doubao.ordergrabbing.dto.GrabOrderRequest;
import com.doubao.ordergrabbing.dto.GrabOrderResponse;
import com.doubao.ordergrabbing.entity.GrabOrder;
import com.doubao.ordergrabbing.exception.BusinessException;
import com.doubao.ordergrabbing.repository.GrabOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GrabOrderService {

    private final GrabOrderRepository grabOrderRepository;

    public GrabOrderService(GrabOrderRepository grabOrderRepository) {
        this.grabOrderRepository = grabOrderRepository;
    }

    @Transactional
    public GrabOrderResponse createGrabOrder(GrabOrderRequest request) {
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new BusinessException("结束时间不能早于开始时间");
        }
        if (request.getStock() <= 0) {
            throw new BusinessException("库存必须大于0");
        }

        GrabOrder grabOrder = new GrabOrder();
        grabOrder.setStartTime(request.getStartTime());
        grabOrder.setEndTime(request.getEndTime());
        grabOrder.setProductName(request.getProductName());
        grabOrder.setStock(request.getStock());

        GrabOrder savedOrder = grabOrderRepository.save(grabOrder);
        return toGrabOrderResponse(savedOrder);
    }

    public List<GrabOrderResponse> getAllGrabOrders() {
        return grabOrderRepository.findAll().stream()
                .map(this::toGrabOrderResponse)
                .collect(Collectors.toList());
    }

    public GrabOrderResponse getGrabOrderById(Long id) {
        GrabOrder grabOrder = grabOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("抢单活动不存在"));
        return toGrabOrderResponse(grabOrder);
    }

    public List<GrabOrderResponse> getActiveGrabOrders() {
        return grabOrderRepository.findActiveGrabOrders(LocalDateTime.now()).stream()
                .map(this::toGrabOrderResponse)
                .collect(Collectors.toList());
    }

    public List<GrabOrderResponse> getAvailableGrabOrders() {
        return grabOrderRepository.findAvailableGrabOrders().stream()
                .map(this::toGrabOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public GrabOrderResponse updateGrabOrder(Long id, GrabOrderRequest request) {
        GrabOrder grabOrder = grabOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("抢单活动不存在"));

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new BusinessException("结束时间不能早于开始时间");
        }

        grabOrder.setStartTime(request.getStartTime());
        grabOrder.setEndTime(request.getEndTime());
        grabOrder.setProductName(request.getProductName());
        grabOrder.setStock(request.getStock());

        GrabOrder savedOrder = grabOrderRepository.save(grabOrder);
        return toGrabOrderResponse(savedOrder);
    }

    @Transactional
    public void deleteGrabOrder(Long id) {
        if (!grabOrderRepository.existsById(id)) {
            throw new BusinessException("抢单活动不存在");
        }
        grabOrderRepository.deleteById(id);
    }

    private GrabOrderResponse toGrabOrderResponse(GrabOrder grabOrder) {
        return new GrabOrderResponse(
                grabOrder.getId(),
                grabOrder.getStartTime(),
                grabOrder.getEndTime(),
                grabOrder.getProductName(),
                grabOrder.getStock()
        );
    }
}
