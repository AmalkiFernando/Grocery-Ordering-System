package com.example.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

@Entity
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "SKU is required")
	@Pattern(regexp = "^[A-Za-z0-9-]+$", message = "SKU must contain only letters, numbers, and hyphens")
	@Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
	@Column(nullable = false, unique = true)
	private String sku;

	@NotBlank(message = "Product name is required")
	@Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
	@Column(nullable = false)
	private String name;

	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.01", message = "Price must be greater than 0")
	@Digits(integer = 10, fraction = 2, message = "Invalid price format")
	@Column(nullable = false)
	private BigDecimal price;

	@NotNull(message = "Quantity in stock is required")
	@Min(value = 0, message = "Quantity in stock cannot be negative")
	@Column(nullable = false)
	private Integer quantityInStock;

	@NotNull(message = "Low stock threshold is required")
	@Min(value = 1, message = "Low stock threshold must be at least 1")
	@Column(nullable = false)
	private Integer lowStockThreshold = 10;

	@URL(message = "Invalid image URL format")
	@Size(max = 255, message = "Image URL must be less than 255 characters")
	@Column
	private String imageUrl;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getQuantityInStock() {
		return quantityInStock;
	}

	public void setQuantityInStock(Integer quantityInStock) {
		this.quantityInStock = quantityInStock;
	}

	public Integer getLowStockThreshold() {
		return lowStockThreshold;
	}

	public void setLowStockThreshold(Integer lowStockThreshold) {
		this.lowStockThreshold = lowStockThreshold;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Transient
	public boolean isLowStock() {
		return quantityInStock != null && lowStockThreshold != null && quantityInStock <= lowStockThreshold;
	}
}

