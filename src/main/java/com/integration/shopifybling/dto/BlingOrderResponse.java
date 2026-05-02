package com.integration.shopifybling.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BlingOrderResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("numero")
    private String number;

    @JsonProperty("situacao")
    private String status;
}
