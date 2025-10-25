package com.example.demo.service;

import com.example.demo.domain.Cart;
import com.example.demo.domain.CartItem;
import com.example.demo.domain.Product;
import com.example.demo.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartService {
	private final CartRepository cartRepository;
	private final ProductService productService;

	public CartService(CartRepository cartRepository, ProductService productService) {
		this.cartRepository = cartRepository;
		this.productService = productService;
	}

	public Cart getOrCreateCart(String username) {
		return cartRepository.findByUsername(username).orElseGet(() -> {
			Cart c = new Cart();
			c.setUsername(username);
			return cartRepository.save(c);
		});
	}

	public Cart addItem(String username, Long productId) {
		Cart cart = getOrCreateCart(username);
		Optional<CartItem> existing = cart.getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).findFirst();
		if (existing.isPresent()) {
			CartItem ci = existing.get();
			ci.setQuantity(ci.getQuantity() + 1);
		} else {
			Product p = productService.getById(productId).orElseThrow();
			CartItem ci = new CartItem();
			ci.setCart(cart);
			ci.setProduct(p);
			ci.setUnitPrice(p.getPrice());
			ci.setQuantity(1);
			cart.getItems().add(ci);
		}
		recalc(cart);
		return cartRepository.save(cart);
	}

	public Cart updateQuantity(String username, Long itemId, int qty) {
		Cart cart = getOrCreateCart(username);
		cart.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst().ifPresent(ci -> ci.setQuantity(qty));
		recalc(cart);
		return cartRepository.save(cart);
	}

	public Cart removeItem(String username, Long itemId) {
		Cart cart = getOrCreateCart(username);
		cart.getItems().removeIf(i -> i.getId().equals(itemId));
		recalc(cart);
		return cartRepository.save(cart);
	}

	private void recalc(Cart cart) {
		BigDecimal subtotal = cart.getItems().stream()
			.map(CartItem::getLineTotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		cart.setSubtotal(subtotal);
	}

	public Cart save(Cart cart) {
		return cartRepository.save(cart);
	}
}

