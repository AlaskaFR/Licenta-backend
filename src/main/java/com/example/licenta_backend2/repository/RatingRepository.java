package com.example.licenta_backend2.repository;

import com.example.licenta_backend2.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByUserId(Long userId);
    List<Rating> findRatingsByProductId(Long productId);
    List<Rating> findByUserIdAndProductId(Long userId, Long productId);
}