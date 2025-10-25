package com.example.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Username is required")
	@Column(nullable = false)
	private String username;

	@NotNull(message = "Created date is required")
	@Column(nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@NotNull(message = "Updated date is required")
	@Column(nullable = false)
	private LocalDateTime updatedAt = LocalDateTime.now();

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<CartItem> items = new ArrayList<>();

	@NotNull(message = "Subtotal is required")
	@Column(nullable = false)
	private BigDecimal subtotal = BigDecimal.ZERO;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

	public List<CartItem> getItems() { return items; }
	public void setItems(List<CartItem> items) { this.items = items; }

	public BigDecimal getSubtotal() { return subtotal; }
	public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

	// Method to calculate total with shipping
	public BigDecimal calculateTotal() {
		BigDecimal shipping = new BigDecimal("5.99");
		return subtotal.add(shipping);
	}

	// Method to validate cart before checkout
	public boolean isValidForCheckout() {
		return username != null 
			&& !items.isEmpty() 
			&& subtotal.compareTo(BigDecimal.ZERO) > 0;
	}

	// Method to clear cart after successful order
	public void clear() {
		items.clear();
		subtotal = BigDecimal.ZERO;
		updatedAt = LocalDateTime.now();
	}
}

