package com.example.licenta_backend2.service;

import com.example.licenta_backend2.dto.CartDto;
import com.example.licenta_backend2.dto.CartItemDto;
import com.example.licenta_backend2.dto.PlaceOrderDto;
import com.example.licenta_backend2.model.Order;
import com.example.licenta_backend2.model.OrderItem;
import com.example.licenta_backend2.repository.OrderRepository;
import com.example.licenta_backend2.repository.OrderItemsRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private PaymentService paymentService;

    public void placeOrder(Long userId, String sessionId) throws StripeException {
        // Retrieve cart items for the user
        CartDto cartDto = cartService.listCartItems(userId);

        // Create order DTO
        PlaceOrderDto placeOrderDto = new PlaceOrderDto();
        placeOrderDto.setUserId(userId);
        placeOrderDto.setTotalPrice(cartDto.getTotalCost());

        // Save order and retrieve order ID
        Long orderId = saveOrder(placeOrderDto, userId, sessionId);

        // Process each cart item and save as order item
        List<CartItemDto> cartItemDtoList = cartDto.getCartItems();
        for (CartItemDto cartItemDto : cartItemDtoList) {
            OrderItem orderItem = new OrderItem(
                    orderId,
                    cartItemDto.getProductId(),
                    cartItemDto.getQuantity(),
                    cartItemDto.getPrice().doubleValue());
            orderItemsRepository.save(orderItem);
        }

        // Clear cart items after order placement
        cartService.deleteCartItems(userId);

        // Create a payment intent with Stripe
        PaymentIntent paymentIntent = paymentService.createPaymentIntent(placeOrderDto.getTotalPrice());

        // Confirm payment
        paymentService.confirmPayment(paymentIntent.getId());
    }

    public Long saveOrder(PlaceOrderDto orderDto, Long userId, String sessionID) {
        Order order = getOrderFromDto(orderDto, userId, sessionID);
        return orderRepository.save(order).getId();
    }

    private Order getOrderFromDto(PlaceOrderDto orderDto, Long userId, String sessionID) {
        return new Order(orderDto, userId, sessionID);
    }

    public List<Order> listOrders(Long userId) {
        return orderRepository.findAllByUserIdOrderByCreatedDateDesc(userId);
    }
}
