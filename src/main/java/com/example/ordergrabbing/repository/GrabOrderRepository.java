package com.example.ordergrabbing.repository;

import com.example.ordergrabbing.entity.GrabOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GrabOrderRepository extends JpaRepository<GrabOrder, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM GrabOrder g WHERE g.id = :id")
    Optional<GrabOrder> findByIdWithLock(@Param("id") Long id);
    
    List<GrabOrder> findByStartTimeBeforeAndEndTimeAfter(LocalDateTime now1, LocalDateTime now2);
    
    List<GrabOrder> findByRemainingStockGreaterThan(Integer stock);
}
