package com.example.demo.controller;

import com.example.demo.domain.Cart;
import com.example.demo.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {
	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@GetMapping
	public String viewCart(@AuthenticationPrincipal User user, Model model) {
		Cart cart = cartService.getOrCreateCart(user.getUsername());
		model.addAttribute("cart", cart);
		return "customer/cart";
	}

	@PostMapping("/add/{productId}")
	public String addToCart(@AuthenticationPrincipal User user, @PathVariable Long productId) {
		cartService.addItem(user.getUsername(), productId);
		return "redirect:/cart";
	}

	@PostMapping("/item/{itemId}/inc")
	public String increment(@AuthenticationPrincipal User user, @PathVariable Long itemId) {
		Cart cart = cartService.getOrCreateCart(user.getUsername());
		cart.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst().ifPresent(ci -> {
			cartService.updateQuantity(user.getUsername(), itemId, ci.getQuantity() + 1);
		});
		return "redirect:/cart";
	}

	@PostMapping("/item/{itemId}/dec")
	public String decrement(@AuthenticationPrincipal User user, @PathVariable Long itemId) {
		Cart cart = cartService.getOrCreateCart(user.getUsername());
		cart.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst().ifPresent(ci -> {
			int next = Math.max(1, ci.getQuantity() - 1);
			cartService.updateQuantity(user.getUsername(), itemId, next);
		});
		return "redirect:/cart";
	}

	@PostMapping("/item/{itemId}/delete")
	public String delete(@AuthenticationPrincipal User user, @PathVariable Long itemId) {
		cartService.removeItem(user.getUsername(), itemId);
		return "redirect:/cart";
	}
}

