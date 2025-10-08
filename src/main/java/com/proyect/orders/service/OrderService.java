package com.proyect.orders.service;
import com.proyect.orders.model.dto.OrderRequest;
import com.proyect.orders.model.entity.Order;
import java.util.List;

public interface OrderService {

    Order createOrder(OrderRequest request);
    Order getOrder(Long id);
    List<Order> getOrdersByCustomer(Long customerId);
    Order updateOrderStatus(Long id, String status);
}