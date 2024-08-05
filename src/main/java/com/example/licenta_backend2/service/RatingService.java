package com.example.licenta_backend2.service;

import com.example.licenta_backend2.dto.RatingDTO;
import com.example.licenta_backend2.model.Product;
import com.example.licenta_backend2.model.Rating;
import com.example.licenta_backend2.model.User;
import com.example.licenta_backend2.repository.ProductRepository;
import com.example.licenta_backend2.repository.RatingRepository;
import com.example.licenta_backend2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public void saveRating(RatingDTO ratingDTO) {
        Rating rating = new Rating();

        Optional<Product> product = productRepository.findById(Long.valueOf(ratingDTO.getProductId()));
        Optional<User> user = userRepository.findByEmail(ratingDTO.getUserEmail());

        if (product.isPresent() && user.isPresent()) {
            rating.setProduct(product.get());
            rating.setUser(user.get());
            rating.setCreatedAt(LocalDateTime.now());
            rating.setUpdatedAt(LocalDateTime.now());
            rating.setRating(ratingDTO.getRating());
            ratingRepository.save(rating);
        }
    }

    public double calculateAverageRating(Long productId) {
        List<Rating> ratings = ratingRepository.findRatingsByProductId(productId);
        return ratings.stream().mapToDouble(Rating::getRating).average().orElse(0.0);
    }

    public int countRatings(Long productId) {
        return ratingRepository.findRatingsByProductId(productId).size();
    }

    public List<Long> getRecommendations(Long userId) {
        List<Rating> allRatings = ratingRepository.findAll();
        // Group ratings by user ID
        Map<Long, List<Rating>> ratingsByUser = allRatings.stream().collect(Collectors.groupingBy(r -> r.getUser().getId()));

        // Compute similarity scores between the target user and all other users
        Map<Long, Double> similarityScores = new HashMap<>();
        List<Rating> currentUserRatings = ratingsByUser.getOrDefault(userId, new ArrayList<>());

        for (Map.Entry<Long, List<Rating>> entry : ratingsByUser.entrySet()) {
            Long otherUserId = entry.getKey();
            if (!otherUserId.equals(userId)) {
                double similarity = calculateSimilarity(currentUserRatings, entry.getValue());
                similarityScores.put(otherUserId, similarity);
            }
        }

        // Get top 5 similar users
        int topN = 5;
        List<Long> similarUsers = similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .toList();

        // Collect products highly rated by similar users
        Set<Long> recommendedProducts = new HashSet<>();
        for (Long similarUserId : similarUsers) {
            List<Rating> similarUserRatings = ratingsByUser.get(similarUserId);
            for (Rating rating : similarUserRatings) {
                if (rating.getRating() >= 4.0) {
                    recommendedProducts.add(rating.getProduct().getId());
                }
            }
        }

        // Exclude products already rated by the target user
        Set<Long> userRatedProducts = currentUserRatings.stream()
                .map(r -> r.getProduct().getId())
                .collect(Collectors.toSet());

        recommendedProducts.removeAll(userRatedProducts);

        return new ArrayList<>(recommendedProducts);
    }

    private double calculateSimilarity(List<Rating> ratings1, List<Rating> ratings2) {
        // Create a map of product IDs to ratings for both users
        Map<Long, Double> ratingsMap1 = ratings1.stream()
                .collect(Collectors.toMap(r -> r.getProduct().getId(), Rating::getRating));
        Map<Long, Double> ratingsMap2 = ratings2.stream()
                .collect(Collectors.toMap(r -> r.getProduct().getId(), Rating::getRating));

        // Find common products rated by both users
        Set<Long> commonProducts = new HashSet<>(ratingsMap1.keySet());
        commonProducts.retainAll(ratingsMap2.keySet());

        if (commonProducts.isEmpty()) {
            // No common products, similarity is zero
            return 0.0;
        }

        // Calculate the averages of the ratings
        double avg1 = ratingsMap1.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avg2 = ratingsMap2.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        // Calculate the Pearson correlation coefficient
        double sumProduct = 0.0;
        double sumSquareDiff1 = 0.0;
        double sumSquareDiff2 = 0.0;

        for (Long productId : commonProducts) {
            double diff1 = ratingsMap1.get(productId) - avg1;
            double diff2 = ratingsMap2.get(productId) - avg2;

            sumProduct += diff1 * diff2;
            sumSquareDiff1 += diff1 * diff1;
            sumSquareDiff2 += diff2 * diff2;
        }

        double denominator = Math.sqrt(sumSquareDiff1) * Math.sqrt(sumSquareDiff2);
        return denominator == 0.0 ? 0.0 : sumProduct / denominator;
    }
}
