package com.example.ordergrabbing.repository;

import com.example.ordergrabbing.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByMemberId(Long memberId);
    
    List<Order> findByGrabOrderId(Long grabOrderId);
    
    boolean existsByMemberIdAndGrabOrderId(Long memberId, Long grabOrderId);
}
