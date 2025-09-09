package com.musique.controller;

import com.musique.model.Order;
import com.musique.repository.OrderRepository;
import com.musique.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controller for managing orders and order history
 */
@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderService orderService;

    /**
     * Display order confirmation page
     * 
     * @param id Order ID
     * @param printMode Set to true for print-friendly version
     * @param model The model
     * @return Order confirmation view
     */
    @GetMapping("/{id}/confirmation")
    public String showConfirmation(@PathVariable Long id, 
                                  @org.springframework.web.bind.annotation.RequestParam(name = "print", required = false, defaultValue = "false") Boolean print,
                                  Model model) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        
        if (orderOpt.isEmpty()) {
            return "redirect:/";
        }
        
        Order order = orderOpt.get();
        model.addAttribute("order", order);
        model.addAttribute("printMode", print);
        
        // Log pour déboguer
        System.out.println("Ordre #" + order.getId() + " chargé, printMode = " + print);
        
        return "order-confirmation";
    }
    
    /**
     * List all orders for the current user
     */
    @GetMapping
    public String listOrders(Model model) {
        // Get the authenticated user
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        
        // Get orders for this user
        Iterable<Order> orders = orderRepository.findByUserEmailOrderByOrderDateDesc(auth.getName());
        
        // Convertir l'iterable en liste pour pouvoir vérifier si elle est vide
        java.util.List<Order> orderList = new java.util.ArrayList<>();
        orders.forEach(orderList::add);
        
        // Log pour le débogage
        System.out.println("Utilisateur: " + auth.getName() + " - Nombre de commandes: " + orderList.size());
        
        model.addAttribute("orders", orderList);
        
        return "orders";
    }

    /**
     * Annule une commande de location
     */
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        
        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Commande non trouvée");
            return "redirect:/orders";
        }
        
        Order order = orderOpt.get();
        
        // Vérifier si c'est une commande de location
        if (!order.getOrderType().equals("RENTAL")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Seules les commandes de location peuvent être annulées");
            return "redirect:/orders";
        }
        
        try {
            orderService.updateOrderStatus(id, "CANCELLED");
            redirectAttributes.addFlashAttribute("successMessage", "La commande a été annulée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'annulation de la commande");
        }
        
        return "redirect:/orders";
    }
}