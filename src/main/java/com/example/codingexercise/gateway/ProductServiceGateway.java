package com.example.codingexercise.gateway;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductServiceGateway {

    private final RestTemplate restTemplate;

    public ProductServiceGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Package getProduct(String id) {
        return restTemplate.getForObject("https://product-service.herokuapp.com/api/v1/packages/{id}", Package.class, id);
    }
}
