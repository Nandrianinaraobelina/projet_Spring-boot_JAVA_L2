package com.musique.controller;

import com.musique.model.Equipment;
import com.musique.model.Order;
import com.musique.model.OrderItem;
import com.musique.model.User;
import com.musique.repository.EquipmentRepository;
import com.musique.repository.OrderRepository;
import com.musique.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling checkout and payment process
 */
@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @GetMapping
    public String showCheckout(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login?checkout";
        }

        @SuppressWarnings("unchecked")
        List<OrderItem> cart = (List<OrderItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        BigDecimal total = cart.stream()
                .map(item -> {
                    if (item.getPrice() != null && item.getQuantity() != null) {
                        return item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cartItems", cart);
        model.addAttribute("total", total);

        Optional<User> user = userRepository.findByEmail(auth.getName());
        user.ifPresent(u -> model.addAttribute("user", u));

        return "checkout";
    }

    @PostMapping
    public String processCheckout(
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String city,
            @RequestParam String country,
            @RequestParam String postalCode,
            @RequestParam String cardName,
            @RequestParam String cardNumber,
            @RequestParam String expiryDate,
            @RequestParam String cvv,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login?checkout";
        }

        @SuppressWarnings("unchecked")
        List<OrderItem> cart = (List<OrderItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        Optional<User> userOpt = userRepository.findByEmail(auth.getName());
        if (!userOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Compte utilisateur non trouvé.");
            return "redirect:/cart";
        }

        User user = userOpt.get();

        try {
            Order order = new Order();
            order.setUser(user);
            order.setOrderDate(java.time.LocalDateTime.now());

            order.setShippingName(name);
            order.setShippingAddress(address);
            order.setShippingCity(city);
            order.setShippingCountry(country);
            order.setShippingPostalCode(postalCode);

            BigDecimal total = cart.stream()
                    .map(item -> {
                        if (item.getPrice() != null && item.getQuantity() != null) {
                            return item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                        }
                        return BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            order.setTotalAmount(total);
            order.setStatus("PAID");
            order.setOrderType("SALE");

            Order savedOrder = orderRepository.save(order);

            for (OrderItem item : cart) {
                item.setOrder(savedOrder);
                Equipment equipment = item.getEquipment();
                if (equipment != null) {
                    int newStock = equipment.getQuantityAvailable() - item.getQuantity();
                    equipment.setQuantityAvailable(newStock);
                    equipment.setAvailable(newStock > 0);
                    equipmentRepository.save(equipment);
                }
            }

            session.removeAttribute("cart");

            return "redirect:/orders/" + savedOrder.getId() + "/confirmation";
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de la commande : " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Une erreur est survenue lors du traitement de votre commande. Veuillez réessayer.");
            return "redirect:/cart";
        }
    }
}
