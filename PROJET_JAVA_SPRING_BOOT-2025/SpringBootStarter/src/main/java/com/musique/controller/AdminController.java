package com.musique.controller;

import com.musique.model.Equipment;
import com.musique.model.Order;
import com.musique.model.User;
import com.musique.repository.EquipmentRepository;
import com.musique.repository.OrderRepository;
import com.musique.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for admin functionalities
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Admin dashboard
     */
    @GetMapping
    public String adminDashboard(Model model) {
        // Get all equipment
        Iterable<Equipment> allEquipment = equipmentRepository.findAll();
        model.addAttribute("equipmentList", allEquipment);
        
        // Get recent orders (limit to 10)
        Iterable<Order> recentOrders = orderRepository.findRecentOrders(PageRequest.of(0, 10)).getContent();
        model.addAttribute("recentOrders", recentOrders);
        
        // Get user count
        long userCount = userRepository.count();
        model.addAttribute("userCount", userCount);
        
        // Get sales count
        long salesCount = orderRepository.countByOrderType("SALE");
        model.addAttribute("salesCount", salesCount);
        
        // Get rental count
        long rentalCount = orderRepository.countByOrderType("RENTAL");
        model.addAttribute("rentalCount", rentalCount);
        
        return "admin/dashboard";
    }
    
    /**
     * View all users
     */
    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    /**
     * View all equipment
     */
    @GetMapping("/equipment")
    public String viewAllEquipment(Model model) {
        Iterable<Equipment> equipment = equipmentRepository.findAll();
        model.addAttribute("equipment", equipment);
        return "admin/equipment";
    }
    
    /**
     * View all orders
     */
    @GetMapping("/orders")
    public String viewAllOrders(Model model) {
        Iterable<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }
}