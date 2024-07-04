package com.example.licenta_backend2.repository;

import com.example.licenta_backend2.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
}
