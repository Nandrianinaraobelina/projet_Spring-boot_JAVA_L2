package com.musique.controller;

import com.musique.model.Equipment;
import com.musique.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for handling home page and other general pages.
 */
@Controller
public class HomeController {

    @Autowired
    private EquipmentRepository equipmentRepository;

    /**
     * Display the home page with featured equipment
     */
    @GetMapping("/")
    public String home(Model model) {
        try {
            // Get featured equipment for sale (most expensive items)
            List<Equipment> featuredForSale = equipmentRepository.findAll(
                    PageRequest.of(0, 4, Sort.by("priceSale").descending())
            ).getContent();
            model.addAttribute("featuredForSale", featuredForSale);
            
            // Rental removed: no featured rental section
            
            // Get categories for the category section
            List<String> categories = equipmentRepository.findDistinctCategories();
            model.addAttribute("categories", categories);
        } catch (Exception e) {
            // En cas d'erreur, on initialise avec des listes vides
            model.addAttribute("featuredForSale", new ArrayList<Equipment>());
            // Rental removed: keep empty list out
            model.addAttribute("categories", new ArrayList<String>());
        }
        
        return "index";
    }

    /**
     * Display the about page
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }

    /**
     * Display the contact page
     */
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
    
    /**
     * Display login page
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}