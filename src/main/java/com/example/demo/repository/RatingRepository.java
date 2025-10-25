package com.example.demo.repository;

import com.example.demo.domain.Rating;
import com.example.demo.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    // Find all ratings for a specific product
    List<Rating> findByProductAndIsActiveTrueOrderByCreatedAtDesc(Product product);
    
    // Find all ratings for a specific product ID
    List<Rating> findByProductIdAndIsActiveTrueOrderByCreatedAtDesc(Long productId);
    
    // Find ratings by customer email
    List<Rating> findByCustomerEmailAndIsActiveTrueOrderByCreatedAtDesc(String customerEmail);
    
    // Find ratings by customer name
    List<Rating> findByCustomerNameAndIsActiveTrueOrderByCreatedAtDesc(String customerName);
    
    // Find ratings by rating value range
    List<Rating> findByRatingValueBetweenAndIsActiveTrueOrderByCreatedAtDesc(Integer minRating, Integer maxRating);
    
    // Find ratings by product and rating value
    List<Rating> findByProductAndRatingValueAndIsActiveTrueOrderByCreatedAtDesc(Product product, Integer ratingValue);
    
    // Find all active ratings
    List<Rating> findByIsActiveTrueOrderByCreatedAtDesc();
    
    // Find all inactive ratings (for admin management)
    List<Rating> findByIsActiveFalseOrderByCreatedAtDesc();
    
    // Count ratings for a product
    long countByProductAndIsActiveTrue(Product product);
    
    // Count ratings by rating value for a product
    long countByProductAndRatingValueAndIsActiveTrue(Product product, Integer ratingValue);
    
    // Find rating by product and customer email (to check if customer already rated)
    Optional<Rating> findByProductAndCustomerEmailAndIsActiveTrue(Product product, String customerEmail);
    
    // Custom query to get average rating for a product
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.product = :product AND r.isActive = true")
    Optional<Double> findAverageRatingByProduct(@Param("product") Product product);
    
    // Custom query to get average rating for a product by ID
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.product.id = :productId AND r.isActive = true")
    Optional<Double> findAverageRatingByProductId(@Param("productId") Long productId);
    
    // Custom query to get rating count by product
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.product = :product AND r.isActive = true")
    long countActiveRatingsByProduct(@Param("product") Product product);
    
    // Custom query to get rating distribution for a product
    @Query("SELECT r.ratingValue, COUNT(r) FROM Rating r WHERE r.product = :product AND r.isActive = true GROUP BY r.ratingValue ORDER BY r.ratingValue")
    List<Object[]> findRatingDistributionByProduct(@Param("product") Product product);
}
