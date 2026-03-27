package com.example.ordergrabbingapi.service;

import com.example.ordergrabbingapi.entity.GrabOrder;

import java.util.List;
import java.util.Optional;

public interface GrabOrderService {
    GrabOrder createGrabOrder(GrabOrder grabOrder);
    Optional<GrabOrder> getGrabOrderById(Long id);
    List<GrabOrder> getAllGrabOrders();
    GrabOrder updateGrabOrder(Long id, GrabOrder grabOrderDetails);
    void deleteGrabOrder(Long id);
    boolean grabOrder(Long grabId, String phoneNumber);
}
