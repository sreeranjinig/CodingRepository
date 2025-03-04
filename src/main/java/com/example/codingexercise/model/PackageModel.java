package com.example.codingexercise.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PackageModel {

    private String id;

    private String name;

    private String description;

    private List<ProductModel> productRequestList;
}
