package com.example.demo.repository;

import com.example.demo.domain.Feedback;
import com.example.demo.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    // Find all feedback for a specific product
    List<Feedback> findByProductAndIsActiveTrueOrderByCreatedAtDesc(Product product);
    
    // Find all feedback for a specific product ID
    List<Feedback> findByProductIdAndIsActiveTrueOrderByCreatedAtDesc(Long productId);
    
    // Find feedback by customer email
    List<Feedback> findByCustomerEmailAndIsActiveTrueOrderByCreatedAtDesc(String customerEmail);
    
    // Find feedback by customer name
    List<Feedback> findByCustomerNameAndIsActiveTrueOrderByCreatedAtDesc(String customerName);
    
    // Find feedback by rating range
    List<Feedback> findByRatingBetweenAndIsActiveTrueOrderByCreatedAtDesc(Integer minRating, Integer maxRating);
    
    // Find feedback by product and rating
    List<Feedback> findByProductAndRatingAndIsActiveTrueOrderByCreatedAtDesc(Product product, Integer rating);
    
    // Find all active feedback
    List<Feedback> findByIsActiveTrueOrderByCreatedAtDesc();
    
    // Find all inactive feedback (for admin management)
    List<Feedback> findByIsActiveFalseOrderByCreatedAtDesc();
    
    // Count feedback for a product
    long countByProductAndIsActiveTrue(Product product);
    
    // Count feedback by rating for a product
    long countByProductAndRatingAndIsActiveTrue(Product product, Integer rating);
    
    // Find feedback by product and customer email (to check if customer already reviewed)
    Optional<Feedback> findByProductAndCustomerEmailAndIsActiveTrue(Product product, String customerEmail);
    
    // Custom query to get average rating for a product
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.product = :product AND f.isActive = true")
    Optional<Double> findAverageRatingByProduct(@Param("product") Product product);
    
    // Custom query to get average rating for a product by ID
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.product.id = :productId AND f.isActive = true")
    Optional<Double> findAverageRatingByProductId(@Param("productId") Long productId);
    
    // Custom query to get feedback count by product
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.product = :product AND f.isActive = true")
    long countActiveFeedbackByProduct(@Param("product") Product product);
}
