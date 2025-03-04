package com.example.codingexercise.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.codingexercise.gateway.dto.Package;
import com.example.codingexercise.model.PackageModel;
import com.example.codingexercise.service.PackageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import static java.util.Objects.nonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class PackageController {

    private final PackageService packageService;

    @PostMapping(value = "/packages")
    @Operation(summary = "Create a new package")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Package is created", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Package.class))})})
    public ResponseEntity<Package> create(@RequestBody PackageModel newProductPackage) {
        return ResponseEntity.status(HttpStatus.CREATED).body(packageService.create(newProductPackage));
    }

    @PutMapping(value = "/packages/{id}")
    @Operation(summary = "Update a package")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Package is updated", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Package.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)})
    public ResponseEntity<Package> update(@RequestBody PackageModel newProductPackage, @PathVariable Long id) {
        return nonNull(packageService.update(newProductPackage, id)) ?
               ResponseEntity.status(HttpStatus.OK).body(packageService.update(newProductPackage, id)) :
               ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(value = "/packages")
    @Operation(summary = "Get all packages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Package.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)})
    public ResponseEntity<List<Package>> getAll() {
        return !CollectionUtils.isEmpty(packageService.getPackages()) ?
               ResponseEntity.status(HttpStatus.OK).body(packageService.getPackages()) :
               ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(value = "/packages/{id}")
    @Operation(summary = "Get a package")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Package.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)})
    public ResponseEntity<Package> get(@PathVariable Long id) {
        return nonNull(packageService.getPackageById(id)) ?
               ResponseEntity.status(HttpStatus.OK).body(packageService.getPackageById(id)) :
               ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping(value = "/packages/{id}")
    @Operation(summary = "Delete a package")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content)})
    public ResponseEntity<String> delete(@PathVariable Long id) {
        packageService.deletePackageById(id);
        return new ResponseEntity<>("Employee deleted successfully!.", HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String error = "The Package Id you entered is invalid, as it should contain only numbers.";
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


}
