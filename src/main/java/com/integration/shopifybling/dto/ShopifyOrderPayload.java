package com.integration.shopifybling.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShopifyOrderPayload {

    @NotBlank(message = "Order ID is required")
    @JsonProperty("id")
    private String id;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @NotNull(message = "Total price is required")
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("financial_status")
    private String financialStatus;

    @JsonProperty("fulfillment_status")
    private String fulfillmentStatus;

    @Valid
    @JsonProperty("customer")
    private Customer customer;

    @Valid
    @NotNull(message = "Shipping address is required")
    @JsonProperty("shipping_address")
    private ShippingAddress shippingAddress;

    @Valid
    @NotEmpty(message = "At least one line item is required")
    @JsonProperty("line_items")
    private List<LineItem> lineItems;

    @Data
    public static class Customer {

        @JsonProperty("id")
        private String id;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("last_name")
        private String lastName;

        @JsonProperty("email")
        private String email;

        @JsonProperty("phone")
        private String phone;
    }

    @Data
    public static class ShippingAddress {

        @NotBlank(message = "Shipping address line 1 is required")
        @JsonProperty("address1")
        private String address1;

        @JsonProperty("address2")
        private String address2;

        @JsonProperty("city")
        private String city;

        @JsonProperty("province_code")
        private String provinceCode;

        @JsonProperty("zip")
        private String zip;

        @JsonProperty("country_code")
        private String countryCode;

        @JsonProperty("name")
        private String name;

        @JsonProperty("phone")
        private String phone;
    }

    @Data
    public static class LineItem {

        @JsonProperty("id")
        private String id;

        @JsonProperty("product_id")
        private String productId;

        @JsonProperty("variant_id")
        private String variantId;

        @JsonProperty("sku")
        private String sku;

        @NotBlank(message = "Product title is required")
        @JsonProperty("title")
        private String title;

        @NotNull(message = "Quantity is required")
        @JsonProperty("quantity")
        private Integer quantity;

        @NotNull(message = "Unit price is required")
        @JsonProperty("price")
        private BigDecimal price;
    }
}
