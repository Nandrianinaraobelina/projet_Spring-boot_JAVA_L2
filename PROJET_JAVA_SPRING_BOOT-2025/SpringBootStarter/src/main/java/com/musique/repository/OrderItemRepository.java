package com.musique.repository;

import com.musique.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for accessing OrderItem entities.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Find all items in a specific order
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * Find all items for a specific equipment
     */
    List<OrderItem> findByEquipmentId(Long equipmentId);

    /**
     * Check if there exists an overlapping rental for the given equipment between start and end dates.
     * Overlap rule: (existing.start < requested.end) AND (existing.end > requested.start)
     * Excludes orders with status 'CANCELLED'.
     */
    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN TRUE ELSE FALSE END " +
           "FROM OrderItem oi JOIN oi.order o " +
           "WHERE oi.equipment.id = :equipmentId " +
           "AND oi.rental = TRUE " +
           "AND o.status <> 'CANCELLED' " +
           "AND oi.rentalStartDate < :endDate " +
           "AND oi.rentalEndDate > :startDate")
    boolean existsOverlappingRental(@Param("equipmentId") Long equipmentId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
}
