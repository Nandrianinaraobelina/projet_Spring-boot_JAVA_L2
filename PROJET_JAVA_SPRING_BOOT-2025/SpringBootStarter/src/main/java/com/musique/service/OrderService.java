package com.musique.service;

import com.musique.model.Equipment;
import com.musique.model.Order;
import com.musique.model.OrderItem;
import com.musique.model.User;
import com.musique.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for order-related operations.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final EquipmentService equipmentService;

    @Autowired
    public OrderService(OrderRepository orderRepository, EquipmentService equipmentService) {
        this.orderRepository = orderRepository;
        this.equipmentService = equipmentService;
    }

    /**
     * Find all orders
     */
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    /**
     * Find order by ID
     */
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Find orders for a user
     */
    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }

    /**
     * Find orders by status
     */
    public List<Order> findByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Find recent orders
     */
    public List<Order> findRecentOrders(int limit) {
        return orderRepository.findRecentOrders(org.springframework.data.domain.PageRequest.of(0, limit)).getContent();
    }

    /**
     * Count total orders
     */
    public long count() {
        return orderRepository.count();
    }

    /**
     * Create new order with items
     */
    @Transactional
    public Order createOrder(Order order, List<OrderItem> items) {
        // Vérifier d'abord la disponibilité de tous les articles
        for (OrderItem item : items) {
            Equipment equipment = item.getEquipment();
            if (equipment.getQuantityAvailable() < item.getQuantity()) {
                throw new IllegalStateException("Quantité insuffisante pour l'équipement: " + equipment.getName());
            }
        }
        
        // Save order first
        Order savedOrder = orderRepository.save(order);
        
        // Add items to order and update stock
        for (OrderItem item : items) {
            item.setOrder(savedOrder);
            savedOrder.getOrderItems().add(item);
            
            // Update stock quantity and availability
            Equipment equipment = item.getEquipment();
            int newQuantity = equipment.getQuantityAvailable() - item.getQuantity();
            equipment.setQuantityAvailable(newQuantity);
            
            // Mettre à jour la disponibilité uniquement si la quantité atteint 0
            equipment.setAvailable(newQuantity > 0);
            
            equipmentService.save(equipment);
        }
        
        return orderRepository.save(savedOrder);
    }

    /**
     * Update order status
     */
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + orderId));
        
        // If cancelling an order, restore stock and availability only for rentals
        if ("CANCELLED".equals(status) && !status.equals(order.getStatus()) && "RENTAL".equals(order.getOrderType())) {
            restoreStock(order);
        }
        
        order.setStatus(status);
        orderRepository.save(order);
    }

    /**
     * Restore stock when order is cancelled
     */
    private void restoreStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Equipment equipment = item.getEquipment();
            int newQuantity = equipment.getQuantityAvailable() + item.getQuantity();
            equipment.setQuantityAvailable(newQuantity);
            equipment.setAvailable(newQuantity > 0);
            equipmentService.save(equipment);
        }
    }

    /**
     * Check if equipment is available for rental during a specific period
     */
    public boolean isEquipmentAvailableForRental(Long equipmentId, LocalDate startDate, LocalDate endDate, int quantity) {
        // Check overlapping rentals
        List<Order> overlappingOrders = orderRepository.findOverlappingRentals(equipmentId, startDate, endDate);
        
        // Calculate total quantity rented during this period
        int totalRented = 0;
        for (Order order : overlappingOrders) {
            for (OrderItem item : order.getOrderItems()) {
                if (item.getEquipment().getId().equals(equipmentId)) {
                    totalRented += item.getQuantity();
                }
            }
        }
        
        // Check if there's enough stock
        Equipment equipment = equipmentService.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid equipment ID: " + equipmentId));
        
        return equipment.getQuantityAvailable() - totalRented >= quantity;
    }
}
