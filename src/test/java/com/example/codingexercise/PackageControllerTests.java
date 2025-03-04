package com.example.codingexercise;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.codingexercise.gateway.dto.Package;
import com.example.codingexercise.gateway.dto.Product;
import com.example.codingexercise.model.PackageModel;
import com.example.codingexercise.model.ProductModel;
import com.example.codingexercise.service.PackageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PackageControllerTests {

	private final TestRestTemplate restTemplate;

    @MockBean
    private final PackageService packageService;

    PackageModel packageModel;
    ProductModel productModel;
    Package packageRecord;
    Product productRecord;

    @BeforeEach
    public void setUp(){
        productModel = ProductModel.builder().productName("p1").productDescription("desc").productPrice(BigDecimal.valueOf(10)).currency("USD").build();
        packageModel = PackageModel.builder().name("Package Name").description("Test").productRequestList(List.of(productModel)).build();

        productRecord = new Product(1L, "p1", new BigDecimal("10.0"));
        packageRecord = new Package(1L, "Package Name", "Test",List.of(productRecord), new BigDecimal("10.0"));
    }

    @Autowired
    PackageControllerTests(TestRestTemplate restTemplate, PackageService packageService) {
		this.restTemplate = restTemplate;
        this.packageService = packageService;
    }

    @Test
    @Order(1)
    void createPackage() {
        when(packageService.create(any(PackageModel.class))).thenReturn(packageRecord);
        ResponseEntity<Package> created = restTemplate.postForEntity("/packages", packageModel, Package.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode(), "Package is created");
        Package createdBody = created.getBody();
        assertNotNull(createdBody, "Package created");
        assertEquals("Package Name", createdBody.packageName(), "Package name matched");
        assertEquals("Test", createdBody.packageDescription(), "Package description matched");
        assertEquals(List.of(new Product(1L, "p1", new BigDecimal("10.0"))), createdBody.products(), "Products matched");
    }

    @Test
    @Order(2)
    void getPackage() {
        when(packageService.getPackageById(any(Long.class))).thenReturn(packageRecord);
        ResponseEntity<Package> fetched = restTemplate.getForEntity("/packages/" + 1L, Package.class, packageRecord.packageId());
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Package By Id successful");
    }

    @Test
    @Order(3)
    void getPackage_notFound() {
        when(packageService.getPackageById(any(Long.class))).thenReturn(null);
        ResponseEntity<Package> fetched = restTemplate.getForEntity("/packages/" + 1L, Package.class, packageRecord.packageId());
        assertEquals(HttpStatus.NOT_FOUND, fetched.getStatusCode(), "Package by Id unsuccessful");
    }


    @Test
    @Order(4)
    void getAllPackages() {
        when(packageService.getPackages()).thenReturn(List.of(packageRecord));
        ResponseEntity<Object> fetched = restTemplate.getForEntity("/packages", Object.class);
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Get All Packages successful");
    }

    @Test
    @Order(5)
    void getAllPackages_notFound() {
        when(packageService.getPackages()).thenReturn(List.of());
        ResponseEntity<Object> fetched = restTemplate.getForEntity("/packages", Object.class);
        assertEquals(HttpStatus.NOT_FOUND, fetched.getStatusCode(), "Package list empty");
    }

    @Test
    @Order(6)
    void updatePackageById() {
        when(packageService.getPackageById(any(Long.class))).thenReturn(packageRecord);
        packageRecord = new Package(1L, "Updated Name", "Test",List.of(productRecord), new BigDecimal("10.0"));
        when(packageService.update(any(PackageModel.class), any(Long.class))).thenReturn(packageRecord);
        packageModel.setDescription("Updated Description");
        packageModel.setName("Updated Name");
        HttpEntity<PackageModel> httpEntity = new HttpEntity<>(packageModel, null);
        ResponseEntity<Package> updated = restTemplate.exchange("/packages/" + 1L, HttpMethod.PUT,httpEntity, Package.class);
        assertEquals(HttpStatus.OK, updated.getStatusCode(), "Package is updated");
        assertEquals("Updated Name", updated.getBody().packageName(), "Package name matched");
    }

    @Test
    @Order(7)
    void updatePackage_notFound() {
        when(packageService.update(any(PackageModel.class), any(Long.class))).thenReturn(null);
        HttpEntity<PackageModel> httpEntity = new HttpEntity<>(packageModel, null);
        ResponseEntity<Package> updated = restTemplate.exchange("/packages/" + 1L, HttpMethod.PUT,httpEntity, Package.class);
        assertEquals(HttpStatus.NOT_FOUND, updated.getStatusCode(), "Package not found");
    }

    @Test
    @Order(8)
    void updatePackageById_badRequest() {
        HttpEntity<PackageModel> httpEntity = new HttpEntity<>(packageModel, null);
        ResponseEntity<String> updated = restTemplate.exchange("/packages/1L", HttpMethod.PUT,httpEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, updated.getStatusCode(), "PackageId is invalid");
    }

    @Test
    @Order(9)
    void deletePackageTest() {
        ResponseEntity<String> resp = restTemplate.exchange("/packages/"+ 1L, HttpMethod.DELETE,HttpEntity.EMPTY, String.class);
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    @Test
    @Order(10)
    void deletePackageTest_badRequest() {
        ResponseEntity<String> resp = restTemplate.exchange("/packages/1L", HttpMethod.DELETE,HttpEntity.EMPTY, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }
}

