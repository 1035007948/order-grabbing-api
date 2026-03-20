package com.doubao.ordergrabbing.repository;

import com.doubao.ordergrabbing.entity.GrabOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GrabOrderRepository extends JpaRepository<GrabOrder, Long> {

    List<GrabOrder> findByStartTimeBeforeAndEndTimeAfter(LocalDateTime startBefore, LocalDateTime endAfter);

    List<GrabOrder> findByEndTimeAfter(LocalDateTime now);
}
