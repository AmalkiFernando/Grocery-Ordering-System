package com.example.demo.service;

import com.example.demo.domain.Supplier;
import com.example.demo.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SupplierService {

	private final SupplierRepository supplierRepository;

	public SupplierService(SupplierRepository supplierRepository) {
		this.supplierRepository = supplierRepository;
	}

	public List<Supplier> getAll() {
		return supplierRepository.findAll();
	}

	public Optional<Supplier> getById(Long id) {
		return supplierRepository.findById(id);
	}

	public Supplier create(Supplier supplier) {
		return supplierRepository.save(supplier);
	}

	public Supplier update(Long id, Supplier updates) {
		return supplierRepository.findById(id)
			.map(existing -> {
				existing.setName(updates.getName());
				existing.setEmail(updates.getEmail());
				existing.setPhone(updates.getPhone());
				existing.setAddress(updates.getAddress());
				return existing;
			})
			.orElseThrow();
	}

	public void delete(Long id) {
		supplierRepository.deleteById(id);
	}
}


