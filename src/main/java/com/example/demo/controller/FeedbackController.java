package com.example.demo.controller;

import com.example.demo.domain.Feedback;
import com.example.demo.service.FeedbackService;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final ProductService productService;

    public FeedbackController(FeedbackService feedbackService, ProductService productService) {
        this.feedbackService = feedbackService;
        this.productService = productService;
    }

    // Customer endpoints
    @GetMapping("/product/{productId}")
    public String getFeedbackByProduct(@PathVariable Long productId, Model model) {
        model.addAttribute("product", productService.getById(productId).orElse(null));
        model.addAttribute("feedbacks", feedbackService.getFeedbackByProduct(productId));
        model.addAttribute("averageRating", feedbackService.getAverageRatingByProduct(productId).orElse(0.0));
        model.addAttribute("feedbackCount", feedbackService.getFeedbackCountByProduct(productId));
        return "customer/feedback";
    }

    @GetMapping("/submit/{productId}")
    public String showSubmitFeedbackForm(@PathVariable Long productId, Model model) {
        model.addAttribute("product", productService.getById(productId).orElse(null));
        model.addAttribute("feedback", new Feedback());
        return "customer/submit-feedback";
    }

    @PostMapping("/submit")
    public String submitFeedback(@ModelAttribute Feedback feedback, 
                               @RequestParam Long productId,
                               @RequestParam String customerName,
                               @RequestParam String customerEmail,
                               @RequestParam String reviewContent,
                               @RequestParam Integer rating,
                               RedirectAttributes redirectAttributes) {
        try {
            feedbackService.createFeedback(productId, customerName, customerEmail, reviewContent, rating);
            redirectAttributes.addFlashAttribute("success", "Thank you for your feedback!");
            return "redirect:/feedback/product/" + productId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/feedback/submit/" + productId;
        }
    }

    @GetMapping("/my-feedback")
    public String getMyFeedback(@RequestParam String customerEmail, Model model) {
        model.addAttribute("feedbacks", feedbackService.getFeedbackByCustomer(customerEmail));
        return "customer/my-feedback";
    }

    @GetMapping("/edit/{id}")
    public String showEditFeedbackForm(@PathVariable Long id, Model model) {
        Optional<Feedback> feedback = feedbackService.getFeedbackById(id);
        if (feedback.isPresent()) {
            model.addAttribute("feedback", feedback.get());
            return "customer/edit-feedback";
        }
        return "redirect:/customer";
    }

    @PostMapping("/edit/{id}")
    public String updateFeedback(@PathVariable Long id,
                               @RequestParam String reviewContent,
                               @RequestParam Integer rating,
                               RedirectAttributes redirectAttributes) {
        try {
            feedbackService.updateFeedback(id, reviewContent, rating);
            redirectAttributes.addFlashAttribute("success", "Feedback updated successfully!");
            return "redirect:/customer";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/feedback/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            feedbackService.deleteFeedback(id);
            redirectAttributes.addFlashAttribute("success", "Feedback deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer";
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    public String getAllFeedback(Model model) {
        model.addAttribute("feedbacks", feedbackService.getAllFeedback());
        return "admin/feedback-management";
    }

    @GetMapping("/admin/inactive")
    public String getInactiveFeedback(Model model) {
        model.addAttribute("feedbacks", feedbackService.getInactiveFeedback());
        return "admin/inactive-feedback";
    }

    @PostMapping("/admin/toggle/{id}")
    public String toggleFeedbackStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            feedbackService.toggleFeedbackStatus(id);
            redirectAttributes.addFlashAttribute("success", "Feedback status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/feedback/admin/all";
    }

    @PostMapping("/admin/restore/{id}")
    public String restoreFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            feedbackService.restoreFeedback(id);
            redirectAttributes.addFlashAttribute("success", "Feedback restored successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/feedback/admin/inactive";
    }

    @PostMapping("/admin/permanent-delete/{id}")
    public String permanentlyDeleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            feedbackService.permanentlyDeleteFeedback(id);
            redirectAttributes.addFlashAttribute("success", "Feedback permanently deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/feedback/admin/inactive";
    }

    // API endpoints for AJAX calls
    @GetMapping("/api/product/{productId}")
    @ResponseBody
    public List<Feedback> getFeedbackByProductApi(@PathVariable Long productId) {
        return feedbackService.getFeedbackByProduct(productId);
    }

    @GetMapping("/api/stats/{productId}")
    @ResponseBody
    public Object getFeedbackStats(@PathVariable Long productId) {
        return new Object() {
            public final double averageRating = feedbackService.getAverageRatingByProduct(productId).orElse(0.0);
            public final long totalCount = feedbackService.getFeedbackCountByProduct(productId);
            public final long fiveStarCount = feedbackService.getFeedbackCountByRating(productId, 5);
            public final long fourStarCount = feedbackService.getFeedbackCountByRating(productId, 4);
            public final long threeStarCount = feedbackService.getFeedbackCountByRating(productId, 3);
            public final long twoStarCount = feedbackService.getFeedbackCountByRating(productId, 2);
            public final long oneStarCount = feedbackService.getFeedbackCountByRating(productId, 1);
        };
    }
}
