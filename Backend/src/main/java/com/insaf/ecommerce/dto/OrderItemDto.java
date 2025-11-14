package com.insaf.ecommerce.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long productId;
    private String title;
    private Integer quantity;
    private Double price;
}
