package com.integration.shopifybling.service;

import com.integration.shopifybling.domain.Order;
import com.integration.shopifybling.domain.OrderStatus;
import com.integration.shopifybling.dto.BlingOrderRequest;
import com.integration.shopifybling.dto.BlingOrderResponse;
import com.integration.shopifybling.dto.ShopifyOrderPayload;
import com.integration.shopifybling.exception.BlingIntegrationException;
import com.integration.shopifybling.exception.DuplicateOrderException;
import com.integration.shopifybling.integration.BlingApiClient;
import com.integration.shopifybling.mapper.ShopifyOrderMapper;
import com.integration.shopifybling.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderIntegrationService {

    private final OrderRepository orderRepository;
    private final ShopifyOrderMapper mapper;
    private final BlingApiClient blingApiClient;
    private final IntegrationLogService logService;

    /**
     * Full integration flow:
     * 1. Validate idempotency (reject duplicates)
     * 2. Persist the incoming order
     * 3. Map to Bling format
     * 4. Send to Bling (with retry via @Retryable in BlingApiClient)
     * 5. Update order status and log result
     */
    @Transactional
    public Order process(ShopifyOrderPayload payload) {
        log.info("Processing Shopify order: {} (ID: {})", payload.getOrderNumber(), payload.getId());

        if (orderRepository.existsByShopifyOrderId(payload.getId())) {
            log.warn("Duplicate order received: {}", payload.getId());
            throw new DuplicateOrderException(payload.getId());
        }

        Order order = mapper.toOrder(payload);
        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);

        sendToBling(order);

        return order;
    }

    private void sendToBling(Order order) {
        long startTime = System.currentTimeMillis();

        try {
            BlingOrderRequest request = mapper.toBlingRequest(order);
            BlingOrderResponse response = blingApiClient.sendOrder(request);

            long duration = System.currentTimeMillis() - startTime;

            int attempt = currentRetryAttempt();
            order.setStatus(OrderStatus.SENT_TO_BLING);
            orderRepository.save(order);

            logService.recordSuccess(
                    order.getShopifyOrderId(),
                    order.getOrderNumber(),
                    response.getId(),
                    attempt,
                    duration
            );

        } catch (BlingIntegrationException ex) {
            long duration = System.currentTimeMillis() - startTime;

            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);

            logService.recordFailure(
                    order.getShopifyOrderId(),
                    order.getOrderNumber(),
                    ex.getMessage(),
                    currentRetryAttempt(),
                    duration
            );

            throw ex;
        }
    }

    private int currentRetryAttempt() {
        var context = RetrySynchronizationManager.getContext();
        return context != null ? context.getRetryCount() + 1 : 1;
    }
}
