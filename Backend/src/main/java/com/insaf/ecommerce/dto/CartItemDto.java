package com.insaf.ecommerce.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private Long id;
    private Long productId;
    private Integer quantity;
    private Double priceSnapshot;
}
