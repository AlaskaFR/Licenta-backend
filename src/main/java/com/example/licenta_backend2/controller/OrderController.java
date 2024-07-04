package com.example.licenta_backend2.controller;

import com.example.licenta_backend2.dto.ApiResponse;
import com.example.licenta_backend2.model.Order;
import com.example.licenta_backend2.security.AuthenticationService;
import com.example.licenta_backend2.service.OrderService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> placeOrder(@RequestParam("token") String token, @RequestParam("sessionId") String sessionId) {
        try {
            authenticationService.authenticate(token);
            Long userId = authenticationService.getUser(token).getId();
            orderService.placeOrder(userId, sessionId);
            return new ResponseEntity<>(new ApiResponse(true, "Order has been placed"), HttpStatus.CREATED);
        } catch (StripeException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Order>> getAllOrders(@RequestParam("token") String token) {
        authenticationService.authenticate(token);
        Long userId = authenticationService.getUser(token).getId();
        List<Order> orderList = orderService.listOrders(userId);
        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }
}
