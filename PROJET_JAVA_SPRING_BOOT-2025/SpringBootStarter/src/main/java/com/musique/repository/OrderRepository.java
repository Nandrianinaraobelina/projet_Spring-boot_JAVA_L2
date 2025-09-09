package com.musique.repository;

import com.musique.model.Order;
import com.musique.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for accessing Order entities.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find orders by user
     */
    List<Order> findByUser(User user);
    
    /**
     * Find orders by user email
     */
    List<Order> findByUserEmailOrderByOrderDateDesc(String email);

    /**
     * Find orders by status
     */
    List<Order> findByStatus(String status);
    
    /**
     * Count orders by order type (SALE or RENTAL)
     */
    long countByOrderType(String orderType);
    
    /**
     * Find recent orders with pagination
     */
    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    Page<Order> findRecentOrders(Pageable pageable);

    /**
     * Find rental orders that overlap with a date range for a specific equipment
     */
    @Query("SELECT o FROM Order o JOIN o.orderItems i WHERE " +
           "o.orderType = 'RENTAL' AND " +
           "i.equipment.id = :equipmentId AND " +
           "o.status NOT IN ('CANCELLED') AND " +
           "((o.rentalStartDate <= :endDate AND o.rentalEndDate >= :startDate))")
    List<Order> findOverlappingRentals(
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
