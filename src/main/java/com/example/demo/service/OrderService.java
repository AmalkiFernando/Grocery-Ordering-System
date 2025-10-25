package com.example.demo.service;

import com.example.demo.domain.Order;
import com.example.demo.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> getById(Long id) {
        return orderRepository.findById(id);
    }

    public Order create(Order order) {
        return orderRepository.save(order);
    }

    public Order update(Order order) {
        return orderRepository.save(order);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    public List<Order> getOrdersBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }

    public List<Order> getCompletedOrdersBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findCompletedOrdersBetween(startDate, endDate);
    }

    public Double getTotalSalesBetween(LocalDateTime startDate, LocalDateTime endDate) {
        Double total = orderRepository.getTotalSalesBetween(startDate, endDate);
        return total != null ? total : 0.0;
    }
}
