package com.example.licenta_backend2.service;

import com.example.licenta_backend2.dto.CartDto;
import com.example.licenta_backend2.dto.CartItemDto;
import com.example.licenta_backend2.model.Cart;
import com.example.licenta_backend2.model.CartItem;
import com.example.licenta_backend2.repository.CartRepository;
import com.example.licenta_backend2.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public CartDto listCartItems(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        List<CartItem> cartItems = cart.getCartItems();
        List<CartItemDto> cartItemDtos = cartItems.stream()
                .map(cartItem -> new CartItemDto(cartItem.getProduct().getId(), cartItem.getQuantity(), cartItem.getProduct().getPrice()))
                .collect(Collectors.toList());

        CartDto cartDto = new CartDto();
        cartDto.setCartItems(cartItemDtos);
        cartDto.setTotalCost(cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return cartDto;
    }

    public void deleteCartItems(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        List<CartItem> cartItems = cart.getCartItems();
        cartItemRepository.deleteAll(cartItems);
    }
}
