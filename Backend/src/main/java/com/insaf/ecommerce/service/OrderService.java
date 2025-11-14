package com.insaf.ecommerce.service;

import com.insaf.ecommerce.dto.CreateOrderResponse;
import com.insaf.ecommerce.dto.OrderDto;
import com.insaf.ecommerce.dto.OrderItemDto;
import com.insaf.ecommerce.model.*;
import com.insaf.ecommerce.repository.CartRepository;
import com.insaf.ecommerce.repository.OrderRepository;
import com.insaf.ecommerce.repository.ProductRepository;
import com.insaf.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public CreateOrderResponse checkout(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(()-> new RuntimeException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        for (CartItem item : cart.getItems()) {
            Product p = productRepository.findById(item.getProductId()).orElseThrow();
            if (p.getStock() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for " + p.getTitle());
            }
        }

        for (CartItem item : cart.getItems()) {
            Product p = productRepository.findById(item.getProductId()).orElseThrow();
            p.setStock(p.getStock() - item.getQuantity());
            productRepository.save(p);
        }

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(ci -> OrderItem.builder()
                        .productId(ci.getProductId())
                        .productTitle(productRepository.findById(ci.getProductId()).get().getTitle())
                        .quantity(ci.getQuantity())
                        .priceSnapshot(ci.getPriceSnapshot())
                        .build())
                .collect(Collectors.toList());

        double total = orderItems.stream()
                .mapToDouble(i -> i.getPriceSnapshot() * i.getQuantity())
                .sum();

        Order order = Order.builder()
                .user(user)
                .items(orderItems)
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .build();

        orderItems.forEach(i -> i.setOrder(order));

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        CreateOrderResponse response = new CreateOrderResponse();
        response.setOrderId(savedOrder.getId());
        response.setTotalAmount(total);
        response.setStatus(savedOrder.getStatus().name());

        return response;

    }

    public List<OrderDto> getUserOrders(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getId());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());

        dto.setItems(order.getItems().stream().map(i->{
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setProductId(i.getProductId());
            itemDto.setTitle(i.getProductTitle());
            itemDto.setQuantity(i.getQuantity());
            itemDto.setPrice(i.getPriceSnapshot());
            return itemDto;
        }).collect(Collectors.toUnmodifiableList()));

        return dto;
    }
}
