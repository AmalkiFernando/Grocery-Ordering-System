package com.example.demo.service;

import com.example.demo.domain.Rating;
import com.example.demo.domain.Product;
import com.example.demo.repository.RatingRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;

    public RatingService(RatingRepository ratingRepository, ProductRepository productRepository) {
        this.ratingRepository = ratingRepository;
        this.productRepository = productRepository;
    }

    // Create Rating
    public Rating createRating(Long productId, String customerName, String customerEmail, Integer ratingValue) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        // Check if customer already rated this product
        Optional<Rating> existingRating = ratingRepository
            .findByProductAndCustomerEmailAndIsActiveTrue(product, customerEmail);
        
        if (existingRating.isPresent()) {
            throw new IllegalArgumentException("You have already rated this product");
        }
        
        Rating rating = new Rating(product, customerName, customerEmail, ratingValue);
        return ratingRepository.save(rating);
    }

    // Read Rating operations
    public List<Rating> getAllActiveRatings() {
        return ratingRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    public List<Rating> getRatingsByProduct(Long productId) {
        return ratingRepository.findByProductIdAndIsActiveTrueOrderByCreatedAtDesc(productId);
    }

    public List<Rating> getRatingsByCustomer(String customerEmail) {
        return ratingRepository.findByCustomerEmailAndIsActiveTrueOrderByCreatedAtDesc(customerEmail);
    }

    public List<Rating> getRatingsByValueRange(Integer minRating, Integer maxRating) {
        return ratingRepository.findByRatingValueBetweenAndIsActiveTrueOrderByCreatedAtDesc(minRating, maxRating);
    }

    public Optional<Rating> getRatingById(Long id) {
        return ratingRepository.findById(id);
    }

    public List<Rating> getInactiveRatings() {
        return ratingRepository.findByIsActiveFalseOrderByCreatedAtDesc();
    }

    // Update Rating
    public Rating updateRating(Long id, Integer ratingValue) {
        return ratingRepository.findById(id)
            .map(existing -> {
                existing.setRatingValue(ratingValue);
                existing.setUpdatedAt(java.time.LocalDateTime.now());
                return ratingRepository.save(existing);
            })
            .orElseThrow(() -> new IllegalArgumentException("Rating not found with id: " + id));
    }

    // Delete Rating (soft delete by setting isActive to false)
    public void deleteRating(Long id) {
        ratingRepository.findById(id)
            .ifPresentOrElse(rating -> {
                rating.setIsActive(false);
                ratingRepository.save(rating);
            }, () -> {
                throw new IllegalArgumentException("Rating not found with id: " + id);
            });
    }

    // Hard delete (permanent removal)
    public void permanentlyDeleteRating(Long id) {
        ratingRepository.deleteById(id);
    }

    // Restore deleted rating
    public void restoreRating(Long id) {
        ratingRepository.findById(id)
            .ifPresentOrElse(rating -> {
                rating.setIsActive(true);
                ratingRepository.save(rating);
            }, () -> {
                throw new IllegalArgumentException("Rating not found with id: " + id);
            });
    }

    // Statistics and analytics
    public long getRatingCountByProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        return ratingRepository.countByProductAndIsActiveTrue(product);
    }

    public long getRatingCountByValue(Long productId, Integer ratingValue) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        return ratingRepository.countByProductAndRatingValueAndIsActiveTrue(product, ratingValue);
    }

    public Optional<Double> getAverageRatingByProduct(Long productId) {
        return ratingRepository.findAverageRatingByProductId(productId);
    }

    public boolean hasCustomerRatedProduct(Long productId, String customerEmail) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        return ratingRepository.findByProductAndCustomerEmailAndIsActiveTrue(product, customerEmail).isPresent();
    }

    // Get rating distribution for a product
    public List<Object[]> getRatingDistributionByProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        return ratingRepository.findRatingDistributionByProduct(product);
    }

    // Admin operations
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Rating toggleRatingStatus(Long id) {
        return ratingRepository.findById(id)
            .map(rating -> {
                rating.setIsActive(!rating.getIsActive());
                return ratingRepository.save(rating);
            })
            .orElseThrow(() -> new IllegalArgumentException("Rating not found with id: " + id));
    }
}
