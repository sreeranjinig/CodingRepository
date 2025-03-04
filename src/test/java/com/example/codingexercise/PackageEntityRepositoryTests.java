package com.example.codingexercise;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.example.codingexercise.repository.PackageEntityRepository;
import com.example.codingexercise.repository.entity.PackageEntity;
import com.example.codingexercise.repository.entity.ProductEntity;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PackageEntityRepositoryTests {

    @Autowired
    private PackageEntityRepository packageEntityRepository;

    @Test
    @DisplayName("Test 1:Save Package Test")
    @Order(1)
    @Rollback(value = false)
    void savePackageTests(){
        ProductEntity productEntity =ProductEntity.builder().productName("p1").productDescription("desc").productPrice(BigDecimal.valueOf(10)).build();
        PackageEntity packageEntity = PackageEntity.builder().packageName("TestPackage").packageDescription("Desc").totalPackagePrice(BigDecimal.valueOf(10)).products(List.of(productEntity)).build();

        packageEntityRepository.save(packageEntity);
        Assertions.assertThat(packageEntity.getId()).isNotNull();
    }

    @Test
    @Order(2)
    void getPackageTest(){
        PackageEntity packageEntity = packageEntityRepository.findById(1L).get();
        Assertions.assertThat(packageEntity).isNotNull();
    }

    @Test
    @Order(3)
    void getAllPackagesTest(){
        List<PackageEntity> packageEntities = packageEntityRepository.findAll();
        Assertions.assertThat(packageEntities.size()).isGreaterThan(0);
    }

    @Test
    @Order(4)
    void updatePackageTest(){
        PackageEntity packageEntity = packageEntityRepository.findById(1L).get();
        packageEntity.setPackageName("UpdatedPackage");
        PackageEntity updatedPackage = packageEntityRepository.save(packageEntity);
        Assertions.assertThat(updatedPackage.getPackageName()).isEqualTo("UpdatedPackage");
    }

    @Test
    @Order(5)
    void deletePackageTest(){
        packageEntityRepository.deleteById(1L);
        Assertions.assertThat(packageEntityRepository.findById(1L)).isEmpty();
    }
}
