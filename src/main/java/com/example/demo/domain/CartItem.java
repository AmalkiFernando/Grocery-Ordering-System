package com.example.demo.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
public class CartItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id", nullable = false)
	private Cart cart;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false)
	private Integer quantity = 1;

	@Column(nullable = false)
	private BigDecimal unitPrice;

	@Column(nullable = false)
	private BigDecimal lineTotal;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public Cart getCart() { return cart; }
	public void setCart(Cart cart) { this.cart = cart; }

	public Product getProduct() { return product; }
	public void setProduct(Product product) { this.product = product; }

	public Integer getQuantity() { return quantity; }
	public void setQuantity(Integer quantity) { this.quantity = quantity; recalc(); }

	public BigDecimal getUnitPrice() { return unitPrice; }
	public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; recalc(); }

	public BigDecimal getLineTotal() { return lineTotal; }
	public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }

	private void recalc(){
		if (unitPrice != null && quantity != null) {
			this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
		}
	}
}

