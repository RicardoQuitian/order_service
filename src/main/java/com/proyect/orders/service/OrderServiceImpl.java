package com.proyect.orders.service;


import com.proyect.orders.model.dto.OrderRequest;
import com.proyect.orders.model.entity.Order;
import com.proyect.orders.model.entity.OrderItem;
import com.proyect.orders.event.OrderEventPublisher;
import com.proyect.orders.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    // cola interna
    private final BlockingQueue<Long> queue = new LinkedBlockingQueue<>(1000);
    private final ExecutorService workers;

    public OrderServiceImpl(OrderRepository repo, OrderEventPublisher publisher) {
        this.orderRepository = repo;
        this.eventPublisher = publisher;
        int workerCount = Runtime.getRuntime().availableProcessors(); // o fijo, ej 4
        this.workers = Executors.newFixedThreadPool(workerCount);
    }

    @PostConstruct
    public void startWorkers() {
        for (int i = 0; i < ((ThreadPoolExecutor) workers).getCorePoolSize(); i++) {
            workers.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Long orderId = queue.take(); // bloqueante
                        // recuperar pedido y publicar
                        Order order = orderRepository.findById(orderId).orElse(null);
                        if (order != null) {
                            eventPublisher.publishOrderCreated(order);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setStatus("CREATED");
        order.setCreatedAt(new Date()); // TODO: poner la lista de items guardados-
        List<OrderItem> items = request.getItems().stream()
                .map(i -> {
                    OrderItem item = new OrderItem();
                    item.setProductId(i.getProductId());
                    item.setQuantity(i.getQuantity());
                    item.setPrice(i.getPrice());
                    return item;
                })
                .toList();

        order.setItems(items);
        order = orderRepository.save(order);
        // encolar para publicar evento (Producer)
        boolean offered = queue.offer(order.getId());
        if (!offered) {
            // si la cola está llena, puedes manejarlo (ej. intentar reintentar o log)
            // aquí lo dejamos simple:
            System.err.println("cola llena - publicar síncronamente");
            eventPublisher.publishOrderCreated(order);
        }
        return order;
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public Order updateOrderStatus(Long id, String status) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(status);
            return orderRepository.save(order);
        }).orElse(null);
    }


}
