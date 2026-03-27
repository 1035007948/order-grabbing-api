package com.example.ordergrabbingapi.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "grab_orders")
public class GrabOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grabId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, length = 100)
    private String productName;

    @Column(nullable = false)
    private Integer stock;
}
