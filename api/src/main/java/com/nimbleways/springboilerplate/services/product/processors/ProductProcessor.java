package com.nimbleways.springboilerplate.services.product.processors;

import com.nimbleways.springboilerplate.entities.Product;

public interface ProductProcessor {
    void process(Product product);
}