package com.proyect.orders.model.dto;

import java.util.List;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {

    @NotNull
    private Long customerId;
    @NotNull
    private List<OrderItemDto> items;

    @Getter
    @Setter
    public static class OrderItemDto {
        @NotNull
        private Long productId;
        @NotNull
        private Integer quantity;
        @NotNull
        private Double price;
    }


}
