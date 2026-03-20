package com.doubao.ordergrabbing.repository;

import com.doubao.ordergrabbing.entity.Order;
import com.doubao.ordergrabbing.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByMemberId(Long memberId);

    List<Order> findByGrabOrderId(Long grabOrderId);

    List<Order> findByStatus(OrderStatus status);

    long countByGrabOrderIdAndStatus(Long grabOrderId, OrderStatus status);
}
