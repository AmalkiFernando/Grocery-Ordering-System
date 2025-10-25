package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Customer-read list catalogue
    @GetMapping("/catalogue")
    public String viewCatalogue(Model model) {
        List<Product> products = productService.getAll();
        model.addAttribute("products", products);
        return "customer/index"; // reuse existing customer index template
    }

    // Admin: show add product form
    @GetMapping("/admin/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/products"; // if you have a dedicated template, adjust accordingly
    }

    // Admin: create product
    @PostMapping("/admin/add")
    public String addProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        try {
            productService.create(product);
            redirectAttributes.addFlashAttribute("success", "Product added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/product/admin/list";
    }

    // Admin: list products
    @GetMapping("/admin/list")
    public String listProductsForAdmin(Model model) {
        model.addAttribute("products", productService.getAll());
        return "admin/products"; // admin products management template
    }

    // Admin: update edit form
    @GetMapping("/admin/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "admin/edit-product";
        }
        return "redirect:/product/admin/list";
    }

    // Admin: update product
    @PostMapping("/admin/edit/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        try {
            productService.update(id, product);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/product/admin/list";
    }

    // Admin: delete
    @PostMapping("/admin/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/product/admin/list";
    }

    // API: list products (JSON)
    @GetMapping("/api/list")
    @ResponseBody
    public List<Product> apiListProducts() {
        return productService.getAll();
    }
}
