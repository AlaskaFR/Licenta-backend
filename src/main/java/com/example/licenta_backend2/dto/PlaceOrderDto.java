package com.example.licenta_backend2.dto;

import com.example.licenta_backend2.model.Order;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PlaceOrderDto {
    private Long id;
    private @NotNull Long userId;
    private @NotNull BigDecimal totalPrice;

    public PlaceOrderDto() {}

    public PlaceOrderDto(Order order) {
        this.id = order.getId();
        this.userId = order.getUserId();
        this.totalPrice = order.getTotalPrice();
    }

    // Getters and Setters
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
