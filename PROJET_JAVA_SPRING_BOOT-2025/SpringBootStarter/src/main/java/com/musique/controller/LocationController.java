package com.musique.controller;

import com.musique.model.Equipment;
import com.musique.repository.EquipmentRepository;
import com.musique.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @GetMapping
    public String showLocationPage(Model model,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                   HttpSession session) {
        // Pre-fill dates if provided
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        // Prefill renter info from session if present
        model.addAttribute("renterName", session.getAttribute("renterName"));
        model.addAttribute("renterEmail", session.getAttribute("renterEmail"));
        model.addAttribute("renterPhone", session.getAttribute("renterPhone"));
        model.addAttribute("renterCin", session.getAttribute("renterCin"));
        model.addAttribute("renterAddress", session.getAttribute("renterAddress"));
        model.addAttribute("results", Collections.emptyList());
        return "location";
    }

    @PostMapping("/search")
    public String searchRentable(Model model,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                 @RequestParam(required = false) String renterName,
                                 @RequestParam(required = false) String renterEmail,
                                 @RequestParam(required = false) String renterPhone,
                                 @RequestParam(required = false) String renterCin,
                                 @RequestParam(required = false) String renterAddress,
                                 HttpSession session) {
        // Basic validation
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
            model.addAttribute("errorMessage", "Dates invalides. La date de fin doit être après la date de début.");
            model.addAttribute("results", Collections.emptyList());
            return "location";
        }

        // Store renter info in session for later checkout prefill
        if (renterName != null) session.setAttribute("renterName", renterName);
        if (renterEmail != null) session.setAttribute("renterEmail", renterEmail);
        if (renterPhone != null) session.setAttribute("renterPhone", renterPhone);
        if (renterCin != null) session.setAttribute("renterCin", renterCin);
        if (renterAddress != null) session.setAttribute("renterAddress", renterAddress);

        // Base list: rentable equipment (available, active, stock > 0, rentalPrice != null)
        List<Equipment> rentable = equipmentRepository
                .findByAvailableTrueAndActiveTrueAndQuantityAvailableGreaterThanAndRentalPriceIsNotNull(0);

        // Exclude equipment with overlapping rentals for requested dates
        rentable = rentable.stream()
                .filter(eq -> !orderItemRepository.existsOverlappingRental(eq.getId(), startDate, endDate))
                .toList();

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("renterName", renterName);
        model.addAttribute("renterEmail", renterEmail);
        model.addAttribute("renterPhone", renterPhone);
        model.addAttribute("renterCin", renterCin);
        model.addAttribute("renterAddress", renterAddress);
        model.addAttribute("results", rentable);
        return "location";
    }
}
