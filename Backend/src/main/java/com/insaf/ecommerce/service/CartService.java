package com.insaf.ecommerce.service;

import com.insaf.ecommerce.dto.AddToCartRequest;
import com.insaf.ecommerce.dto.CartDto;
import com.insaf.ecommerce.dto.CartItemDto;
import com.insaf.ecommerce.dto.UpdateCartItemRequest;
import com.insaf.ecommerce.exception.CustomExceptions;
import com.insaf.ecommerce.model.Cart;
import com.insaf.ecommerce.model.CartItem;
import com.insaf.ecommerce.model.Product;
import com.insaf.ecommerce.model.User;
import com.insaf.ecommerce.repository.CartRepository;
import com.insaf.ecommerce.repository.ProductRepository;
import com.insaf.ecommerce.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    @Transactional
    public CartDto addToCart(String userEmail, AddToCartRequest req) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("product not found"));

        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater that 0");
        }

        Cart cart = getOrCreateCart(user);

        CartItem existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(req.getProductId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + req.getQuantity());
        } else {
            CartItem item = CartItem.builder()
                    .productId(product.getId())
                    .quantity(req.getQuantity())
                    .priceSnapshot(product.getPrice())
                    .cart(cart)
                    .build();
            cart.getItems().add(item);
        }
            Cart saved = cartRepository.save(cart);
            return mapToDto(saved);
    }

    @Transactional
    public CartDto updateItem(String userEmail, Long cartItemId, UpdateCartItemRequest req) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Cart cart = getOrCreateCart(user);

        CartItem item = cart.getItems().stream()
                .filter(i-> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CustomExceptions.CartItemNotFoundException("Cart item not found"));

        if (req.getQuantity() == 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(req.getQuantity());
        }

        Cart saved = cartRepository.save(cart);
        return mapToDto(saved);
    }

    @Transactional
    public void removeItem(String userEmail, Long cartItemId) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Cart cart = getOrCreateCart(user);

        cart.getItems().removeIf(i-> i.getId().equals(cartItemId));
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public CartDto getCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Cart cart = cartRepository.findByUser(user).orElseGet(()-> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return newCart;
        });
        return mapToDto(cart);
    }


    private CartDto mapToDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setItems(cart.getItems().stream().map(i-> {
            CartItemDto it = new CartItemDto();
            it.setId(i.getId());
            it.setProductId(i.getProductId());
            it.setQuantity(i.getQuantity());
            it.setPriceSnapshot(i.getPriceSnapshot());
            return it;
        }).collect(Collectors.toUnmodifiableList()));

        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPriceSnapshot() * i.getQuantity())
                .sum();
        dto.setTotal(total);
        return dto;

    }

}
