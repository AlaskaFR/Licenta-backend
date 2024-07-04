package com.example.licenta_backend2.repository;

import com.example.licenta_backend2.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findByUserId(Long userId);
}
