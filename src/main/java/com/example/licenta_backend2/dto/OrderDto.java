package com.example.licenta_backend2.dto;

import com.example.licenta_backend2.model.Order;

public class OrderDto {
    private Long id;
    private Long userId;

    public OrderDto(Order order) {
        this.id = order.getId();
        this.userId = order.getUserId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
