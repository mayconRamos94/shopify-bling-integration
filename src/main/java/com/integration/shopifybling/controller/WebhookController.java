package com.integration.shopifybling.controller;

import com.integration.shopifybling.domain.Order;
import com.integration.shopifybling.dto.ShopifyOrderPayload;
import com.integration.shopifybling.dto.WebhookResponse;
import com.integration.shopifybling.exception.BlingIntegrationException;
import com.integration.shopifybling.exception.DuplicateOrderException;
import com.integration.shopifybling.service.OrderIntegrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/webhook")
@Slf4j
@RequiredArgsConstructor
public class WebhookController {

    private final OrderIntegrationService integrationService;

    /**
     * Shopify webhook endpoint for new orders.
     *
     * Shopify sends this automatically when an order is created.
     * In production, validate the X-Shopify-Hmac-Sha256 header
     * to ensure the request is genuinely from Shopify.
     */
    @PostMapping("/orders")
    public ResponseEntity<WebhookResponse> receiveOrder(
            @Valid @RequestBody ShopifyOrderPayload payload,
            @RequestHeader(value = "X-Shopify-Topic", defaultValue = "orders/create") String topic,
            @RequestHeader(value = "X-Shopify-Shop-Domain", required = false) String shopDomain) {

        log.info("Webhook received | Topic: {} | Shop: {} | Order: {}", topic, shopDomain, payload.getId());

        try {
            Order order = integrationService.process(payload);

            return ResponseEntity.ok(WebhookResponse.builder()
                    .status("accepted")
                    .message("Order received and sent to Bling ERP")
                    .shopifyOrderId(order.getShopifyOrderId())
                    .orderNumber(order.getOrderNumber())
                    .processedAt(LocalDateTime.now())
                    .build());

        } catch (DuplicateOrderException ex) {
            log.warn("Duplicate order rejected: {}", payload.getId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(WebhookResponse.builder()
                    .status("ignored")
                    .message(ex.getMessage())
                    .shopifyOrderId(payload.getId())
                    .build());

        } catch (BlingIntegrationException ex) {
            log.error("Bling integration failed for order {}: {}", payload.getId(), ex.getMessage());
            // Return 200 to Shopify so it doesn't retry the webhook — we handle retries internally
            return ResponseEntity.ok(WebhookResponse.builder()
                    .status("queued")
                    .message("Order received but Bling ERP is temporarily unavailable. Will retry.")
                    .shopifyOrderId(payload.getId())
                    .build());
        }
    }
}
