package com.example.demo.service;

import com.example.demo.domain.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<Product> getAll() {
		return productRepository.findAll();
	}

	public Optional<Product> getById(Long id) {
		return productRepository.findById(id);
	}

	public Product create(Product product) {
		return productRepository.save(product);
	}

	public Product update(Long id, Product updates) {
		return productRepository.findById(id)
			.map(existing -> {
				existing.setName(updates.getName());
				existing.setSku(updates.getSku());
				existing.setPrice(updates.getPrice());
				existing.setQuantityInStock(updates.getQuantityInStock());
				existing.setLowStockThreshold(updates.getLowStockThreshold());
                existing.setImageUrl(updates.getImageUrl());
				return existing;
			})
			.orElseThrow();
	}

	public void delete(Long id) {
		productRepository.deleteById(id);
	}

	public List<Product> getLowStock() {
		return productRepository.findByQuantityInStockLessThanEqual(10);
	}
}


