package com.example.codingexercise;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.codingexercise.gateway.dto.Package;
import com.example.codingexercise.gateway.dto.Product;
import com.example.codingexercise.mapper.PackageMapper;
import com.example.codingexercise.model.PackageModel;
import com.example.codingexercise.model.ProductModel;
import com.example.codingexercise.repository.PackageEntityRepository;
import com.example.codingexercise.repository.entity.PackageEntity;
import com.example.codingexercise.repository.entity.ProductEntity;
import com.example.codingexercise.service.PackageService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PackageServiceTests {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PackageEntityRepository packageEntityRepository;

    @Mock
    private PackageMapper packageMapper ;

    @InjectMocks
    private PackageService packageService;

    @Test
    void savePackageTest(){
        ProductModel productModel = ProductModel.builder().productName("p1").productDescription("desc").productPrice(BigDecimal.valueOf(10)).currency("USD").build();
        PackageModel packageModel = PackageModel.builder().name("Package Name").description("Test").productRequestList(List.of(productModel)).build();
        ProductEntity productEntity =ProductEntity.builder().productId(1L).productName("p1").productDescription("desc").productPrice(BigDecimal.valueOf(10)).build();
        PackageEntity packageEntity = PackageEntity.builder().id(1L).packageName("TestPackage").packageDescription("Desc").totalPackagePrice(BigDecimal.valueOf(10)).products(List.of(productEntity)).build();

        String content = "{\"base\":\"USD\",\"date\":\"2025-02-28\",\"currency\":\"USD\"}";
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.frankfurter.dev/v1/latest?base=USD").build().toUri();
        when(restTemplate.getForEntity(uri, String.class)).thenReturn(ResponseEntity.status(200).body(content));
        when(packageEntityRepository.save(packageEntity)).thenReturn(packageEntity);
        when(packageMapper.createPackageEntity(any(),any())).thenReturn(packageEntity);
        Package pkgEntity = packageService.create(packageModel);
        assertNotNull(pkgEntity, "Package created");
    }

    @Test
    void updatePackageTest(){
        ProductModel productModel = ProductModel.builder().productName("p1").productDescription("desc").productPrice(BigDecimal.valueOf(10)).currency("USD").build();
        PackageModel packageModel = PackageModel.builder().name("Package Name").description("Test").productRequestList(List.of(productModel)).build();
        ProductEntity productEntity =ProductEntity.builder().productId(1L).productName("p1").productDescription("desc").productPrice(BigDecimal.valueOf(10)).build();
        PackageEntity packageEntity = PackageEntity.builder().id(1L).packageName("TestPackage").packageDescription("Desc").totalPackagePrice(BigDecimal.valueOf(10)).products(List.of(productEntity)).build();

        String content = "{\"base\":\"USD\",\"date\":\"2025-02-28\",\"currency\":\"USD\"}";
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.frankfurter.dev/v1/latest?base=USD").build().toUri();
        when(restTemplate.getForEntity(uri, String.class)).thenReturn(ResponseEntity.status(200).body(content));
        when(packageEntityRepository.findById(packageEntity.getId())).thenReturn(Optional.of(new PackageEntity()));
        when(packageEntityRepository.save(packageEntity)).thenReturn(packageEntity);
        when(packageMapper.updatePackageEntity(any(),any(),any())).thenReturn(packageEntity);
        Package pkgEntity = packageService.update(packageModel, packageEntity.getId());
        assertNotNull(pkgEntity, "Package updated");
    }

    @Test
    void updatePackageTest_notFound(){
        ProductModel productModel = ProductModel.builder().productName("p1").productDescription("desc").productPrice(BigDecimal.valueOf(10)).currency("USD").build();
        PackageModel packageModel = PackageModel.builder().name("Package Name").description("Test").productRequestList(List.of(productModel)).build();

        when(packageEntityRepository.findById(1L)).thenReturn(Optional.empty());
        Package pkgEntity = packageService.update(packageModel, 1L);
        assertNull(pkgEntity, "Package not updated");
    }

   @Test
   void getPackageByIdTest(){
       Product productRecord = new Product(1L, "p1", new BigDecimal("10.0"));
       Package packageRecord = new Package(1L, "Package Name", "Test",List.of(productRecord), new BigDecimal("10.0"));

       when(packageEntityRepository.findById(1L)).thenReturn(Optional.of(new PackageEntity()));
       when(packageMapper.mapToPackage(any())).thenReturn(packageRecord);
       Package pkgEntity = packageService.getPackageById(1L);
       assertNotNull(pkgEntity, "Package retrieved");
   }

    @Test
    void getPackageByIdTest_notFound(){
        when(packageEntityRepository.findById(1L)).thenReturn(Optional.empty());
        Package pkgEntity = packageService.getPackageById(1L);
        assertNull(pkgEntity, "Package not found");
    }

    @Test
    void getAllPackageTest(){
        when(packageEntityRepository.findAll()).thenReturn(List.of(new PackageEntity()));
        List<Package> pkgEntity = packageService.getPackages();
        assertNotNull(pkgEntity, "Package list retrieved");
    }

    @Test
    void getAllPackageTest_notFound(){
        when(packageEntityRepository.findAll()).thenReturn(List.of());
        List<Package> pkgEntity = packageService.getPackages();
        assertThat(pkgEntity).isEmpty();
    }

    @Test
    void deletePackageTest(){
        when(packageEntityRepository.findById(1L)).thenReturn(Optional.of(new PackageEntity()));
        willDoNothing().given(packageEntityRepository).deleteById(1L);
        packageService.deletePackageById(1L);
        verify(packageEntityRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePackageTest_notFound(){
        when(packageEntityRepository.findById(1L)).thenReturn(Optional.empty());
        packageService.deletePackageById(1L);
        verify(packageEntityRepository, times(0)).deleteById(1L);
    }

}
