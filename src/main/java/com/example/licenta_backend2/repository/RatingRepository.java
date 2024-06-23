package com.example.licenta_backend2.repository;

import com.example.licenta_backend2.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByUserId(Long userId);
    List<Rating> findByProductId(Long productId);
    List<Rating> findByUserIdAndProductId(Long userId, Long productId);
}