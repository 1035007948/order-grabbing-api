package com.example.ordergrabbing.repository;

import com.example.ordergrabbing.entity.Grab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrabRepository extends JpaRepository<Grab, Long> {
}
