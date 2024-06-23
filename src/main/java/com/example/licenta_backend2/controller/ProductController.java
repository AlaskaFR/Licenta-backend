package com.example.licenta_backend2.controller;

import com.example.licenta_backend2.model.Product;
import com.example.licenta_backend2.model.Rating;
import com.example.licenta_backend2.service.ProductService;
import com.example.licenta_backend2.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private RatingService ratingService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.findById(id)
                .map(existingProduct -> {
                    product.setId(existingProduct.getId());
                    Product updatedProduct = productService.save(product);
                    return ResponseEntity.ok(updatedProduct);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Long productId) {
        Optional<Product> product = productService.findById(productId);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{productId}/ratings")
    public ResponseEntity<List<Rating>> getProductRatings(@PathVariable Long productId) {
        List<Rating> ratings = ratingService.findByProductId(productId);
        return ResponseEntity.ok(ratings);
    }

    @PostMapping("/{productId}/ratings")
    public ResponseEntity<?> submitRating(@PathVariable Long productId, @RequestBody Rating rating) {
        rating.setProduct(new Product());
        rating.getProduct().setId(productId);
        ratingService.saveRating(rating);
        double newAverageRating = ratingService.calculateAverageRating(productId);
        int newRatingCount = ratingService.countRatings(productId);
        return ResponseEntity.ok(new RatingResponse(newAverageRating, newRatingCount));
    }

    private static class RatingResponse {
        private double newAverageRating;
        private int newRatingCount;

        public RatingResponse(double newAverageRating, int newRatingCount) {
            this.newAverageRating = newAverageRating;
            this.newRatingCount = newRatingCount;
        }

        public double getNewAverageRating() {
            return newAverageRating;
        }

        public int getNewRatingCount() {
            return newRatingCount;
        }
    }
}
