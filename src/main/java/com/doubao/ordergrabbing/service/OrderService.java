package com.doubao.ordergrabbing.service;

import com.doubao.ordergrabbing.dto.OrderRequest;
import com.doubao.ordergrabbing.dto.OrderResponse;
import com.doubao.ordergrabbing.entity.GrabOrder;
import com.doubao.ordergrabbing.entity.Order;
import com.doubao.ordergrabbing.entity.OrderStatus;
import com.doubao.ordergrabbing.exception.BusinessException;
import com.doubao.ordergrabbing.repository.GrabOrderRepository;
import com.doubao.ordergrabbing.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final GrabOrderRepository grabOrderRepository;

    public OrderService(OrderRepository orderRepository, GrabOrderRepository grabOrderRepository) {
        this.orderRepository = orderRepository;
        this.grabOrderRepository = grabOrderRepository;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        GrabOrder grabOrder = grabOrderRepository.findById(request.getGrabOrderId())
                .orElseThrow(() -> new BusinessException("抢单活动不存在"));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(grabOrder.getStartTime())) {
            throw new BusinessException("抢单活动尚未开始");
        }
        if (now.isAfter(grabOrder.getEndTime())) {
            throw new BusinessException("抢单活动已结束");
        }
        if (grabOrder.getStock() <= 0) {
            throw new BusinessException("商品库存不足");
        }

        grabOrder.setStock(grabOrder.getStock() - 1);
        grabOrderRepository.save(grabOrder);

        Order order = new Order();
        order.setPhone(request.getPhone());
        order.setGrabOrderId(request.getGrabOrderId());
        order.setStatus(OrderStatus.SUCCESS);
        order.setCreateTime(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        return toOrderResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        return toOrderResponse(order);
    }

    public List<OrderResponse> getOrdersByPhone(String phone) {
        return orderRepository.findByPhone(phone).stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByGrabOrderId(Long grabOrderId) {
        return orderRepository.findByGrabOrderId(grabOrderId).stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("订单已取消");
        }

        GrabOrder grabOrder = grabOrderRepository.findById(order.getGrabOrderId())
                .orElseThrow(() -> new BusinessException("抢单活动不存在"));

        grabOrder.setStock(grabOrder.getStock() + 1);
        grabOrderRepository.save(grabOrder);

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        return toOrderResponse(savedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (order.getStatus() == OrderStatus.SUCCESS) {
            GrabOrder grabOrder = grabOrderRepository.findById(order.getGrabOrderId())
                    .orElse(null);
            if (grabOrder != null) {
                grabOrder.setStock(grabOrder.getStock() + 1);
                grabOrderRepository.save(grabOrder);
            }
        }

        orderRepository.deleteById(id);
    }

    private OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getPhone(),
                order.getGrabOrderId(),
                order.getStatus(),
                order.getCreateTime()
        );
    }
}
