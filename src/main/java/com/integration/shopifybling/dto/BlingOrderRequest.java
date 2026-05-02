package com.integration.shopifybling.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents the payload sent to the Bling ERP API v3.
 * Field names follow Bling's documented API contract.
 */
@Data
@Builder
public class BlingOrderRequest {

    @JsonProperty("numeroPedido")
    private String orderNumber;

    @JsonProperty("dataPedido")
    private String orderDate;

    @JsonProperty("contato")
    private Contact contact;

    @JsonProperty("enderecoEntrega")
    private DeliveryAddress deliveryAddress;

    @JsonProperty("itens")
    private List<Item> items;

    @JsonProperty("totalProdutos")
    private BigDecimal totalProducts;

    @JsonProperty("totalPedido")
    private BigDecimal totalOrder;

    @JsonProperty("observacoes")
    private String observations;

    @Data
    @Builder
    public static class Contact {

        @JsonProperty("nome")
        private String name;

        @JsonProperty("email")
        private String email;

        @JsonProperty("telefone")
        private String phone;
    }

    @Data
    @Builder
    public static class DeliveryAddress {

        @JsonProperty("endereco")
        private String address;

        @JsonProperty("cidade")
        private String city;

        @JsonProperty("uf")
        private String state;

        @JsonProperty("cep")
        private String zipCode;

        @JsonProperty("pais")
        private String country;
    }

    @Data
    @Builder
    public static class Item {

        @JsonProperty("codigo")
        private String sku;

        @JsonProperty("descricao")
        private String description;

        @JsonProperty("quantidade")
        private Integer quantity;

        @JsonProperty("valor")
        private BigDecimal unitPrice;
    }
}
