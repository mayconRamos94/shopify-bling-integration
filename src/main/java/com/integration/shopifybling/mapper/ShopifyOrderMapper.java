package com.integration.shopifybling.mapper;

import com.integration.shopifybling.domain.Order;
import com.integration.shopifybling.domain.OrderItem;
import com.integration.shopifybling.domain.OrderStatus;
import com.integration.shopifybling.dto.BlingOrderRequest;
import com.integration.shopifybling.dto.ShopifyOrderPayload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Component
public class ShopifyOrderMapper {

    private static final DateTimeFormatter BLING_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Order toOrder(ShopifyOrderPayload payload) {
        String customerName = resolveCustomerName(payload);

        Order order = Order.builder()
                .shopifyOrderId(payload.getId())
                .orderNumber(payload.getOrderNumber())
                .customerName(customerName)
                .customerEmail(resolveEmail(payload))
                .customerPhone(resolvePhone(payload))
                .totalPrice(payload.getTotalPrice())
                .currency(payload.getCurrency())
                .status(OrderStatus.RECEIVED)
                .receivedAt(LocalDateTime.now())
                .shippingAddress(payload.getShippingAddress().getAddress1())
                .shippingCity(payload.getShippingAddress().getCity())
                .shippingState(payload.getShippingAddress().getProvinceCode())
                .shippingZip(payload.getShippingAddress().getZip())
                .shippingCountry(payload.getShippingAddress().getCountryCode())
                .build();

        List<OrderItem> items = payload.getLineItems().stream()
                .map(lineItem -> toOrderItem(lineItem, order))
                .toList();

        order.setItems(items);
        return order;
    }

    public BlingOrderRequest toBlingRequest(Order order) {
        List<BlingOrderRequest.Item> items = order.getItems().stream()
                .map(this::toBlingItem)
                .toList();

        return BlingOrderRequest.builder()
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getReceivedAt().format(BLING_DATE_FORMAT))
                .contact(BlingOrderRequest.Contact.builder()
                        .name(order.getCustomerName())
                        .email(order.getCustomerEmail())
                        .phone(order.getCustomerPhone())
                        .build())
                .deliveryAddress(BlingOrderRequest.DeliveryAddress.builder()
                        .address(order.getShippingAddress())
                        .city(order.getShippingCity())
                        .state(order.getShippingState())
                        .zipCode(order.getShippingZip())
                        .country(order.getShippingCountry())
                        .build())
                .items(items)
                .totalProducts(calculateItemsTotal(order.getItems()))
                .totalOrder(order.getTotalPrice())
                .observations("Pedido importado do Shopify #" + order.getShopifyOrderId())
                .build();
    }

    private OrderItem toOrderItem(ShopifyOrderPayload.LineItem lineItem, Order order) {
        BigDecimal totalPrice = lineItem.getPrice()
                .multiply(BigDecimal.valueOf(lineItem.getQuantity()));

        return OrderItem.builder()
                .order(order)
                .shopifyLineItemId(lineItem.getId())
                .productId(lineItem.getProductId())
                .sku(lineItem.getSku())
                .title(lineItem.getTitle())
                .quantity(lineItem.getQuantity())
                .unitPrice(lineItem.getPrice())
                .totalPrice(totalPrice)
                .build();
    }

    private BlingOrderRequest.Item toBlingItem(OrderItem item) {
        return BlingOrderRequest.Item.builder()
                .sku(Objects.requireNonNullElse(item.getSku(), item.getProductId()))
                .description(item.getTitle())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }

    private String resolveCustomerName(ShopifyOrderPayload payload) {
        if (payload.getCustomer() != null) {
            return payload.getCustomer().getFirstName() + " " + payload.getCustomer().getLastName();
        }
        if (payload.getShippingAddress().getName() != null) {
            return payload.getShippingAddress().getName();
        }
        return "Cliente Shopify";
    }

    private String resolveEmail(ShopifyOrderPayload payload) {
        if (payload.getEmail() != null) return payload.getEmail();
        if (payload.getCustomer() != null) return payload.getCustomer().getEmail();
        return null;
    }

    private String resolvePhone(ShopifyOrderPayload payload) {
        if (payload.getPhone() != null) return payload.getPhone();
        if (payload.getCustomer() != null && payload.getCustomer().getPhone() != null) {
            return payload.getCustomer().getPhone();
        }
        if (payload.getShippingAddress().getPhone() != null) {
            return payload.getShippingAddress().getPhone();
        }
        return null;
    }

    private BigDecimal calculateItemsTotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
