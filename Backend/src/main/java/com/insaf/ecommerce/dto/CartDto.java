package com.insaf.ecommerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDto {
    private Long id;
    private List<CartItemDto> items;
    private Double total;
}
