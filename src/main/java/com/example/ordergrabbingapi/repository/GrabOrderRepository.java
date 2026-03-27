package com.example.ordergrabbingapi.repository;

import com.example.ordergrabbingapi.entity.GrabOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GrabOrderRepository extends JpaRepository<GrabOrder, Long> {

    /**
     * Atomically decrease stock by 1 if stock > 0
     * @param grabId the grab order ID
     * @return number of rows affected
     */
    @Modifying
    @Query("UPDATE GrabOrder g SET g.stock = g.stock - 1 WHERE g.grabId = :grabId AND g.stock > 0")
    int decreaseStock(@Param("grabId") Long grabId);
}
