package com.integration.shopifybling.exception;

public class BlingIntegrationException extends RuntimeException {

    public BlingIntegrationException(String message) {
        super(message);
    }

    public BlingIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
