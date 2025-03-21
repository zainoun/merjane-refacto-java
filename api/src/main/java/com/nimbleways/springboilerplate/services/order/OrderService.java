package com.nimbleways.springboilerplate.services.order;

import com.nimbleways.springboilerplate.entities.Order;

public interface OrderService {
    void processOrder(Order order);
}