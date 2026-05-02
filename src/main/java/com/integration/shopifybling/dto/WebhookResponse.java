package com.integration.shopifybling.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebhookResponse {

    private String status;
    private String message;
    private String shopifyOrderId;
    private String orderNumber;
    private LocalDateTime processedAt;
}
