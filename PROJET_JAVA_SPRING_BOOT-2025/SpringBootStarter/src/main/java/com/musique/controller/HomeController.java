package com.musique.controller;

import com.musique.service.ClientService;
import com.musique.repository.EquipmentRepository;
import com.musique.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling home page and other general pages.
 */
@Controller
public class HomeController {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Display the home page with featured equipment
     */
    @GetMapping("/")
    public String home(Model model) {
        try {
            long totalProduits = equipmentRepository.countByActiveTrue();
            Long totalStock = equipmentRepository.sumQuantityAvailableForActive();
            if (totalStock == null) totalStock = 0L;
            long totalClients = clientService.countClients();
            long totalVentes = orderRepository.countByOrderTypeAndStatus("SALE", "COMPLETED");

            model.addAttribute("totalProduits", totalProduits);
            model.addAttribute("totalClients", totalClients);
            model.addAttribute("totalStock", totalStock);
            model.addAttribute("totalVentes", totalVentes);
        } catch (Exception e) {
            model.addAttribute("totalProduits", 0);
            model.addAttribute("totalClients", 0);
            model.addAttribute("totalStock", 0);
            model.addAttribute("totalVentes", 0);
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