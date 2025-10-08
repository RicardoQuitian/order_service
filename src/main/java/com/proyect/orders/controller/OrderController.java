package com.proyect.orders.controller;


import com.proyect.orders.model.dto.OrderRequest;
import com.proyect.orders.model.entity.Order;
import com.proyect.orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Gestión de órdenes")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService service) { this.orderService = service; }


    @PostMapping
    @Operation(summary = "Crear una nueva orden", description = "Crea una orden con sus items asociados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request) {
        Order saved = orderService.createOrder(request);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una orden", description = "Busca una orden por su ID")
    @ApiResponse(responseCode = "200", description = "Orden encontrada")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return orderService.getOrder(id)
                != null ? ResponseEntity.ok(orderService.getOrder(id))
                : ResponseEntity.notFound().build();
    }


    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar el estado de una orden", description = "Actualiza el estado de la orden por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
        })
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Order updated = orderService.updateOrderStatus(id, status);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }


    @GetMapping
    @Operation(summary = "Listar órdenes por cliente", description = "Obtiene las órdenes de un cliente según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de órdenes obtenida exitosamente")
    })
    public ResponseEntity<List<Order>> listByCustomer(@RequestParam Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }
}
