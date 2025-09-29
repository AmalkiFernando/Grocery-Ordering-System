package com.grocery.ordering.Repository;

import com.grocery.ordering.Cart.Cart;
import com.grocery.ordering.Auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserAndStatus(User user, Cart.CartStatus status);
    Optional<Cart> findByUserIdAndStatus(Long userId, Cart.CartStatus status);
}
