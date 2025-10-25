package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.domain.Supplier;
import com.example.demo.service.ProductService;
import com.example.demo.service.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final ProductService productService;
	private final SupplierService supplierService;

	public AdminController(ProductService productService, SupplierService supplierService) {
		this.productService = productService;
		this.supplierService = supplierService;
	}

	@GetMapping
	public String adminHome() {
		// Show the products page directly when visiting /admin
		return "forward:/admin/products";
	}

	// Products tab
	@GetMapping("/products")
	public String products(Model model) {
		model.addAttribute("products", productService.getAll());
		model.addAttribute("newProduct", new Product());
		return "admin/products";
	}

	@PostMapping("/products")
	public String addProduct(@ModelAttribute("newProduct") Product product, BindingResult br) {
		if (!br.hasErrors()) {
			productService.create(product);
		}
		return "redirect:/admin/products";
	}

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }

	// Inventory tab
	@GetMapping("/inventory")
	public String inventory(Model model) {
		List<Product> lowStock = productService.getLowStock();
		model.addAttribute("products", productService.getAll());
		model.addAttribute("lowStock", lowStock);
		return "admin/inventory";
	}

	@PostMapping("/inventory/notify/{productId}")
	public String notifySupplier(@PathVariable Long productId) {
		// Placeholder: In real app, send email/notification to supplier
		return "redirect:/admin/inventory";
	}

	// Suppliers tab
	@GetMapping("/suppliers")
	public String suppliers(Model model) {
		model.addAttribute("suppliers", supplierService.getAll());
		model.addAttribute("newSupplier", new Supplier());
		return "admin/suppliers";
	}

	@PostMapping("/suppliers")
	public String addSupplier(@ModelAttribute("newSupplier") Supplier supplier, BindingResult br) {
		if (!br.hasErrors()) {
			supplierService.create(supplier);
		}
		return "redirect:/admin/suppliers";
	}

	@PostMapping("/suppliers/{id}/delete")
	public String deleteSupplier(@PathVariable Long id) {
		supplierService.delete(id);
		return "redirect:/admin/suppliers";
	}
}


