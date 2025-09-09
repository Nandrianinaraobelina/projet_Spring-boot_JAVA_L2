package com.musique.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Entity representing music equipment items available for sale or rental.
 */
@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @NotNull(message = "Sale price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Sale price must be greater than 0")
    @Column(name = "price_sale", nullable = false)
    private BigDecimal priceSale;

    @DecimalMin(value = "0.0", message = "Rental price must be non-negative")
    @Column(name = "price_rental", nullable = false)
    private BigDecimal priceRental = BigDecimal.ZERO;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be non-negative")
    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable;

    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "active", nullable = false)
    private boolean active = true; // Par défaut, tous les nouveaux équipements sont actifs

    @Column(name = "available", nullable = false)
    private boolean available = true; // Par défaut, tous les équipements sont disponibles

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPriceSale() {
        return priceSale;
    }

    public void setPriceSale(BigDecimal priceSale) {
        this.priceSale = priceSale;
    }

    public BigDecimal getPriceRental() {
        return priceRental;
    }

    public void setPriceRental(BigDecimal priceRental) {
        this.priceRental = priceRental;
    }

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Determine if the equipment is available for rental
    public boolean isRentable() {
        return priceRental != null && priceRental.compareTo(BigDecimal.ZERO) > 0 && available;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", priceSale=" + priceSale +
                ", priceRental=" + priceRental +
                ", quantityAvailable=" + quantityAvailable +
                '}';
    }
}
