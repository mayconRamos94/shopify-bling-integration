package com.integration.shopifybling.exception;

public class DuplicateOrderException extends RuntimeException {

    public DuplicateOrderException(String shopifyOrderId) {
        super("Order already processed: " + shopifyOrderId);
    }
}
