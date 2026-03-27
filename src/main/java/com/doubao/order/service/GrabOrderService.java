package com.doubao.order.service;

import com.doubao.order.dto.GrabOrderRequest;
import com.doubao.order.entity.GrabOrder;
import com.doubao.order.exception.BusinessException;
import com.doubao.order.repository.GrabOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GrabOrderService {

    @Autowired
    private GrabOrderRepository grabOrderRepository;

    public List<GrabOrder> findAll() {
        return grabOrderRepository.findAll();
    }

    public GrabOrder findById(Long id) {
        return grabOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Grab order not found with id: " + id));
    }

    @Transactional
    public GrabOrder create(GrabOrderRequest request) {
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new BusinessException("End time must be after start time");
        }

        GrabOrder grabOrder = new GrabOrder();
        grabOrder.setProductName(request.getProductName());
        grabOrder.setStock(request.getStock());
        grabOrder.setStartTime(request.getStartTime());
        grabOrder.setEndTime(request.getEndTime());

        return grabOrderRepository.save(grabOrder);
    }

    @Transactional
    public GrabOrder update(Long id, GrabOrderRequest request) {
        GrabOrder grabOrder = findById(id);

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new BusinessException("End time must be after start time");
        }

        grabOrder.setProductName(request.getProductName());
        grabOrder.setStock(request.getStock());
        grabOrder.setStartTime(request.getStartTime());
        grabOrder.setEndTime(request.getEndTime());

        return grabOrderRepository.save(grabOrder);
    }

    @Transactional
    public void delete(Long id) {
        GrabOrder grabOrder = findById(id);
        grabOrderRepository.delete(grabOrder);
    }

    @Transactional
    public synchronized boolean decreaseStock(Long id) {
        GrabOrder grabOrder = findById(id);
        if (grabOrder.getStock() <= 0) {
            return false;
        }
        grabOrder.setStock(grabOrder.getStock() - 1);
        grabOrderRepository.save(grabOrder);
        return true;
    }
}
