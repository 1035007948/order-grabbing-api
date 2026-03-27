package com.example.ordergrabbingapi.service.impl;

import com.example.ordergrabbingapi.entity.GrabOrder;
import com.example.ordergrabbingapi.entity.Order;
import com.example.ordergrabbingapi.exception.GrabOrderException;
import com.example.ordergrabbingapi.repository.GrabOrderRepository;
import com.example.ordergrabbingapi.repository.OrderRepository;
import com.example.ordergrabbingapi.service.GrabOrderService;
import com.example.ordergrabbingapi.util.PhoneNumberValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GrabOrderServiceImpl implements GrabOrderService {

    private static final Logger logger = LoggerFactory.getLogger(GrabOrderServiceImpl.class);

    @Autowired
    private GrabOrderRepository grabOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public GrabOrder createGrabOrder(GrabOrder grabOrder) {
        validateGrabOrder(grabOrder);
        return grabOrderRepository.save(grabOrder);
    }

    @Override
    public Optional<GrabOrder> getGrabOrderById(Long id) {
        return grabOrderRepository.findById(id);
    }

    @Override
    public List<GrabOrder> getAllGrabOrders() {
        return grabOrderRepository.findAll();
    }

    @Override
    public GrabOrder updateGrabOrder(Long id, GrabOrder grabOrderDetails) {
        GrabOrder grabOrder = grabOrderRepository.findById(id)
                .orElseThrow(() -> new GrabOrderException(GrabOrderException.ErrorCode.GRAB_ORDER_NOT_FOUND));

        validateGrabOrder(grabOrderDetails);

        grabOrder.setStartTime(grabOrderDetails.getStartTime());
        grabOrder.setEndTime(grabOrderDetails.getEndTime());
        grabOrder.setProductName(grabOrderDetails.getProductName());
        grabOrder.setStock(grabOrderDetails.getStock());

        return grabOrderRepository.save(grabOrder);
    }

    @Override
    public void deleteGrabOrder(Long id) {
        GrabOrder grabOrder = grabOrderRepository.findById(id)
                .orElseThrow(() -> new GrabOrderException(GrabOrderException.ErrorCode.GRAB_ORDER_NOT_FOUND));
        grabOrderRepository.delete(grabOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean grabOrder(Long grabId, String phoneNumber) {
        // 1. Validate phone number format
        if (!PhoneNumberValidator.isValid(phoneNumber)) {
            logger.warn("Invalid phone number format: {}", phoneNumber);
            throw new GrabOrderException(GrabOrderException.ErrorCode.INVALID_PHONE_NUMBER);
        }

        // 2. Check if grab order exists
        GrabOrder grabOrder = grabOrderRepository.findById(grabId)
                .orElseThrow(() -> new GrabOrderException(GrabOrderException.ErrorCode.GRAB_ORDER_NOT_FOUND));

        // 3. Validate grab time window
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(grabOrder.getStartTime())) {
            logger.info("Grab activity has not started yet. Current time: {}, Start time: {}",
                    now, grabOrder.getStartTime());
            throw new GrabOrderException(GrabOrderException.ErrorCode.GRAB_NOT_STARTED);
        }
        if (now.isAfter(grabOrder.getEndTime())) {
            logger.info("Grab activity has ended. Current time: {}, End time: {}",
                    now, grabOrder.getEndTime());
            throw new GrabOrderException(GrabOrderException.ErrorCode.GRAB_ENDED);
        }

        // 4. Check for duplicate orders (one phone number can only grab once per grab order)
        if (orderRepository.existsByPhoneNumberAndGrabId(phoneNumber, grabId)) {
            logger.warn("Duplicate order attempt: phoneNumber={}, grabId={}", phoneNumber, grabId);
            throw new GrabOrderException(GrabOrderException.ErrorCode.DUPLICATE_ORDER);
        }

        // 5. Atomic stock update - using database level lock to prevent oversold
        int updatedRows = grabOrderRepository.decreaseStock(grabId);
        if (updatedRows == 0) {
            logger.warn("Failed to update stock - insufficient stock or stock already 0 for grabId: {}", grabId);
            throw new GrabOrderException(GrabOrderException.ErrorCode.INSUFFICIENT_STOCK);
        }

        // 6. Create order record
        try {
            Order order = new Order();
            order.setPhoneNumber(phoneNumber);
            order.setGrabId(grabId);
            order.setOrderStatus("SUCCESS");
            orderRepository.save(order);
            logger.info("Order created successfully: phoneNumber={}, grabId={}", phoneNumber, grabId);
        } catch (Exception e) {
            logger.error("Failed to create order: phoneNumber={}, grabId={}", phoneNumber, grabId, e);
            throw new GrabOrderException(GrabOrderException.ErrorCode.ORDER_CREATION_FAILED,
                    "Order creation failed: " + e.getMessage());
        }

        return true;
    }

    /**
     * Validate grab order information before create/update
     */
    private void validateGrabOrder(GrabOrder grabOrder) {
        if (grabOrder.getStartTime() == null || grabOrder.getEndTime() == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null");
        }
        if (grabOrder.getEndTime().isBefore(grabOrder.getStartTime())) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        if (grabOrder.getProductName() == null || grabOrder.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (grabOrder.getStock() == null || grabOrder.getStock() < 0) {
            throw new IllegalArgumentException("Stock must be greater than or equal to 0");
        }
    }
}
