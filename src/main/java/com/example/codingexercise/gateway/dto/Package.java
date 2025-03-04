package com.example.codingexercise.gateway.dto;

import java.math.BigDecimal;
import java.util.List;

public record Package(Long packageId, String packageName, String packageDescription, List<Product> products, BigDecimal totalPrice) {
}
