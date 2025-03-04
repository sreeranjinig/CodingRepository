package com.example.codingexercise.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ProductModel {

    private String id;

    private String productName;

    private String productDescription;

    private BigDecimal productPrice;

    private String currency;
}
