package com.example.licenta_backend2.controller;

import com.example.licenta_backend2.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Long>> getRecommendations(@PathVariable Long userId) {
        List<Long> recommendedProductIds = ratingService.getRecommendations(userId);
        return ResponseEntity.ok(recommendedProductIds);
    }
}
