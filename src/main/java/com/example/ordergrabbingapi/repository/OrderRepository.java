package com.example.ordergrabbingapi.repository;

import com.example.ordergrabbingapi.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Check if an order already exists for the same phone number and grab order
     * @param phoneNumber the phone number
     * @param grabId the grab order ID
     * @return true if duplicate order exists
     */
    boolean existsByPhoneNumberAndGrabId(String phoneNumber, Long grabId);

    /**
     * Find orders by phone number
     * @param phoneNumber the phone number
     * @return list of orders
     */
    List<Order> findByPhoneNumber(String phoneNumber);

    /**
     * Find orders by grab ID
     * @param grabId the grab order ID
     * @return list of orders
     */
    List<Order> findByGrabId(Long grabId);

    /**
     * Count orders by grab ID
     * @param grabId the grab order ID
     * @return count of orders
     */
    long countByGrabId(Long grabId);
}
