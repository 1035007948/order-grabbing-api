package com.doubao.order.repository;

import com.doubao.order.entity.GrabOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrabOrderRepository extends JpaRepository<GrabOrder, Long> {
}
