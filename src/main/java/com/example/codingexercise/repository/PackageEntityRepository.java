package com.example.codingexercise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.codingexercise.repository.entity.PackageEntity;

@Repository
public interface PackageEntityRepository extends JpaRepository<PackageEntity, Long> {
}
