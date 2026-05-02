package com.integration.shopifybling.integration;

import com.integration.shopifybling.dto.BlingOrderRequest;
import com.integration.shopifybling.dto.BlingOrderResponse;
import com.integration.shopifybling.exception.BlingIntegrationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.UUID;

/**
 * Client for Bling ERP API v3.
 *
 * The actual HTTP call uses RestTemplate with Bling's documented endpoint.
 * For portfolio demonstration, the response is simulated to showcase retry
 * logic without requiring a live Bling account.
 *
 * In production: remove the simulation block and the real HTTP call takes over.
 */
@Component
@Slf4j
public class BlingApiClient {

    private final RestTemplate restTemplate;
    private final String blingApiUrl;
    private final String blingApiKey;
    private final double simulatedFailureRate;
    private final Random random = new Random();

    public BlingApiClient(
            RestTemplate restTemplate,
            @Value("${integration.bling.api-url}") String blingApiUrl,
            @Value("${integration.bling.api-key}") String blingApiKey,
            @Value("${integration.bling.failure-rate:0.3}") double simulatedFailureRate) {
        this.restTemplate = restTemplate;
        this.blingApiUrl = blingApiUrl;
        this.blingApiKey = blingApiKey;
        this.simulatedFailureRate = simulatedFailureRate;
    }

    /**
     * Sends an order to Bling ERP.
     * Retries up to 3 times with exponential backoff on failure.
     */
    @Retryable(
            retryFor = BlingIntegrationException.class,
            maxAttemptsExpression = "${retry.max-attempts:3}",
            backoff = @Backoff(
                    delayExpression = "${retry.initial-interval-ms:1000}",
                    multiplierExpression = "${retry.multiplier:2.0}"
            )
    )
    public BlingOrderResponse sendOrder(BlingOrderRequest request) {
        log.info("Sending order {} to Bling ERP", request.getOrderNumber());

        simulateNetworkConditions(request.getOrderNumber());

        /*
         * Real HTTP call — uncomment and configure in production:
         *
         * HttpHeaders headers = new HttpHeaders();
         * headers.set("Authorization", "Bearer " + blingApiKey);
         * headers.setContentType(MediaType.APPLICATION_JSON);
         * HttpEntity<BlingOrderRequest> entity = new HttpEntity<>(request, headers);
         *
         * ResponseEntity<BlingOrderResponse> response = restTemplate.exchange(
         *     blingApiUrl + "/pedidos/vendas",
         *     HttpMethod.POST,
         *     entity,
         *     BlingOrderResponse.class
         * );
         * return response.getBody();
         */

        BlingOrderResponse response = new BlingOrderResponse();
        response.setId(UUID.randomUUID().toString());
        response.setNumber(request.getOrderNumber());
        response.setStatus("Em aberto");

        log.info("Order {} successfully sent to Bling. Bling ID: {}", request.getOrderNumber(), response.getId());
        return response;
    }

    @Recover
    public BlingOrderResponse recoverAfterRetries(BlingIntegrationException ex, BlingOrderRequest request) {
        log.error("All retry attempts exhausted for order {}. Final error: {}",
                request.getOrderNumber(), ex.getMessage());
        throw ex;
    }

    /**
     * Simulates real-world transient failures (network timeouts, rate limits, etc.)
     * to showcase the retry mechanism. Remove in production.
     */
    private void simulateNetworkConditions(String orderNumber) {
        if (random.nextDouble() < simulatedFailureRate) {
            log.warn("Simulated transient failure for order {}", orderNumber);
            throw new BlingIntegrationException(
                    "Bling API temporarily unavailable (simulated). Order: " + orderNumber
            );
        }
    }
}
