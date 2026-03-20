package com.example.ordergrabbing.service;

import com.example.ordergrabbing.dto.*;
import com.example.ordergrabbing.entity.GrabOrder;
import com.example.ordergrabbing.entity.Member;
import com.example.ordergrabbing.entity.Order;
import com.example.ordergrabbing.enums.OrderStatus;
import com.example.ordergrabbing.exception.BusinessException;
import com.example.ordergrabbing.repository.GrabOrderRepository;
import com.example.ordergrabbing.repository.MemberRepository;
import com.example.ordergrabbing.repository.OrderRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final GrabOrderRepository grabOrderRepository;
    
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("订单不存在: " + id));
        return convertToResponse(order);
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByMember(Long memberId) {
        return orderRepository.findByMemberId(memberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByGrabOrder(Long grabOrderId) {
        return orderRepository.findByGrabOrderId(grabOrderId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public GrabResponse grabOrder(GrabRequest request) {
        Long memberId = request.getMemberId();
        Long grabOrderId = request.getGrabOrderId();
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("会员不存在: " + memberId));
        
        GrabOrder grabOrder = grabOrderRepository.findByIdWithLock(grabOrderId)
                .orElseThrow(() -> new EntityNotFoundException("抢单活动不存在: " + grabOrderId));
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(grabOrder.getStartTime())) {
            return GrabResponse.builder()
                    .success(false)
                    .message("抢单活动尚未开始")
                    .build();
        }
        
        if (now.isAfter(grabOrder.getEndTime())) {
            return GrabResponse.builder()
                    .success(false)
                    .message("抢单活动已结束")
                    .build();
        }
        
        if (grabOrder.getRemainingStock() <= 0) {
            return GrabResponse.builder()
                    .success(false)
                    .message("商品库存不足")
                    .build();
        }
        
        if (orderRepository.existsByMemberIdAndGrabOrderId(memberId, grabOrderId)) {
            return GrabResponse.builder()
                    .success(false)
                    .message("您已经参与过该抢单活动")
                    .build();
        }
        
        grabOrder.setRemainingStock(grabOrder.getRemainingStock() - 1);
        grabOrderRepository.save(grabOrder);
        
        Order order = Order.builder()
                .memberId(memberId)
                .grabOrderId(grabOrderId)
                .amount(BigDecimal.valueOf(99.99))
                .status(OrderStatus.SUCCESS)
                .build();
        
        Order savedOrder = orderRepository.save(order);
        
        log.info("会员 {} 成功抢到商品 {}, 订单号: {}", member.getNickname(), grabOrder.getProductName(), savedOrder.getId());
        
        return GrabResponse.builder()
                .success(true)
                .message("抢单成功")
                .orderId(savedOrder.getId())
                .build();
    }
    
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("订单不存在: " + id);
        }
        orderRepository.deleteById(id);
    }
    
    private OrderResponse convertToResponse(Order order) {
        Member member = memberRepository.findById(order.getMemberId()).orElse(null);
        GrabOrder grabOrder = grabOrderRepository.findById(order.getGrabOrderId()).orElse(null);
        
        return OrderResponse.builder()
                .id(order.getId())
                .memberId(order.getMemberId())
                .memberNickname(member != null ? member.getNickname() : "未知")
                .grabOrderId(order.getGrabOrderId())
                .productName(grabOrder != null ? grabOrder.getProductName() : "未知")
                .amount(order.getAmount())
                .status(order.getStatus())
                .statusDescription(order.getStatus().getDescription())
                .createTime(order.getCreateTime())
                .build();
    }
}
