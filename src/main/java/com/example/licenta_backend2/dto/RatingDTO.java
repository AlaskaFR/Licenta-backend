package com.example.licenta_backend2.dto;


public class RatingDTO {

    private Integer id;
    private String productId;
    private String userEmail;
    private Double rating;

    public RatingDTO(Integer id, String productId, String userEmail, Double rating) {
        this.id = id;
        this.productId = productId;
        this.userEmail = userEmail;
        this.rating = rating;
    }
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
