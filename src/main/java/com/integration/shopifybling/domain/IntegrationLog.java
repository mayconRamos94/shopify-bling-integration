package com.integration.shopifybling.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "integration_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shopify_order_id", nullable = false)
    private String shopifyOrderId;

    @Column(name = "order_number")
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false)
    private IntegrationResult result;

    @Column(name = "bling_order_id")
    private String blingOrderId;

    @Column(name = "attempt_number")
    private int attemptNumber;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(name = "duration_ms")
    private Long durationMs;
}
