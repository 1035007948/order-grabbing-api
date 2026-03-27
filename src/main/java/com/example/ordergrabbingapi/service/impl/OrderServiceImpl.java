package com.example.ordergrabbingapi.service.impl;

import com.example.ordergrabbingapi.entity.GrabOrder;
import com.example.ordergrabbingapi.entity.Order;
import com.example.ordergrabbingapi.exception.GrabOrderException;
import com.example.ordergrabbingapi.repository.GrabOrderRepository;
import com.example.ordergrabbingapi.repository.OrderRepository;
import com.example.ordergrabbingapi.service.OrderService;
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
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private GrabOrderRepository grabOrderRepository;

    @Override
    public Order createOrder(Order order) {
        // Basic validation
        validateOrder(order);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrderWithValidation(String phoneNumber, Long grabId) {
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

        // 5. Check stock availability
        if (grabOrder.getStock() <= 0) {
            logger.warn("Insufficient stock for grabId: {}, current stock: {}", grabId, grabOrder.getStock());
            throw new GrabOrderException(GrabOrderException.ErrorCode.INSUFFICIENT_STOCK);
        }

        // 6. Atomic stock update - using database level lock to prevent oversold
        int updatedRows = grabOrderRepository.decreaseStock(grabId);
        if (updatedRows == 0) {
            logger.warn("Failed to update stock - insufficient stock or stock already 0 for grabId: {}", grabId);
            throw new GrabOrderException(GrabOrderException.ErrorCode.INSUFFICIENT_STOCK);
        }

        // 7. Create order record
        try {
            Order order = new Order();
            order.setPhoneNumber(phoneNumber);
            order.setGrabId(grabId);
            order.setOrderStatus("SUCCESS");
            Order savedOrder = orderRepository.save(order);
            logger.info("Order created successfully: orderId={}, phoneNumber={}, grabId={}",
                    savedOrder.getOrderId(), phoneNumber, grabId);
            return savedOrder;
        } catch (Exception e) {
            logger.error("Failed to create order: phoneNumber={}, grabId={}", phoneNumber, grabId, e);
            throw new GrabOrderException(GrabOrderException.ErrorCode.ORDER_CREATION_FAILED,
                    "Order creation failed: " + e.getMessage());
        }
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order updateOrder(Long id, Order orderDetails) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new GrabOrderException(GrabOrderException.ErrorCode.GRAB_ORDER_NOT_FOUND,
                        "Order not found with id: " + id));

        // Validate the updated order details
        validateOrder(orderDetails);

        order.setPhoneNumber(orderDetails.getPhoneNumber());
        order.setGrabId(orderDetails.getGrabId());
        order.setOrderStatus(orderDetails.getOrderStatus());

        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new GrabOrderException(GrabOrderException.ErrorCode.GRAB_ORDER_NOT_FOUND,
                        "Order not found with id: " + id));
        orderRepository.delete(order);
        logger.info("Order deleted successfully: orderId={}", id);
    }

    @Override
    public List<Order> getOrdersByPhoneNumber(String phoneNumber) {
        return orderRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public List<Order> getOrdersByGrabId(Long grabId) {
        return orderRepository.findByGrabId(grabId);
    }

    /**
     * Validate order information before create/update
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        // Validate phone number
        if (order.getPhoneNumber() == null || order.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        if (!PhoneNumberValidator.isValid(order.getPhoneNumber())) {
            throw new GrabOrderException(GrabOrderException.ErrorCode.INVALID_PHONE_NUMBER);
        }

        // Validate grab ID
        if (order.getGrabId() == null) {
            throw new IllegalArgumentException("Grab ID cannot be null");
        }

        // Validate order status
        if (order.getOrderStatus() == null || order.getOrderStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Order status cannot be empty");
        }

        // Verify grab order exists
        if (!grabOrderRepository.existsById(order.getGrabId())) {
            throw new GrabOrderException(GrabOrderException.ErrorCode.GRAB_ORDER_NOT_FOUND);
        }
    }
}
