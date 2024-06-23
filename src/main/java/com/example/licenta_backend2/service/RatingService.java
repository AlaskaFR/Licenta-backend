package com.example.licenta_backend2.service;

import com.example.licenta_backend2.model.Rating;
import com.example.licenta_backend2.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepo;

    public List<Rating> findByProductId(Long productId) {
        return ratingRepo.findByProductId(productId);
    }

    public void saveRating(Rating rating) {
        ratingRepo.save(rating);
    }

    public double calculateAverageRating(Long productId) {
        List<Rating> ratings = ratingRepo.findByProductId(productId);
        return ratings.stream().mapToDouble(Rating::getRating).average().orElse(0.0);
    }

    public int countRatings(Long productId) {
        return ratingRepo.findByProductId(productId).size();
    }

    public List<Long> getRecommendations(Long userId) {
        List<Rating> allRatings = ratingRepo.findAll();
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
                .collect(Collectors.toList());

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
        Map<Long, Double> ratingsMap1 = ratings1.stream()
                .collect(Collectors.toMap(r -> r.getProduct().getId(), Rating::getRating));
        Map<Long, Double> ratingsMap2 = ratings2.stream()
                .collect(Collectors.toMap(r -> r.getProduct().getId(), Rating::getRating));

        Set<Long> commonProducts = new HashSet<>(ratingsMap1.keySet());
        commonProducts.retainAll(ratingsMap2.keySet());

        if (commonProducts.isEmpty()) return 0.0;

        double sum1 = 0.0, sum2 = 0.0, sum1Sq = 0.0, sum2Sq = 0.0, productSum = 0.0;
        int commonCount = commonProducts.size();

        for (Long productId : commonProducts) {
            double rating1 = ratingsMap1.get(productId);
            double rating2 = ratingsMap2.get(productId);

            sum1 += rating1;
            sum2 += rating2;
            sum1Sq += Math.pow(rating1, 2);
            sum2Sq += Math.pow(rating2, 2);
            productSum += rating1 * rating2;
        }

        double numerator = productSum - (sum1 * sum2 / commonCount);
        double denominator = Math.sqrt((sum1Sq - Math.pow(sum1, 2) / commonCount) * (sum2Sq - Math.pow(sum2, 2) / commonCount));
        if (denominator == 0) return 0.0;

        return numerator / denominator;
    }
}
