package com.integration.shopifybling.controller;

import com.integration.shopifybling.dto.IntegrationLogResponse;
import com.integration.shopifybling.service.IntegrationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class IntegrationLogController {

    private final IntegrationLogService logService;

    @GetMapping
    public ResponseEntity<List<IntegrationLogResponse>> getAllLogs() {
        return ResponseEntity.ok(logService.findAll());
    }

    @GetMapping("/order/{shopifyOrderId}")
    public ResponseEntity<List<IntegrationLogResponse>> getLogsByOrder(@PathVariable String shopifyOrderId) {
        return ResponseEntity.ok(logService.findByOrderId(shopifyOrderId));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getSummary() {
        return ResponseEntity.ok(logService.getSummary());
    }
}
