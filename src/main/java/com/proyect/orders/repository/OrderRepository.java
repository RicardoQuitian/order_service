package com.proyect.orders.repository;

import java.util.List;
import com.proyect.orders.model.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "items")
    List<Order> findByCustomerId(Long customerId);
}
