package com.musique.controller;

import com.musique.model.Equipment;
import com.musique.model.Client;
import com.musique.model.Order;
import com.musique.model.OrderItem;
import com.musique.model.User;
import com.musique.repository.EquipmentRepository;
import com.musique.repository.OrderRepository;
import com.musique.repository.UserRepository;
import com.musique.service.ClientService;
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

    @Autowired
    private ClientService clientService;

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

        BigDecimal subtotal = cart.stream()
                .map(item -> {
                    if (item.getPrice() != null && item.getQuantity() != null) {
                        return item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcul réduction fidélité (20% si >= 3 achats)
        Optional<User> user = userRepository.findByEmail(auth.getName());
        boolean eligibleDiscount = user.flatMap(u -> clientService.findByEmailIgnoreCase(u.getEmail()))
                .map(c -> c.getPurchaseCount() != null && c.getPurchaseCount() >= 3)
                .orElse(false);

        BigDecimal discount = eligibleDiscount ? subtotal.multiply(new BigDecimal("0.20")) : BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(discount);

        model.addAttribute("cartItems", cart);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discount", discount);
        model.addAttribute("total", total);

        user.ifPresent(u -> model.addAttribute("user", u));

        // Afficher le client sélectionné s'il a été choisi dans le panier (priorité à l'ID)
        Object selectedId = session.getAttribute("selectedClientId");
        if (selectedId instanceof Long selectedClientId) {
            clientService.findById(selectedClientId)
                    .ifPresent(c -> model.addAttribute("clientSelected", c));
        } else {
            // fallback: par email utilisateur
            clientService.findByEmailIgnoreCase(user.map(User::getEmail).orElse(""))
                    .ifPresent(c -> model.addAttribute("clientSelected", c));
        }

        // Rôle utilisateur pour affichage
        model.addAttribute("userRole", user.map(User::getRole).orElse("client").toUpperCase());

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

            // Enregistrer/mettre à jour le client acheteur (CRM léger)
            Client client = clientService.findByEmailIgnoreCase(user.getEmail()).orElseGet(() -> {
                Client c = new Client();
                String[] parts = (name != null ? name : user.getName()).trim().split(" ", 2);
                c.setFirstName(parts.length > 1 ? parts[0] : (name != null ? name : user.getName()));
                c.setLastName(parts.length > 1 ? parts[1] : "");
                c.setEmail(user.getEmail());
                c.setCin("N/A");
                c.setAddress(address);
                c.setPhone("");
                return c;
            });
            clientService.incrementPurchaseCount(client);

            // Réduction 20% si plus de 3 achats (à appliquer prochain achat)
            if (client.getPurchaseCount() != null && client.getPurchaseCount() >= 3) {
                // TODO: marquer un flag de réduction ou générer un coupon; ici on ne modifie pas la commande en cours
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
