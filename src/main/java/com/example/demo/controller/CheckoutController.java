package com.example.demo.controller;

import com.example.demo.domain.*;
import com.example.demo.service.CartService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.UUID;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
@RequestMapping
public class CheckoutController {
	private final CartService cartService;
	private final OrderService orderService;
	private final CustomerService customerService;

	public CheckoutController(CartService cartService, OrderService orderService, CustomerService customerService) {
		this.cartService = cartService;
		this.orderService = orderService;
		this.customerService = customerService;
	}

	@GetMapping("/checkout")
	public String checkout(@AuthenticationPrincipal User user, Model model) {
		Cart cart = cartService.getOrCreateCart(user.getUsername());
		model.addAttribute("cart", cart);
		return "customer/checkout";
	}

	@PostMapping("/checkout/confirm")
	public String confirm(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails, 
						 String fullName,
						 String address,
						 String city,
						 String cardNumber,
						 Model model) {
		try {
			// Validate input
			if (fullName == null || fullName.trim().isEmpty()) {
				model.addAttribute("error", "Full name is required");
				return "customer/checkout";
			}
			if (address == null || address.trim().isEmpty()) {
				model.addAttribute("error", "Address is required");
				return "customer/checkout";
			}
			if (city == null || city.trim().isEmpty()) {
				model.addAttribute("error", "City is required");
				return "customer/checkout";
			}
			if (cardNumber == null || cardNumber.trim().isEmpty()) {
				model.addAttribute("error", "Card number is required");
				return "customer/checkout";
			}

			Cart cart = cartService.getOrCreateCart(userDetails.getUsername());
			if (cart.getItems().isEmpty()) {
				model.addAttribute("error", "Cart is empty");
				return "redirect:/cart";
			}

			// Create and save order
			Order order = new Order();
			order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
			order.setStatus(Order.OrderStatus.CONFIRMED);
			
			// Get the Customer entity for the current user
			Customer customer = customerService.findByUsername(userDetails.getUsername());
			if (customer == null) {
				throw new RuntimeException("Customer not found");
			}
			order.setCustomer(customer);
			
			// Add shipping address to Customer if not exists
			customer.setAddress(address + ", " + city);
			customer = customerService.save(customer);
			
			// Calculate total including shipping
			BigDecimal total = cart.calculateTotal();
			if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
				throw new RuntimeException("Invalid cart total");
			}
			order.setTotalAmount(total);
			
			// Transfer cart items to order items
			for (CartItem cartItem : cart.getItems()) {
				OrderItem orderItem = new OrderItem();
				orderItem.setOrder(order);
				orderItem.setProduct(cartItem.getProduct());
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setUnitPrice(cartItem.getUnitPrice());
				order.getOrderItems().add(orderItem);
			}
			
			// Save the order
			order = orderService.create(order);
			
			// Clear the cart after successful order
			cart.clear();
			cartService.save(cart);

			model.addAttribute("order", order);
			return "customer/order-confirmed";
		} catch (Exception e) {
			model.addAttribute("error", "Error processing order: " + e.getMessage());
			return "customer/checkout";
		}
	}
}