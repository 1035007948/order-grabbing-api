package com.doubao.order.repository;

import com.doubao.order.entity.Order;
import com.doubao.order.entity.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByPhone(String phone);

    List<Order> findByGrabId(Long grabId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByPhoneAndStatus(String phone, OrderStatus status);
}
