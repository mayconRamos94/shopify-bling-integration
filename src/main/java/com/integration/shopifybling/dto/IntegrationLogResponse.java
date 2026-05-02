package com.integration.shopifybling.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntegrationLogResponse {

    private Long id;
    private String shopifyOrderId;
    private String orderNumber;
    private String result;
    private String blingOrderId;
    private int attemptNumber;
    private String errorMessage;
    private LocalDateTime executedAt;
    private Long durationMs;
}
