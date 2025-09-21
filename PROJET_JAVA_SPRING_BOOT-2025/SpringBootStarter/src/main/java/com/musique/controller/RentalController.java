package com.musique.controller;

import com.musique.model.Equipment;
import com.musique.model.OrderItem;
import com.musique.repository.EquipmentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/rental")
public class RentalController {

    private static final BigDecimal DELIVERY_FEE = new BigDecimal("100000");

    @Autowired
    private EquipmentRepository equipmentRepository;

    @PostMapping("/add")
    public String addRentalToCart(@RequestParam Long equipmentId,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        // Validate dates
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
            redirectAttributes.addAttribute("error", "dates");
            return "redirect:/equipment";
        }

        Optional<Equipment> equipmentOpt = equipmentRepository.findById(equipmentId);
        if (!equipmentOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Équipement introuvable.");
            return "redirect:/equipment";
        }

        Equipment equipment = equipmentOpt.get();
        if (equipment.getQuantityAvailable() == null || equipment.getQuantityAvailable() <= 0) {
            redirectAttributes.addAttribute("error", "unavailable");
            return "redirect:/equipment";
        }

        if (equipment.getRentalPrice() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cet équipement n'est pas disponible à la location.");
            return "redirect:/equipment";
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) {
            redirectAttributes.addAttribute("error", "dates");
            return "redirect:/equipment";
        }

        // Build rental order item
        OrderItem rentalItem = new OrderItem();
        rentalItem.setEquipment(equipment);
        rentalItem.setQuantity(1); // one unit per rental line
        rentalItem.setRental(true);
        rentalItem.setRentalDays((int) days);
        rentalItem.setRentalStartDate(startDate);
        rentalItem.setRentalEndDate(endDate);

        BigDecimal rentalTotal = equipment.getRentalPrice().multiply(BigDecimal.valueOf(days)).add(DELIVERY_FEE);
        rentalItem.setPrice(rentalTotal);

        // Decrease available quantity (reserve the item)
        equipment.setQuantityAvailable(equipment.getQuantityAvailable() - 1);
        equipmentRepository.save(equipment);

        // Put in session cart
        List<OrderItem> cart = getCart(session);
        cart.add(rentalItem);
        session.setAttribute("cart", cart);

        redirectAttributes.addFlashAttribute("successMessage",
                "Location ajoutée: " + equipment.getName() + " du " + startDate + " au " + endDate + ".");

        return "redirect:/cart";
    }

    @SuppressWarnings("unchecked")
    private List<OrderItem> getCart(HttpSession session) {
        List<OrderItem> cart = (List<OrderItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}
