package com.example.licenta_backend2.repository;

import com.example.licenta_backend2.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemsRepository extends JpaRepository<OrderItem, Integer> {
}
