package com.grocery.ordering.Service;

import com.grocery.ordering.Cart.Cart;
import com.grocery.ordering.Cart.CartItem;
import com.grocery.ordering.Products.Product;
import com.grocery.ordering.Auth.User;
import com.grocery.ordering.Repository.CartRepository;
import com.grocery.ordering.Repository.CartItemRepository;
import com.grocery.ordering.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    public Cart getOrCreateCart(User user) {
        Optional<Cart> existingCart = cartRepository.findByUserAndStatus(user, Cart.CartStatus.ACTIVE);
        if (existingCart.isPresent()) {
            return existingCart.get();
        }
        
        Cart newCart = new Cart(user);
        return cartRepository.save(newCart);
    }
    
    public Cart addToCart(User user, Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Cart cart = getOrCreateCart(user);
        
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity, product.getPrice());
            cartItemRepository.save(newItem);
        }
        
        updateCartTotal(cart);
        return cart;
    }
    
    public Cart updateCartItemQuantity(User user, Long cartItemId, Integer quantity) {
        Cart cart = getOrCreateCart(user);
        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        
        updateCartTotal(cart);
        return cart;
    }
    
    public Cart removeFromCart(User user, Long cartItemId) {
        Cart cart = getOrCreateCart(user);
        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        cartItemRepository.delete(item);
        updateCartTotal(cart);
        return cart;
    }
    
    public List<CartItem> getCartItems(User user) {
        Cart cart = getOrCreateCart(user);
        return cartItemRepository.findByCart(cart);
    }
    
    private void updateCartTotal(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCart(cart);
        BigDecimal total = items.stream()
            .map(CartItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        cart.setTotalAmount(total);
        cartRepository.save(cart);
    }
    
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteByCart(cart);
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }
}
