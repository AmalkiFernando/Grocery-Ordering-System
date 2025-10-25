package com.example.demo.repository;

import com.example.demo.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
	Optional<Supplier> findByEmail(String email);
}


