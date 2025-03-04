package com.example.codingexercise.gateway.dto;

import java.math.BigDecimal;

public record Product(Long id, String name, BigDecimal usdPrice) {
}
