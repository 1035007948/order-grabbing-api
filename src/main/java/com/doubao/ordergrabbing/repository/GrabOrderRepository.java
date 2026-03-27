package com.doubao.ordergrabbing.repository;

import com.doubao.ordergrabbing.entity.GrabOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GrabOrderRepository extends JpaRepository<GrabOrder, Long> {

    List<GrabOrder> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<GrabOrder> findByEndTimeAfter(LocalDateTime time);

    @Query("SELECT g FROM GrabOrder g WHERE g.startTime <= :now AND g.endTime >= :now")
    List<GrabOrder> findActiveGrabOrders(@Param("now") LocalDateTime now);

    @Query("SELECT g FROM GrabOrder g WHERE g.stock > 0")
    List<GrabOrder> findAvailableGrabOrders();
}
