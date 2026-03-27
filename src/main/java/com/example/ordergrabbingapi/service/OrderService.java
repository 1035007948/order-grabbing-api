package com.example.ordergrabbingapi.service;

import com.example.ordergrabbingapi.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    /**
     * Create a new order with validation
     * @param order the order to create
     * @return the created order
     */
    Order createOrder(Order order);

    /**
     * Create order with grab order validation and stock management
     * @param phoneNumber user phone number
     * @param grabId grab order ID
     * @return the created order
     */
    Order createOrderWithValidation(String phoneNumber, Long grabId);

    Optional<Order> getOrderById(Long id);
    List<Order> getAllOrders();
    Order updateOrder(Long id, Order orderDetails);
    void deleteOrder(Long id);

    /**
     * Get orders by phone number
     * @param phoneNumber user phone number
     * @return list of orders
     */
    List<Order> getOrdersByPhoneNumber(String phoneNumber);

    /**
     * Get orders by grab ID
     * @param grabId grab order ID
     * @return list of orders
     */
    List<Order> getOrdersByGrabId(Long grabId);
}
