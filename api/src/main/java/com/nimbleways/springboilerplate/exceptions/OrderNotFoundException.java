package com.nimbleways.springboilerplate.exceptions;

public class OrderNotFoundException extends ApplicationException {
    public OrderNotFoundException(Long orderId) {
        super("Order with ID " + orderId + " not found");
    }
}