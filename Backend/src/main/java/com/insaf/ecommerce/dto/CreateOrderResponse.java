package com.insaf.ecommerce.dto;


import lombok.Data;

@Data
public class CreateOrderResponse {
    private Long orderId;
    private Double totalAmount;
    private String status;
}
