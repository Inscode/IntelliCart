package com.insaf.ecommerce.repository;

import com.insaf.ecommerce.dto.AddToCartRequest;
import com.insaf.ecommerce.dto.AuthRequest;
import com.insaf.ecommerce.dto.CartDto;
import com.insaf.ecommerce.dto.UpdateCartItemRequest;
import com.insaf.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartDto> addToCart(Authentication authentication, @RequestBody AddToCartRequest req) {
        String email = authentication.getName();
        CartDto dto = cartService.addToCart(email, req);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<CartDto> getCart(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.getCart(email));
    }

    @PutMapping("/item/{id}")
    public ResponseEntity<CartDto> updateItem(Authentication authentication, @PathVariable("id") Long cartItemId, @RequestBody UpdateCartItemRequest req)
    {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.updateItem(email, cartItemId, req));
    }

    @DeleteMapping("/item/{id}")
    public ResponseEntity<Void> removeItem(Authentication authentication, @PathVariable("id") Long cartItemId) {
        String email = authentication.getName();
        cartService.removeItem(email, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        String email = authentication.getName();
        cartService.clearCart(email);
        return ResponseEntity.noContent().build();
    }
}
