// BACKEND COMPONENT - Inventory Entity (Function 4: Inventory Management)
package com.grocery.ordering.Inventory;

import com.grocery.ordering.Products.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @NotNull(message = "Current stock is required")
    @Min(value = 0, message = "Current stock cannot be negative")
    @Column(name = "current_stock", nullable = false)
    private Integer currentStock = 0;

    @Min(value = 0, message = "Minimum stock level cannot be negative")
    @Column(name = "min_stock_level")
    private Integer minStockLevel = 10;

    @Min(value = 0, message = "Maximum stock level cannot be negative")
    @Column(name = "max_stock_level")
    private Integer maxStockLevel = 100;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "last_restocked")
    private LocalDateTime lastRestocked;

    @Column(name = "restock_alert_sent")
    private Boolean restockAlertSent = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InventoryStatus status = InventoryStatus.IN_STOCK;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Inventory() {}

    public Inventory(Product product, Integer currentStock, Supplier supplier) {
        this.product = product;
        this.currentStock = currentStock;
        this.supplier = supplier;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
        updateStatus();
    }

    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) {
        this.minStockLevel = minStockLevel;
        updateStatus();
    }

    public Integer getMaxStockLevel() { return maxStockLevel; }
    public void setMaxStockLevel(Integer maxStockLevel) { this.maxStockLevel = maxStockLevel; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public LocalDateTime getLastRestocked() { return lastRestocked; }
    public void setLastRestocked(LocalDateTime lastRestocked) { this.lastRestocked = lastRestocked; }

    public Boolean getRestockAlertSent() { return restockAlertSent; }
    public void setRestockAlertSent(Boolean restockAlertSent) { this.restockAlertSent = restockAlertSent; }

    public InventoryStatus getStatus() { return status; }
    public void setStatus(InventoryStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    private void updateStatus() {
        if (currentStock == null || currentStock == 0) {
            this.status = InventoryStatus.OUT_OF_STOCK;
        } else if (minStockLevel != null && currentStock <= minStockLevel) {
            this.status = InventoryStatus.LOW_STOCK;
        } else {
            this.status = InventoryStatus.IN_STOCK;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        updateStatus();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updateStatus();
    }

    // Enum for inventory status
    public enum InventoryStatus {
        IN_STOCK,
        LOW_STOCK,
        OUT_OF_STOCK
    }
}