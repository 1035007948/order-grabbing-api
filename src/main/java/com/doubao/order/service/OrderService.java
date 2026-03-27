package com.doubao.order.service;

import com.doubao.order.dto.OrderRequest;
import com.doubao.order.entity.GrabOrder;
import com.doubao.order.entity.Order;
import com.doubao.order.entity.Order.OrderStatus;
import com.doubao.order.exception.BusinessException;
import com.doubao.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private GrabOrderService grabOrderService;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Order not found with id: " + id));
    }

    public List<Order> findByPhone(String phone) {
        return orderRepository.findByPhone(phone);
    }

    public List<Order> findByGrabId(Long grabId) {
        return orderRepository.findByGrabId(grabId);
    }

    @Transactional
    public Order grabOrder(OrderRequest request) {
        GrabOrder grabOrder = grabOrderService.findById(request.getGrabId());

        if (!grabOrder.isActive()) {
            throw new BusinessException("Grab order is not active or out of stock");
        }

        boolean success = grabOrderService.decreaseStock(request.getGrabId());
        if (!success) {
            throw new BusinessException("Failed to grab order, stock depleted");
        }

        Order order = new Order();
        order.setPhone(request.getPhone());
        order.setGrabId(request.getGrabId());
        order.setStatus(OrderStatus.GRABBED);

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus status) {
        Order order = findById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public void delete(Long id) {
        Order order = findById(id);
        orderRepository.delete(order);
    }
}
