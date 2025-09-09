package com.musique.repository;

import com.musique.model.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for accessing Equipment entities.
 */
@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    /**
     * Find equipment by name containing the given text (case-insensitive)
     */
    List<Equipment> findByNameContainingIgnoreCase(String name);

    /**
     * Find equipment available for rental (price_rental > 0)
     */
    @Query("SELECT e FROM Equipment e WHERE e.priceRental > 0")
    List<Equipment> findRentableEquipment();

    /**
     * Find equipment with available stock
     */
    List<Equipment> findByQuantityAvailableGreaterThan(int quantity);
    
    /**
     * Find available and active equipment
     */
    List<Equipment> findByAvailableTrueAndActiveTrue();
    
    /**
     * Find active equipment items
     */
    Page<Equipment> findByActiveTrue(Pageable pageable);
    
    /**
     * Find active equipment by category
     */
    Page<Equipment> findByCategoryAndActiveTrue(String category, Pageable pageable);
    
    /**
     * Find equipment with rental price greater than the given amount
     */
    Page<Equipment> findByPriceRentalGreaterThanAndActiveTrue(BigDecimal price, Pageable pageable);
    
    /**
     * Find related equipment in the same category (excluding the current item)
     */
    List<Equipment> findByCategoryAndIdNotAndActiveTrue(String category, Long id, Pageable pageable);
    
    /**
     * Get list of all distinct categories
     */
    @Query("SELECT DISTINCT e.category FROM Equipment e WHERE e.active = true ORDER BY e.category")
    List<String> findDistinctCategories();
}
