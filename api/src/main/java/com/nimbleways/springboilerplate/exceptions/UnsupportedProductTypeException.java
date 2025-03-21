package com.nimbleways.springboilerplate.exceptions;

public class UnsupportedProductTypeException extends ApplicationException {
    public UnsupportedProductTypeException(String type) {
        super("Unsupported product type: " + type);
    }
}