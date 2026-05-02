package com.integration.shopifybling.repository;

import com.integration.shopifybling.domain.Order;
import com.integration.shopifybling.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByShopifyOrderId(String shopifyOrderId);

    boolean existsByShopifyOrderId(String shopifyOrderId);

    List<Order> findByStatusOrderByReceivedAtDesc(OrderStatus status);
}
