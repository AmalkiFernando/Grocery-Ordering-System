package com.example.demo.service;

import com.example.demo.domain.Feedback;
import com.example.demo.domain.Product;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ProductRepository productRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, ProductRepository productRepository) {
        this.feedbackRepository = feedbackRepository;
        this.productRepository = productRepository;
    }

    // Create Feedback
    public Feedback createFeedback(Long productId, String customerName, String customerEmail, 
                                  String reviewContent, Integer rating) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        // Check if customer already reviewed this product
        Optional<Feedback> existingFeedback = feedbackRepository
            .findByProductAndCustomerEmailAndIsActiveTrue(product, customerEmail);
        
        if (existingFeedback.isPresent()) {
            throw new IllegalArgumentException("You have already reviewed this product");
        }
        
        Feedback feedback = new Feedback(product, customerName, customerEmail, reviewContent, rating);
        return feedbackRepository.save(feedback);
    }

    // Read Feedback operations
    public List<Feedback> getAllActiveFeedback() {
        return feedbackRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    public List<Feedback> getFeedbackByProduct(Long productId) {
        return feedbackRepository.findByProductIdAndIsActiveTrueOrderByCreatedAtDesc(productId);
    }

    public List<Feedback> getFeedbackByCustomer(String customerEmail) {
        return feedbackRepository.findByCustomerEmailAndIsActiveTrueOrderByCreatedAtDesc(customerEmail);
    }

    public List<Feedback> getFeedbackByRatingRange(Integer minRating, Integer maxRating) {
        return feedbackRepository.findByRatingBetweenAndIsActiveTrueOrderByCreatedAtDesc(minRating, maxRating);
    }

    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }

    public List<Feedback> getInactiveFeedback() {
        return feedbackRepository.findByIsActiveFalseOrderByCreatedAtDesc();
    }

    // Update Feedback
    public Feedback updateFeedback(Long id, String reviewContent, Integer rating) {
        return feedbackRepository.findById(id)
            .map(existing -> {
                existing.setReviewContent(reviewContent);
                existing.setRating(rating);
                existing.setUpdatedAt(java.time.LocalDateTime.now());
                return feedbackRepository.save(existing);
            })
            .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id: " + id));
    }

    // Delete Feedback (soft delete by setting isActive to false)
    public void deleteFeedback(Long id) {
        feedbackRepository.findById(id)
            .ifPresentOrElse(feedback -> {
                feedback.setIsActive(false);
                feedbackRepository.save(feedback);
            }, () -> {
                throw new IllegalArgumentException("Feedback not found with id: " + id);
            });
    }

    // Hard delete (permanent removal)
    public void permanentlyDeleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    // Restore deleted feedback
    public void restoreFeedback(Long id) {
        feedbackRepository.findById(id)
            .ifPresentOrElse(feedback -> {
                feedback.setIsActive(true);
                feedbackRepository.save(feedback);
            }, () -> {
                throw new IllegalArgumentException("Feedback not found with id: " + id);
            });
    }

    // Statistics and analytics
    public long getFeedbackCountByProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        return feedbackRepository.countByProductAndIsActiveTrue(product);
    }

    public long getFeedbackCountByRating(Long productId, Integer rating) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        return feedbackRepository.countByProductAndRatingAndIsActiveTrue(product, rating);
    }

    public Optional<Double> getAverageRatingByProduct(Long productId) {
        return feedbackRepository.findAverageRatingByProductId(productId);
    }

    public boolean hasCustomerReviewedProduct(Long productId, String customerEmail) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        return feedbackRepository.findByProductAndCustomerEmailAndIsActiveTrue(product, customerEmail).isPresent();
    }

    // Admin operations
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public Feedback toggleFeedbackStatus(Long id) {
        return feedbackRepository.findById(id)
            .map(feedback -> {
                feedback.setIsActive(!feedback.getIsActive());
                return feedbackRepository.save(feedback);
            })
            .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id: " + id));
    }
}
