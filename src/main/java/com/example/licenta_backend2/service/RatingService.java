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
        Map<Long, List<Double>> ratingsMap1 = new HashMap<>();
        ratings1.forEach(rating -> ratingsMap1.computeIfAbsent(rating.getProduct().getId(), element -> new ArrayList<>()).add(rating.getRating()));

        Map<Long, List<Double>> ratingsMap2 = new HashMap<>();
        ratings2.forEach(rating -> ratingsMap2.computeIfAbsent(rating.getProduct().getId(), element -> new ArrayList<>()).add(rating.getRating()));

        Set<Long> commonProducts = new HashSet<>(ratingsMap1.keySet());
        commonProducts.retainAll(ratingsMap2.keySet());

        if (commonProducts.isEmpty()) return 0.0;

        double sumRating1 = 0.0, sumRating2 = 0.0, sumSquaredRating1 = 0.0, sumSquaredRating2 = 0.0, sumRating1Rating2 = 0.0;
        int commonCount = commonProducts.size();

        for (Long productId : commonProducts) {
            double rating1 = ratingsMap1.get(productId).stream().reduce(0.0, Double::sum) / ratingsMap1.get(productId).size();
            double rating2 = ratingsMap2.get(productId).stream().reduce(0.0, Double::sum) / ratingsMap2.get(productId).size();

            sumRating1 += rating1;
            sumRating2 += rating2;

            sumRating1Rating2 += rating1 * rating2;

            sumSquaredRating1 += Math.pow(rating1, 2);
            sumSquaredRating2 += Math.pow(rating2, 2);
        }

        double numerator = commonCount * sumRating1Rating2 - sumRating1 * sumRating2;

        double denominator = Math.sqrt((commonCount * sumSquaredRating1 - sumRating1 * sumRating1) * (commonCount * sumSquaredRating2 - sumRating2 * sumRating2));

        if (denominator == 0) return 0.0;

        return numerator / denominator;
    }
}
