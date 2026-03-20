package com.example.ordergrabbing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "grab_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrabOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;
    
    @Column(name = "stock", nullable = false)
    private Integer stock;
    
    @Column(name = "remaining_stock", nullable = false)
    private Integer remainingStock;
    
    @Version
    private Long version;
}
