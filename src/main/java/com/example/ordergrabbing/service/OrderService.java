package com.example.ordergrabbing.service;

import com.example.ordergrabbing.entity.Grab;
import com.example.ordergrabbing.entity.Member;
import com.example.ordergrabbing.entity.Order;
import com.example.ordergrabbing.repository.GrabRepository;
import com.example.ordergrabbing.repository.MemberRepository;
import com.example.ordergrabbing.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private GrabRepository grabRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public Order createOrder(Order order) {
        // Validate member exists
        Member member = memberRepository.findById(order.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + order.getMemberId()));

        // Validate grab exists and is active
        Grab grab = grabRepository.findById(order.getGrabId())
                .orElseThrow(() -> new RuntimeException("Grab not found with id: " + order.getGrabId()));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(grab.getStartTime()) || now.isAfter(grab.getEndTime())) {
            throw new RuntimeException("Grab is not active");
        }

        // Check stock availability
        long orderCount = orderRepository.countByGrabId(order.getGrabId());
        if (orderCount >= grab.getStock()) {
            throw new RuntimeException("Stock is exhausted");
        }

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByMemberId(Long memberId) {
        return orderRepository.findByMemberId(memberId);
    }

    public List<Order> getOrdersByGrabId(Long grabId) {
        return orderRepository.findByGrabId(grabId);
    }

    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        orderRepository.delete(order);
    }
}
