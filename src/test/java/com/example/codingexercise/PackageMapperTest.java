package com.example.codingexercise;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.codingexercise.mapper.PackageMapper;
import com.example.codingexercise.mapper.PackageMapperImpl;
import com.example.codingexercise.model.PackageModel;
import com.example.codingexercise.model.ProductModel;
import com.example.codingexercise.repository.entity.PackageEntity;
import com.example.codingexercise.repository.entity.ProductEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PackageMapperTest {

    private final PackageMapper packageMapper = new PackageMapperImpl();

    ObjectMapper mapper = new ObjectMapper();

    JsonNode node = mapper.createObjectNode();

    @Test
    void createPackageEntity() throws JsonProcessingException {
        ProductModel productModel = ProductModel.builder().productName("p1").productDescription("desc").productPrice(BigDecimal.valueOf(10)).currency("USD").build();
        PackageModel packageModel = PackageModel.builder().name("Package Name").description("Test").productRequestList(List.of(productModel)).build();
        node = mapper.readTree("{\"amount\":1.0,\"rates\":{\"EUR\" : 0.96052,\"GBP\" : 0.79347},\"base\":\"USD\",\"date\":\"2025-02-28\"}");

        PackageEntity packageEntity = packageMapper.createPackageEntity(packageModel, node);

        assertThat(packageEntity.getPackageName()).isEqualTo(packageModel.getName());
        assertThat(packageEntity.getTotalPackagePrice()).isEqualTo(BigDecimal.valueOf(10));
    }

    @Test
    void createPackageEntity_withGBPAndUSD() throws JsonProcessingException {
        ProductModel productModelWithGBP = ProductModel.builder().productName("p1").productDescription("p1-desc").productPrice(BigDecimal.valueOf(10)).currency("GBP").build();
        ProductModel productModelWIthUSD = ProductModel.builder().productName("p2").productDescription("p2-desc").productPrice(BigDecimal.valueOf(10)).currency("USD").build();
        PackageModel packageModel = PackageModel.builder().name("Package Name").description("Test").productRequestList(List.of(productModelWithGBP,productModelWIthUSD)).build();
        node = mapper.readTree("{\"EUR\" : 0.96052,\"GBP\" : 0.79347}");

        PackageEntity packageEntity = packageMapper.createPackageEntity(packageModel, node);

        assertThat(packageEntity.getPackageName()).isEqualTo(packageModel.getName());
        assertThat(packageEntity.getProducts().get(0).getProductPrice()).isEqualTo(new BigDecimal("7.93470"));
        assertThat(packageEntity.getProducts().get(1).getProductPrice()).isEqualTo(new BigDecimal("10"));
        assertThat(packageEntity.getTotalPackagePrice()).isEqualTo(new BigDecimal("17.93470"));
    }

    @Test
    void updatePackageEntity_existingProduct() throws JsonProcessingException {
        ProductEntity productEntity =ProductEntity.builder().productId(1L).productName("p1").productDescription("desc").productPrice(BigDecimal.valueOf(10)).build();
        PackageEntity packageEntity = PackageEntity.builder().id(1L).packageName("TestPackage").packageDescription("Desc").totalPackagePrice(BigDecimal.valueOf(10)).products(List.of(productEntity)).build();

        ProductModel productModelWithEUR = ProductModel.builder().id("1").productName("p1").productDescription("p1-desc").productPrice(BigDecimal.valueOf(10)).currency("EUR").build();
                PackageModel packageModel = PackageModel.builder().id("1").name("Updated Package Name").description("Updated Desc").productRequestList(List.of(productModelWithEUR)).build();
        node = mapper.readTree("{\"EUR\" : 0.96052,\"GBP\" : 0.79347}");

        PackageEntity updatePackageEntity = packageMapper.updatePackageEntity(packageEntity,packageModel, node);

        assertThat(updatePackageEntity.getPackageName()).isEqualTo(packageModel.getName());
        assertThat(updatePackageEntity.getProducts().get(0).getProductPrice()).isEqualTo(new BigDecimal("9.60520"));
        assertThat(packageEntity.getTotalPackagePrice()).isEqualTo(new BigDecimal("9.60520"));
    }

}
