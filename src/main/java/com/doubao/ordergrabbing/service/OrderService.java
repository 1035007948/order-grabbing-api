package com.doubao.ordergrabbing.service;

import com.doubao.ordergrabbing.entity.GrabOrder;
import com.doubao.ordergrabbing.entity.Order;
import com.doubao.ordergrabbing.entity.OrderStatus;
import com.doubao.ordergrabbing.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final GrabOrderService grabOrderService;

    public Order createOrder(Order order) {
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

    public List<Order> getOrdersByGrabOrderId(Long grabOrderId) {
        return orderRepository.findByGrabOrderId(grabOrderId);
    }

    @Transactional
    public Order grabOrder(Long memberId, Long grabOrderId, BigDecimal amount) {
        if (!grabOrderService.isActive(grabOrderId)) {
            throw new RuntimeException("抢单活动未开始或已结束");
        }

        if (!grabOrderService.decreaseStock(grabOrderId)) {
            throw new RuntimeException("库存不足");
        }

        Order order = new Order();
        order.setMemberId(memberId);
        order.setGrabOrderId(grabOrderId);
        order.setAmount(amount);
        order.setStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    public Order payOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在: " + id));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("订单状态不允许支付");
        }
        order.setStatus(OrderStatus.PAID);
        return orderRepository.save(order);
    }

    public Order cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在: " + id));
        if (order.getStatus() == OrderStatus.PAID) {
            throw new RuntimeException("已支付订单不能取消");
        }
        order.setStatus(OrderStatus.CANCELLED);
        
        GrabOrder grabOrder = grabOrderService.getGrabOrderById(order.getGrabOrderId())
                .orElseThrow(() -> new RuntimeException("抢单活动不存在"));
        grabOrder.setStock(grabOrder.getStock() + 1);
        grabOrderService.updateGrabOrder(grabOrder.getId(), grabOrder);
        
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
