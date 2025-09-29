package com.grocery.ordering.Controller;

import com.grocery.ordering.Service.ProductService;
import com.grocery.ordering.Service.CartService;
import com.grocery.ordering.Service.AuthService;
import com.grocery.ordering.Products.Product;
import com.grocery.ordering.Products.Category;
import com.grocery.ordering.Cart.CartItem;
import com.grocery.ordering.Auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class WebController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private AuthService authService;

    // Very simple in-memory session token store for demo purposes
    private final ConcurrentHashMap<String, Long> sessionTokens = new ConcurrentHashMap<>();

    @GetMapping("/cart")
    public String cart(Model model) {
        // Using demo user for cart operations in this sample
        User user = createDemoUser();
        List<CartItem> items = cartService.getCartItems(user);

        double subtotal = items.stream()
                .map(ci -> ci.getTotalPrice().doubleValue())
                .reduce(0.0, Double::sum);
        double tax = Math.round(subtotal * 0.08 * 100.0) / 100.0; // 8% tax
        double delivery = subtotal > 50 ? 0.0 : 4.99; // free shipping over $50
        double total = Math.round((subtotal + tax + delivery) * 100.0) / 100.0;

        model.addAttribute("cartItems", items);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("tax", tax);
        model.addAttribute("delivery", delivery);
        model.addAttribute("total", total);
        return "cart";
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public String addToCart(@RequestParam Long productId, @RequestParam Integer quantity) {
        try {
            User demoUser = createDemoUser();
            cartService.addToCart(demoUser, productId, quantity);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/cart/update")
    @ResponseBody
    public String updateCartItem(@RequestParam Long cartItemId, @RequestParam Integer quantity) {
        try {
            User demoUser = createDemoUser();
            cartService.updateCartItemQuantity(demoUser, cartItemId, quantity);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/cart/remove")
    @ResponseBody
    public String removeFromCart(@RequestParam Long cartItemId) {
        try {
            User demoUser = createDemoUser();
            cartService.removeFromCart(demoUser, cartItemId);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> loginUser(@RequestParam String email, 
                                        @RequestParam String password,
                                        @RequestParam String userType) {
        try {
            User user = authService.loginUser(email, password);
            
            // Check if user type matches
            boolean isCorrectType = "admin".equals(userType) ? 
                authService.isAdmin(user) : authService.isCustomer(user);
            
            if (!isCorrectType) {
                return Map.of("success", false, "message", "Invalid user type for this account");
            }
            
            String token = UUID.randomUUID().toString();
            sessionTokens.put(token, user.getId());
            return Map.of("success", true, "user", user, "token", token);
        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> registerUser(@RequestParam String name,
                                           @RequestParam String email,
                                           @RequestParam String password,
                                           @RequestParam String userType,
                                           @RequestParam(required = false) String phone,
                                           @RequestParam(required = false) String address) {
        try {
            User user = authService.registerUser(name, email, password, userType);
            if (phone != null) user.setPhoneNumber(phone);
            if (address != null) user.setAddress(address);
            
            String token = UUID.randomUUID().toString();
            sessionTokens.put(token, user.getId());
            return Map.of("success", true, "user", user, "token", token);
        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }
    
    // Helper method to create a demo user
    private User createDemoUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Demo User");
        user.setEmail("demo@example.com");
        return user;
    }
    
    // Helper method to get sample orders
    private List<Object> getSampleOrders() {
        return List.of(
            Map.of("id", "12345", "date", "March 15, 2024", "status", "Processing", 
                   "items", List.of("Fresh Apples (2 lbs)", "Organic Milk (1 bottle)", "Whole Wheat Bread (1 loaf)"),
                   "total", "$14.46", "delivery", "March 16, 2024"),
            Map.of("id", "12344", "date", "March 12, 2024", "status", "Delivered",
                   "items", List.of("Bananas (3 lbs)", "Chicken Breast (2 lbs)"),
                   "total", "$23.95", "delivery", "March 13, 2024"),
            Map.of("id", "12343", "date", "March 10, 2024", "status", "Delivered",
                   "items", List.of("Fresh Orange Juice (1 bottle)", "Whole Wheat Bread (2 loaves)"),
                   "total", "$12.97", "delivery", "March 11, 2024")
        );
    }
}
