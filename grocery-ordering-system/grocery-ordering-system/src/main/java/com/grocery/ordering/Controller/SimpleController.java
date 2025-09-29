package com.grocery.ordering.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SimpleController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/products")
    public String products() {
        return "products";
    }

    // Cart is handled in WebController to include totals and items

    @GetMapping("/orders")
    public String orders() {
        return "orders";
    }
}
