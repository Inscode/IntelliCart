package com.insaf.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CustomExceptions {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class CartItemNotFoundException extends RuntimeException {
        public CartItemNotFoundException(String message) {
            super(message);
        }
    }
}

