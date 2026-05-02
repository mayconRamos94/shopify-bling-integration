package com.integration.shopifybling.service;

import com.integration.shopifybling.domain.IntegrationLog;
import com.integration.shopifybling.domain.IntegrationResult;
import com.integration.shopifybling.dto.IntegrationLogResponse;
import com.integration.shopifybling.repository.IntegrationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class IntegrationLogService {

    private final IntegrationLogRepository logRepository;

    @Transactional
    public IntegrationLog recordSuccess(String shopifyOrderId, String orderNumber,
                                        String blingOrderId, int attempt, long durationMs) {
        IntegrationLog entry = IntegrationLog.builder()
                .shopifyOrderId(shopifyOrderId)
                .orderNumber(orderNumber)
                .result(IntegrationResult.SUCCESS)
                .blingOrderId(blingOrderId)
                .attemptNumber(attempt)
                .executedAt(LocalDateTime.now())
                .durationMs(durationMs)
                .build();

        log.info("[LOG] SUCCESS | Order: {} | Bling ID: {} | Attempt: {} | {}ms",
                orderNumber, blingOrderId, attempt, durationMs);

        return logRepository.save(entry);
    }

    @Transactional
    public IntegrationLog recordFailure(String shopifyOrderId, String orderNumber,
                                         String errorMessage, int attempt, long durationMs) {
        IntegrationLog entry = IntegrationLog.builder()
                .shopifyOrderId(shopifyOrderId)
                .orderNumber(orderNumber)
                .result(IntegrationResult.FAILURE)
                .attemptNumber(attempt)
                .errorMessage(truncate(errorMessage, 1000))
                .executedAt(LocalDateTime.now())
                .durationMs(durationMs)
                .build();

        log.error("[LOG] FAILURE | Order: {} | Attempt: {} | Error: {}", orderNumber, attempt, errorMessage);

        return logRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public List<IntegrationLogResponse> findAll() {
        return logRepository.findAllByOrderByExecutedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IntegrationLogResponse> findByOrderId(String shopifyOrderId) {
        return logRepository.findByShopifyOrderIdOrderByExecutedAtDesc(shopifyOrderId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getSummary() {
        return Map.of(
                "total", logRepository.count(),
                "success", logRepository.countByResult(IntegrationResult.SUCCESS),
                "failure", logRepository.countByResult(IntegrationResult.FAILURE)
        );
    }

    private IntegrationLogResponse toResponse(IntegrationLog log) {
        return IntegrationLogResponse.builder()
                .id(log.getId())
                .shopifyOrderId(log.getShopifyOrderId())
                .orderNumber(log.getOrderNumber())
                .result(log.getResult().name())
                .blingOrderId(log.getBlingOrderId())
                .attemptNumber(log.getAttemptNumber())
                .errorMessage(log.getErrorMessage())
                .executedAt(log.getExecutedAt())
                .durationMs(log.getDurationMs())
                .build();
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) return value;
        return value.substring(0, maxLength);
    }
}
