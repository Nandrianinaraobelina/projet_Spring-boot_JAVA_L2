package com.musique.controller;

import com.musique.model.Equipment;
import com.musique.model.OrderItem;
import com.musique.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

/**
 * Controller for managing the shopping cart functionality.
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private EquipmentRepository equipmentRepository;

    /**
     * Display the cart contents
     */
    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        // Get the cart from session or create a new one
        List<OrderItem> cart = getCart(session);
        model.addAttribute("cartItems", cart);
        
        // Calculate totals
        BigDecimal total = calculateTotal(cart);
        model.addAttribute("total", total);
        model.addAttribute("totalAmount", total);
        
        // Check if cart is empty
        model.addAttribute("emptyCart", cart.isEmpty());
        
        // Add subtotal to each item
        for (OrderItem item : cart) {
            if (item.getPrice() != null && item.getQuantity() != null) {
                BigDecimal subtotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                item.setSubtotal(subtotal);
            } else {
                item.setSubtotal(BigDecimal.ZERO);
            }
        }
        
        return "cart";
    }

    /**
     * Add an item to the cart
     */
    @PostMapping("/add")
    public String addToCart(
            @RequestParam Long equipmentId,
            @RequestParam(defaultValue = "1") Integer quantity,
            
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(equipmentId);
        if (!equipmentOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Equipment not found.");
            return "redirect:/equipment";
        }
        
        Equipment equipment = equipmentOpt.get();
        
        // Vérifier si la quantité demandée est disponible
        if (quantity > equipment.getQuantityAvailable()) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Désolé, seulement " + equipment.getQuantityAvailable() + " unités sont disponibles.");
            return "redirect:/equipment/" + equipmentId;
        }
        
        List<OrderItem> cart = getCart(session);
        
        // Create the new order item
        OrderItem orderItem = new OrderItem();
        orderItem.setEquipment(equipment);
        orderItem.setQuantity(quantity);
        
        // Regular purchase only
        orderItem.setRental(false);
        orderItem.setPrice(equipment.getPriceSale().multiply(BigDecimal.valueOf(quantity)));
        
        // Mettre à jour la quantité disponible en stock
        equipment.setQuantityAvailable(equipment.getQuantityAvailable() - quantity);
        equipmentRepository.save(equipment);
        
        // Add to cart
        cart.add(orderItem);
        session.setAttribute("cart", cart);
        
        // Add success message
        redirectAttributes.addFlashAttribute("successMessage", 
                quantity + " " + equipment.getName() + " added to cart successfully.");
        
        return "redirect:/cart";
    }
    
    /**
     * Remove an item from the cart by equipment ID
     */
    @GetMapping("/remove/{id}")
    public String removeItemByEquipmentId(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        List<OrderItem> cart = getCart(session);
        
        // Find the index of the item with the specified equipment ID
        int indexToRemove = -1;
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getEquipment().getId().equals(id)) {
                indexToRemove = i;
                break;
            }
        }
        
        // Remove the item if found
        if (indexToRemove >= 0) {
            OrderItem removed = cart.remove(indexToRemove);
            session.setAttribute("cart", cart);
            
            // Restaurer le stock
            Equipment equipment = removed.getEquipment();
            Optional<Equipment> currentEquipment = equipmentRepository.findById(equipment.getId());
            if (currentEquipment.isPresent()) {
                Equipment eq = currentEquipment.get();
                eq.setQuantityAvailable(eq.getQuantityAvailable() + removed.getQuantity());
                equipmentRepository.save(eq);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    removed.getEquipment().getName() + " removed from cart successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Item not found in cart.");
        }
        
        return "redirect:/cart";
    }
    
    /**
     * Remove an item from the cart by index
     */
    @PostMapping("/remove/{index}")
    public String removeFromCart(@PathVariable int index, HttpSession session, RedirectAttributes redirectAttributes) {
        List<OrderItem> cart = getCart(session);
        
        if (index >= 0 && index < cart.size()) {
            OrderItem removed = cart.remove(index);
            session.setAttribute("cart", cart);
            
            // Restaurer le stock
            Equipment equipment = removed.getEquipment();
            Optional<Equipment> currentEquipment = equipmentRepository.findById(equipment.getId());
            if (currentEquipment.isPresent()) {
                Equipment eq = currentEquipment.get();
                eq.setQuantityAvailable(eq.getQuantityAvailable() + removed.getQuantity());
                equipmentRepository.save(eq);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    removed.getEquipment().getName() + " removed from cart successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid item.");
        }
        
        return "redirect:/cart";
    }
    
    /**
     * Clear the entire cart
     */
    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        // Restaurer le stock pour tous les articles du panier
        List<OrderItem> cart = getCart(session);
        for (OrderItem item : cart) {
            Equipment equipment = item.getEquipment();
            Optional<Equipment> currentEquipment = equipmentRepository.findById(equipment.getId());
            if (currentEquipment.isPresent()) {
                Equipment eq = currentEquipment.get();
                eq.setQuantityAvailable(eq.getQuantityAvailable() + item.getQuantity());
                equipmentRepository.save(eq);
            }
        }
        
        session.removeAttribute("cart");
        redirectAttributes.addFlashAttribute("successMessage", "Cart cleared successfully.");
        return "redirect:/cart";
    }
    
    /**
     * Helper method to get cart from session or create a new one
     */
    @SuppressWarnings("unchecked")
    private List<OrderItem> getCart(HttpSession session) {
        List<OrderItem> cart = (List<OrderItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
    
    /**
     * Helper method to calculate total amount
     */
    private BigDecimal calculateTotal(List<OrderItem> cart) {
        return cart.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}