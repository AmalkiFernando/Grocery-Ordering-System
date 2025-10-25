package com.example.demo.controller;

import com.example.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	private final ProductService productService;

	public CustomerController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public String customerHome(Model model) {
		model.addAttribute("products", productService.getAll());
		return "customer/index";
	}
}


