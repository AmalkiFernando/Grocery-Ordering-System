package com.example.demo.controller;

import com.example.demo.domain.Rating;
import com.example.demo.service.RatingService;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/rating")
public class RatingController {

    private final RatingService ratingService;
    private final ProductService productService;

    public RatingController(RatingService ratingService, ProductService productService) {
        this.ratingService = ratingService;
        this.productService = productService;
    }

    // Customer endpoints
    @GetMapping("/product/{productId}")
    public String getRatingsByProduct(@PathVariable Long productId, Model model) {
        model.addAttribute("product", productService.getById(productId).orElse(null));
        model.addAttribute("ratings", ratingService.getRatingsByProduct(productId));
        model.addAttribute("averageRating", ratingService.getAverageRatingByProduct(productId).orElse(0.0));
        model.addAttribute("ratingCount", ratingService.getRatingCountByProduct(productId));
        model.addAttribute("ratingDistribution", ratingService.getRatingDistributionByProduct(productId));
        return "customer/ratings";
    }

    @GetMapping("/submit/{productId}")
    public String showSubmitRatingForm(@PathVariable Long productId, Model model) {
        model.addAttribute("product", productService.getById(productId).orElse(null));
        model.addAttribute("rating", new Rating());
        return "customer/submit-rating";
    }

    @PostMapping("/submit")
    public String submitRating(@ModelAttribute Rating rating,
                             @RequestParam Long productId,
                             @RequestParam String customerName,
                             @RequestParam String customerEmail,
                             @RequestParam Integer ratingValue,
                             RedirectAttributes redirectAttributes) {
        try {
            ratingService.createRating(productId, customerName, customerEmail, ratingValue);
            redirectAttributes.addFlashAttribute("success", "Thank you for your rating!");
            return "redirect:/rating/product/" + productId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/rating/submit/" + productId;
        }
    }

    @GetMapping("/my-ratings")
    public String getMyRatings(@RequestParam String customerEmail, Model model) {
        model.addAttribute("ratings", ratingService.getRatingsByCustomer(customerEmail));
        return "customer/my-ratings";
    }

    @GetMapping("/edit/{id}")
    public String showEditRatingForm(@PathVariable Long id, Model model) {
        Optional<Rating> rating = ratingService.getRatingById(id);
        if (rating.isPresent()) {
            model.addAttribute("rating", rating.get());
            return "customer/edit-rating";
        }
        return "redirect:/customer";
    }

    @PostMapping("/edit/{id}")
    public String updateRating(@PathVariable Long id,
                             @RequestParam Integer ratingValue,
                             RedirectAttributes redirectAttributes) {
        try {
            ratingService.updateRating(id, ratingValue);
            redirectAttributes.addFlashAttribute("success", "Rating updated successfully!");
            return "redirect:/customer";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/rating/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteRating(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ratingService.deleteRating(id);
            redirectAttributes.addFlashAttribute("success", "Rating deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer";
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    public String getAllRatings(Model model) {
        model.addAttribute("ratings", ratingService.getAllRatings());
        return "admin/rating-management";
    }

    @GetMapping("/admin/inactive")
    public String getInactiveRatings(Model model) {
        model.addAttribute("ratings", ratingService.getInactiveRatings());
        return "admin/inactive-ratings";
    }

    @PostMapping("/admin/toggle/{id}")
    public String toggleRatingStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ratingService.toggleRatingStatus(id);
            redirectAttributes.addFlashAttribute("success", "Rating status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rating/admin/all";
    }

    @PostMapping("/admin/restore/{id}")
    public String restoreRating(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ratingService.restoreRating(id);
            redirectAttributes.addFlashAttribute("success", "Rating restored successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rating/admin/inactive";
    }

    @PostMapping("/admin/permanent-delete/{id}")
    public String permanentlyDeleteRating(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ratingService.permanentlyDeleteRating(id);
            redirectAttributes.addFlashAttribute("success", "Rating permanently deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rating/admin/inactive";
    }

    // API endpoints for AJAX calls
    @GetMapping("/api/product/{productId}")
    @ResponseBody
    public List<Rating> getRatingsByProductApi(@PathVariable Long productId) {
        return ratingService.getRatingsByProduct(productId);
    }

    @GetMapping("/api/stats/{productId}")
    @ResponseBody
    public Object getRatingStats(@PathVariable Long productId) {
        return new Object() {
            public final double averageRating = ratingService.getAverageRatingByProduct(productId).orElse(0.0);
            public final long totalCount = ratingService.getRatingCountByProduct(productId);
            public final long fiveStarCount = ratingService.getRatingCountByValue(productId, 5);
            public final long fourStarCount = ratingService.getRatingCountByValue(productId, 4);
            public final long threeStarCount = ratingService.getRatingCountByValue(productId, 3);
            public final long twoStarCount = ratingService.getRatingCountByValue(productId, 2);
            public final long oneStarCount = ratingService.getRatingCountByValue(productId, 1);
            public final List<Object[]> distribution = ratingService.getRatingDistributionByProduct(productId);
        };
    }
}
