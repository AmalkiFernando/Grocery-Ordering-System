package com.example.demo.service;

import com.example.demo.domain.Payment;
import com.example.demo.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment update(Payment payment) {
        return paymentRepository.save(payment);
    }

    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }

    public List<Payment> getPaymentsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }

    public List<Payment> getCompletedPaymentsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findCompletedPaymentsBetween(startDate, endDate);
    }

    public Double getTotalPaymentsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        Double total = paymentRepository.getTotalPaymentsBetween(startDate, endDate);
        return total != null ? total : 0.0;
    }
}
