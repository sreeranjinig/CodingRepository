package com.example.codingexercise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.codingexercise.repository.entity.ProductEntity;

@Repository
public interface ProductEntityRepository extends JpaRepository<ProductEntity, Long> {
}
