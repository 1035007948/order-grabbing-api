package com.doubao.ordergrabbing.repository;

import com.doubao.ordergrabbing.entity.Order;
import com.doubao.ordergrabbing.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByGrabOrderId(Long grabOrderId);

    List<Order> findByPhone(String phone);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByGrabOrderIdAndStatus(Long grabOrderId, OrderStatus status);
}
