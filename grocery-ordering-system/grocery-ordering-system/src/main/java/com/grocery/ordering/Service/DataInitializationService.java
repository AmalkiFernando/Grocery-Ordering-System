package com.grocery.ordering.Service;

import com.grocery.ordering.Products.Category;
import com.grocery.ordering.Products.Product;
import com.grocery.ordering.Auth.User;
import com.grocery.ordering.Auth.Role;
import com.grocery.ordering.Repository.CategoryRepository;
import com.grocery.ordering.Repository.ProductRepository;
import com.grocery.ordering.Repository.UserRepository;
import com.grocery.ordering.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

//@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        // Create categories
        Category fruits = createCategory("Fruits & Vegetables", "Fresh fruits and vegetables");
        Category dairy = createCategory("Dairy & Eggs", "Milk, cheese, eggs and dairy products");
        Category meat = createCategory("Meat & Seafood", "Fresh meat, poultry and seafood");
        Category bakery = createCategory("Bakery", "Fresh bread, pastries and baked goods");
        Category beverages = createCategory("Beverages", "Juices, sodas and other drinks");

        // Create products with colorful images
        createProduct("Fresh Apples", "Crisp and sweet red apples, perfect for snacking or baking.", 
                     new BigDecimal("2.99"), fruits, 50, "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400&h=300&fit=crop");
        
        createProduct("Organic Milk", "Fresh organic whole milk from grass-fed cows.", 
                     new BigDecimal("4.99"), dairy, 30, "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=400&h=300&fit=crop");
        
        createProduct("Whole Wheat Bread", "Freshly baked whole wheat bread, perfect for sandwiches.", 
                     new BigDecimal("3.49"), bakery, 25, "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400&h=300&fit=crop");
        
        createProduct("Chicken Breast", "Fresh, lean chicken breast, perfect for healthy meals.", 
                     new BigDecimal("8.99"), meat, 20, "https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=400&h=300&fit=crop");
        
        createProduct("Bananas", "Sweet and ripe bananas, great for breakfast or snacks.", 
                     new BigDecimal("1.99"), fruits, 40, "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=400&h=300&fit=crop");
        
        createProduct("Fresh Orange Juice", "100% pure orange juice, freshly squeezed and packed.", 
                     new BigDecimal("5.99"), beverages, 15, "https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?w=400&h=300&fit=crop");
        
        createProduct("Salmon Fillet", "Fresh Atlantic salmon fillet, perfect for grilling.", 
                     new BigDecimal("12.99"), meat, 10, "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=400&h=300&fit=crop");
        
        createProduct("Greek Yogurt", "Creamy Greek yogurt, high in protein and probiotics.", 
                     new BigDecimal("3.99"), dairy, 35, "https://images.unsplash.com/photo-1571212515410-1a2d4b2b3b3b?w=400&h=300&fit=crop");
        
        createProduct("Carrots", "Fresh, crunchy carrots, great for cooking or snacking.", 
                     new BigDecimal("1.49"), fruits, 60, "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=400&h=300&fit=crop");
        
        createProduct("Croissants", "Buttery, flaky croissants, perfect for breakfast.", 
                     new BigDecimal("4.49"), bakery, 20, "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400&h=300&fit=crop");
        
        createProduct("Strawberries", "Sweet and juicy strawberries, perfect for desserts.", 
                     new BigDecimal("3.99"), fruits, 30, "https://images.unsplash.com/photo-1464965911861-746a04b4bca6?w=400&h=300&fit=crop");
        
        createProduct("Cheese", "Aged cheddar cheese, perfect for sandwiches and snacks.", 
                     new BigDecimal("6.99"), dairy, 25, "https://images.unsplash.com/photo-1486297678162-eb2a19b0a32d?w=400&h=300&fit=crop");
        
        createProduct("Fresh Spinach", "Organic baby spinach, great for salads and smoothies.", 
                     new BigDecimal("2.49"), fruits, 45, "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400&h=300&fit=crop");
        
        createProduct("Ground Beef", "Fresh ground beef, perfect for burgers and meatballs.", 
                     new BigDecimal("7.99"), meat, 15, "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=400&h=300&fit=crop");
        
        createProduct("Fresh Coffee", "Premium roasted coffee beans, perfect for morning brew.", 
                     new BigDecimal("9.99"), beverages, 20, "https://images.unsplash.com/photo-1447933601403-0c6688de566e?w=400&h=300&fit=crop");

        // Create demo user
        createDemoUser();
    }

    private Category createCategory(String name, String description) {
        Category category = new Category(name, description);
        return categoryRepository.save(category);
    }

    private Product createProduct(String name, String description, BigDecimal price, 
                                Category category, Integer stock, String imageUrl) {
        Product product = new Product(name, description, price, category);
        product.setStockQuantity(stock);
        product.setImageUrl(imageUrl);
        product.setRating(4.5);
        product.setReviewCount(25);
        return productRepository.save(product);
    }

    private void createDemoUser() {
        // Check if roles already exist
        Role customerRole = roleRepository.findByName(Role.RoleName.CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(Role.RoleName.CUSTOMER)));
        
        Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(Role.RoleName.ADMIN)));
        
        // Create demo customer if not exists
        if (!userRepository.existsByEmail("customer@example.com")) {
            User customer = new User("Demo Customer", "customer@example.com", "password123");
            customer.setPhoneNumber("+1-555-0123");
            customer.setAddress("123 Main Street");
            customer.setCity("New York");
            customer.setState("NY");
            customer.setZipCode("10001");
            customer.setRoles(Set.of(customerRole));
            userRepository.save(customer);
        }
        
        // Create demo admin if not exists
        if (!userRepository.existsByEmail("admin@example.com")) {
            User admin = new User("Admin User", "admin@example.com", "admin123");
            admin.setPhoneNumber("+1-555-0124");
            admin.setAddress("456 Admin Street");
            admin.setCity("New York");
            admin.setState("NY");
            admin.setZipCode("10002");
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
        }
    }
}
