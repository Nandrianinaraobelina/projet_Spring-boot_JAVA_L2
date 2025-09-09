package com.musique.repository;

import com.musique.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
