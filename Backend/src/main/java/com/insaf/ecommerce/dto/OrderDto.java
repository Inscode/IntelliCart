package com.insaf.ecommerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDto {
    private Long orderId;
    private Double totalAmount;
    private String status;
    private List<OrderItemDto> items;
}
