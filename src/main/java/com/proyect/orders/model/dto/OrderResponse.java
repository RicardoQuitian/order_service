package com.proyect.orders.model.dto;

import jakarta.validation.constraints.NotNull;

public class OrderResponse {

    @NotNull
    private Long id; // idOrder
    private String status;
}
