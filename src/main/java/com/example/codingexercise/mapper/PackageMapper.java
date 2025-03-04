package com.example.codingexercise.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.mapstruct.Builder;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import com.example.codingexercise.gateway.dto.Package;
import com.example.codingexercise.gateway.dto.Product;
import com.example.codingexercise.model.PackageModel;
import com.example.codingexercise.model.ProductModel;
import com.example.codingexercise.repository.entity.PackageEntity;
import com.example.codingexercise.repository.entity.ProductEntity;
import com.fasterxml.jackson.databind.JsonNode;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        builder = @Builder(disableBuilder = true))
public interface PackageMapper {

    String CURRENCY_USD = "USD";

    List<Package> mapToPackageList(List<PackageEntity> packageEntities);

    default Package mapToPackage(PackageEntity packageEntity) {
        return new Package(packageEntity.getId(), packageEntity.getPackageName(), packageEntity.getPackageDescription(), mapToProduct(packageEntity.getProducts()),
                           packageEntity.getTotalPackagePrice());
    }

    default List<Product> mapToProduct(List<ProductEntity> productRequestList) {
        List<Product> productList = new ArrayList<>();
        productRequestList.forEach(productRequest ->
                                           productList.add(new Product(productRequest.getProductId(), productRequest.getProductName(), productRequest.getProductPrice())));
        return productList;
    }

    /**
     * Maps the request object to entity
     * Scenarios:
     * 1. Add new product for package
     *
     * @param newProducts
     * @param jsonNode
     * @return PackageEntity
     */

    default PackageEntity createPackageEntity(PackageModel newProducts, JsonNode jsonNode) {
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName(newProducts.getName());
        packageEntity.setPackageDescription(newProducts.getDescription());
        packageEntity.setTotalPackagePrice(BigDecimal.ZERO);
        List<ProductEntity> productList = new ArrayList<>();
        newProducts.getProductRequestList().forEach(productRequest -> {
            productList.add(createProductEntity(packageEntity, productRequest, jsonNode));
        });
        packageEntity.setProducts(productList);
        return packageEntity;
    }

    /**
     * Maps the update request object to entity
     * Scenarios:
     * 1. Update existing product -> Subtracts the product price from total price
     *
     * @param packageEntity
     * @param packageModel
     * @param jsonNode
     * @return PackageEntity
     */
    default PackageEntity updatePackageEntity(PackageEntity packageEntity, PackageModel packageModel, JsonNode jsonNode) {
        packageEntity.setPackageName(nonNull(packageModel.getName()) ? packageModel.getName() : packageEntity.getPackageName());
        packageEntity.setPackageDescription(nonNull(packageModel.getDescription()) ? packageModel.getDescription() : packageEntity.getPackageDescription());
        if (nonNull(packageModel.getProductRequestList())) {
            packageEntity.getProducts().forEach(productEntity ->
                                                        packageModel.getProductRequestList().forEach(productRequest -> {
                                                            if (Objects.equals(Long.parseLong(productRequest.getId()), productEntity.getProductId())) {
                                                                packageEntity.setTotalPackagePrice(packageEntity.getTotalPackagePrice().subtract(productEntity.getProductPrice()));
                                                                ProductEntity product =packageEntity.getProducts().stream()
                                                                                                    .filter(p -> Objects.equals(p.getProductId(), productEntity.getProductId()))
                                                                                                    .findFirst().get();
                                                                product.setProductName(productRequest.getProductName());
                                                                product.setProductDescription(productRequest.getProductDescription());
                                                                product.setProductPrice(CURRENCY_USD.equals(productRequest.getCurrency()) || Objects.isNull(productRequest.getCurrency())?
                                                                                        productRequest.getProductPrice() :
                                                                                        productRequest.getProductPrice().multiply(
                                                                                                new BigDecimal(jsonNode.get(productRequest.getCurrency()).asText())));

                                                                packageEntity.setTotalPackagePrice(packageEntity.getTotalPackagePrice().add(product.getProductPrice()));
                                                            }}));
        }

        return packageEntity;
    }


    default ProductEntity createProductEntity(PackageEntity packageEntity, ProductModel productModel, JsonNode jsonNode) {
        ProductEntity product = ProductEntity.builder()
                                             .productDescription(productModel.getProductDescription())
                                             .productName(productModel.getProductName())
                                             .productPrice(CURRENCY_USD.equals(productModel.getCurrency()) || isNull(productModel.getCurrency()) ?
                                                           productModel.getProductPrice() :
                                                           productModel.getProductPrice().multiply(
                                                                   new BigDecimal(jsonNode.get(productModel.getCurrency()).asText())))
                                             .build();
        packageEntity.setTotalPackagePrice(packageEntity.getTotalPackagePrice().add(product.getProductPrice()));
        return product;
    }




}