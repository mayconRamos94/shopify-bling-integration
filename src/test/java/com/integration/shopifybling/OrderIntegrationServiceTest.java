package com.integration.shopifybling;

import com.integration.shopifybling.domain.Order;
import com.integration.shopifybling.domain.OrderStatus;
import com.integration.shopifybling.dto.BlingOrderRequest;
import com.integration.shopifybling.dto.BlingOrderResponse;
import com.integration.shopifybling.dto.ShopifyOrderPayload;
import com.integration.shopifybling.exception.DuplicateOrderException;
import com.integration.shopifybling.integration.BlingApiClient;
import com.integration.shopifybling.mapper.ShopifyOrderMapper;
import com.integration.shopifybling.repository.OrderRepository;
import com.integration.shopifybling.service.IntegrationLogService;
import com.integration.shopifybling.service.OrderIntegrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderIntegrationServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ShopifyOrderMapper mapper;
    @Mock private BlingApiClient blingApiClient;
    @Mock private IntegrationLogService logService;

    @InjectMocks
    private OrderIntegrationService service;

    private ShopifyOrderPayload payload;
    private Order order;

    @BeforeEach
    void setUp() {
        payload = buildPayload("shopify-order-001", "#1001");

        order = Order.builder()
                .shopifyOrderId("shopify-order-001")
                .orderNumber("#1001")
                .status(OrderStatus.PROCESSING)
                .build();
    }

    @Test
    void shouldProcessOrderSuccessfully() {
        when(orderRepository.existsByShopifyOrderId(anyString())).thenReturn(false);
        when(mapper.toOrder(any())).thenReturn(order);
        when(orderRepository.save(any())).thenReturn(order);
        when(mapper.toBlingRequest(any())).thenReturn(BlingOrderRequest.builder()
                .orderNumber("#1001").build());

        BlingOrderResponse blingResponse = new BlingOrderResponse();
        blingResponse.setId("bling-999");
        when(blingApiClient.sendOrder(any())).thenReturn(blingResponse);

        service.process(payload);

        verify(orderRepository, atLeastOnce()).save(any());
        verify(blingApiClient).sendOrder(any());
        verify(logService).recordSuccess(anyString(), anyString(), eq("bling-999"), anyInt(), anyLong());
    }

    @Test
    void shouldRejectDuplicateOrder() {
        when(orderRepository.existsByShopifyOrderId("shopify-order-001")).thenReturn(true);

        assertThatThrownBy(() -> service.process(payload))
                .isInstanceOf(DuplicateOrderException.class)
                .hasMessageContaining("shopify-order-001");

        verify(blingApiClient, never()).sendOrder(any());
    }

    private ShopifyOrderPayload buildPayload(String id, String orderNumber) {
        ShopifyOrderPayload p = new ShopifyOrderPayload();
        p.setId(id);
        p.setOrderNumber(orderNumber);
        p.setEmail("customer@example.com");
        p.setTotalPrice(new BigDecimal("199.90"));
        p.setCurrency("BRL");

        ShopifyOrderPayload.ShippingAddress address = new ShopifyOrderPayload.ShippingAddress();
        address.setAddress1("Rua das Flores, 100");
        address.setCity("São Paulo");
        address.setProvinceCode("SP");
        address.setZip("01310-100");
        address.setCountryCode("BR");
        p.setShippingAddress(address);

        ShopifyOrderPayload.LineItem item = new ShopifyOrderPayload.LineItem();
        item.setId("line-001");
        item.setTitle("Produto Exemplo");
        item.setSku("SKU-001");
        item.setQuantity(2);
        item.setPrice(new BigDecimal("99.95"));
        p.setLineItems(List.of(item));

        return p;
    }
}
