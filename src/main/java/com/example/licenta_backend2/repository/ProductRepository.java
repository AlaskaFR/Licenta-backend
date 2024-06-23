package com.example.licenta_backend2.repository;

import com.example.licenta_backend2.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
