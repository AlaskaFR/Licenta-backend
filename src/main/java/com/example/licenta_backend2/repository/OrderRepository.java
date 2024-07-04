package com.example.licenta_backend2.repository;

import com.example.licenta_backend2.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAllByUserIdOrderByCreatedDateDesc(Long userId);
}
