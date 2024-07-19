package com.example.licenta_backend2.controller;

import com.example.licenta_backend2.dto.RatingDTO;
import com.example.licenta_backend2.model.Product;
import com.example.licenta_backend2.service.ProductService;
import com.example.licenta_backend2.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{productId}/ratings")
    public ResponseEntity<?> getProductRating(@PathVariable Long productId) {
        return ResponseEntity.ok(setRatingResponse(productId));
    }

    @PostMapping("/{productId}/ratings")
    public ResponseEntity<?> submitRating(@PathVariable Long productId, @RequestBody RatingDTO ratingDTO) {
        ratingService.saveRating(ratingDTO);
        return ResponseEntity.ok(setRatingResponse(productId));
    }

    private RatingResponse setRatingResponse(Long productId) {
        double newAverageRating = ratingService.calculateAverageRating(productId);
        int newRatingCount = ratingService.countRatings(productId);
        return new RatingResponse(newAverageRating, newRatingCount);
    }

    private static class RatingResponse {
        private final double averageRating;
        private final int ratingCount;

        public RatingResponse(double averageRating, int newRatingCount) {
            this.averageRating = averageRating;
            this.ratingCount = newRatingCount;
        }

        public double getAverageRating() {
            return averageRating;
        }

        public int getRatingCount() {
            return ratingCount;
        }
    }
}
