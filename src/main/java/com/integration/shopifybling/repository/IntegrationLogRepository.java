package com.integration.shopifybling.repository;

import com.integration.shopifybling.domain.IntegrationLog;
import com.integration.shopifybling.domain.IntegrationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegrationLogRepository extends JpaRepository<IntegrationLog, Long> {

    List<IntegrationLog> findByShopifyOrderIdOrderByExecutedAtDesc(String shopifyOrderId);

    List<IntegrationLog> findAllByOrderByExecutedAtDesc();

    List<IntegrationLog> findByResultOrderByExecutedAtDesc(IntegrationResult result);

    long countByResult(IntegrationResult result);
}
