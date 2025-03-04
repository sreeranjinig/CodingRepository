package com.example.codingexercise.service;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.codingexercise.exception.CustomException;
import com.example.codingexercise.gateway.dto.Package;
import com.example.codingexercise.mapper.PackageMapper;
import com.example.codingexercise.model.PackageModel;
import com.example.codingexercise.repository.PackageEntityRepository;
import com.example.codingexercise.repository.entity.PackageEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PackageService {

    private final PackageEntityRepository packageEntityRepository;

    private final PackageMapper packageMapper;

    private final RestTemplate restTemplate;

    ObjectMapper mapper = new ObjectMapper();

    JsonNode node = mapper.createObjectNode();

    /**
     * Method for saving the package to H2 database.
     * If currency is specified :
     * 1. If USD :  saves the data in USD currency
     * 2. Any other currency : Converts to USD currency by querying and retrieving the exchange rate value from third party API - "https://api.frankfurter.dev/v1/latest?base=USD"
     *
     * Total package price is calculated and stored in USD currency.
     * @param newProducts : Package to be saved
     * @return Package
     */
    public Package create(PackageModel newProducts) {
        PackageEntity packageEntity = packageEntityRepository.save(packageMapper.createPackageEntity(newProducts, getExchangeRates()));
        return new Package(packageEntity.getId(), packageEntity.getPackageName(), packageEntity.getPackageDescription(), packageMapper.mapToProduct(packageEntity.getProducts()),
                           packageEntity.getTotalPackagePrice());
    }

    /**
     * Method for updating the package to H2 database.
     *
     * If currency is specified :
     * 1. If USD :  saves the data in USD currency
     * 2. Any other currency : Converts to USD currency by querying and retrieving the exchange rate value from third party API - "https://api.frankfurter.dev/v1/latest?base=USD"
     * Total package price is calculated by deducting the existing price of the product and adding the updated price value.
     *
     * @param newProducts : Package to be updated
     * @param packageId : Package id of the package that needs to be updated
     * @return Package
     */
    public Package update(PackageModel newProducts, Long packageId) {
        Optional<PackageEntity> packageEntity = packageEntityRepository.findById(packageId);
        if (packageEntity.isPresent()){
           PackageEntity updatedPackageEntity = packageEntityRepository.save(packageMapper.updatePackageEntity(packageEntity.get(), newProducts, getExchangeRates()));
            return new Package(updatedPackageEntity.getId(), updatedPackageEntity.getPackageName(), updatedPackageEntity.getPackageDescription(), packageMapper.mapToProduct(updatedPackageEntity.getProducts()),
                               updatedPackageEntity.getTotalPackagePrice());
        }
        return null;
    }

    /**
     * Retrieves all the packages from the database.
     * @return List<Package>
     */
    public List<Package> getPackages() {
        return packageMapper.mapToPackageList(packageEntityRepository.findAll());
    }

    /**
     * Retrieves the package for the corresponding package id
     * @param packageId
     * @return Package
     */
    public Package getPackageById(Long packageId) {
        Optional<PackageEntity> packageEntity = packageEntityRepository.findById(packageId);
        return packageEntity.map(packageMapper::mapToPackage).orElse(null);
    }

    /**
     * Deletes the package for the corresponding package id.
     * @param packageId
     */
    public void deletePackageById(Long packageId) {
        if (packageEntityRepository.findById(packageId).isPresent()) {
            packageEntityRepository.deleteById(packageId);
        }
    }

    /**
     * Method for currency exchange rate API invocation
     * Retrieves the exchange rate value from third party API corresponding to currency USD - "https://api.frankfurter.dev/v1/latest?base=USD"
     *
     * @return JsonNode
     */
    private JsonNode getExchangeRates(){
        if(node.isEmpty()){
            ResponseEntity<String> currencyExchangeRate = restTemplate.getForEntity(URI.create("https://api.frankfurter.dev/v1/latest?base=USD"), String.class);
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            try {
                node = mapper.readTree(currencyExchangeRate.getBody()).get("rates");
            } catch (JsonProcessingException e) {
                throw new CustomException("Exception occurred while parsing currency exchange details");
            }
        }
        return node;
    }

}
