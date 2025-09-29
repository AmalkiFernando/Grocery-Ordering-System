package com.grocery.ordering.Service;

import com.grocery.ordering.Products.Product;
import com.grocery.ordering.Products.Category;
import com.grocery.ordering.Repository.ProductRepository;
import com.grocery.ordering.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }
    
    public List<Product> searchProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllActiveProducts();
        }
        return productRepository.searchProducts(searchTerm);
    }
    
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }
    
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
