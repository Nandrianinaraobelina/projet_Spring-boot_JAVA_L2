package com.musique.controller;

import com.musique.model.Equipment;
import com.musique.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller handling equipment-related requests.
 */
@Controller
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    private com.musique.service.EquipmentService equipmentService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    /**
     * Endpoint pour restocker tous les équipements en rupture de stock à 10
     */
    @org.springframework.web.bind.annotation.PostMapping("/restock-out-of-stock")
    @org.springframework.web.bind.annotation.ResponseBody
    public java.util.Map<String, Object> restockAllOutOfStock() {
        int updated = equipmentService.restockAllOutOfStockEquipments();
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("updatedCount", updated);
        result.put("message", updated + " équipements restockés à 10");
        return result;
    }

    /**
     * Display a list of equipment.
     *
     * @param model The model to add attributes to
     * @param page The page number (0-based)
     * @param size The page size
     * @param rental Filter for rental items
     * @param category Filter by category
     * @return The view name
     */
    @GetMapping
    public String listEquipment(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Equipment> equipmentPage = equipmentRepository.findByActiveTrue(pageable);

            model.addAttribute("equipment", equipmentPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", equipmentPage.getTotalPages());
            model.addAttribute("totalItems", equipmentPage.getTotalElements());
        } catch (Exception e) {
            // En cas d'erreur, initialiser avec des listes vides
            model.addAttribute("equipment", new ArrayList<Equipment>());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0);
        }

        return "equipment";
    }

    /**
     * Display details for a specific equipment item.
     *
     * @param id The equipment ID
     * @param model The model to add attributes to
     * @return The view name
     */
    @GetMapping("/{id}")
    public String equipmentDetails(@PathVariable Long id, Model model) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(id);
        
        if (equipmentOpt.isEmpty()) {
            return "redirect:/equipment";
        }
        
        Equipment equipment = equipmentOpt.get();
        
        // Vérifier si l'équipement est disponible
        if (!equipment.isAvailable()) {
            model.addAttribute("errorMessage", "Désolé, cet équipement n'est plus disponible car il a été réservé.");
        }
        
        // Vérifier la quantité disponible
        if (equipment.getQuantityAvailable() <= 0) {
            model.addAttribute("errorMessage", "Désolé, cet équipement est actuellement en rupture de stock.");
        }
        
        model.addAttribute("equipment", equipment);
        
        // Trouver des équipements similaires
        List<Equipment> similarEquipment = equipmentRepository
                .findByCategoryAndIdNotAndActiveTrue(equipment.getCategory(), equipment.getId(), 
                        org.springframework.data.domain.PageRequest.of(0, 4));
        model.addAttribute("similarEquipment", similarEquipment);
        
        return "equipment-detail";
    }
    
    /**
     * Display a list of recently added equipment.
     *
     * @param model The model to add attributes to
     * @return The view name
     */
    /**
     * Page des équipements récents - modifiée pour éviter le problème 404
     */
    @GetMapping("/recent")
    public String recentEquipment(Model model) {
        try {
            // Get the most recently added equipment (limited to 8)
            Pageable pageable = PageRequest.of(0, 8, Sort.by("id").descending());
            Page<Equipment> recentEquipmentPage = equipmentRepository.findByActiveTrue(pageable);
            
            model.addAttribute("equipment", recentEquipmentPage.getContent());
            model.addAttribute("title", "Équipements récemment ajoutés");
            model.addAttribute("isRecentView", true);
            
            // Filter UI removed; categories not required here

            // Log pour le débogage
            System.out.println(">>> Page RECENT - Nombre d'équipements chargés : " + recentEquipmentPage.getContent().size());
        } catch (Exception e) {
            // En cas d'erreur, initialiser avec des listes vides
            model.addAttribute("equipment", new ArrayList<Equipment>());
            model.addAttribute("title", "Équipements récemment ajoutés");
            model.addAttribute("categories", new ArrayList<String>());
            // Log pour le débogage
            System.out.println(">>> ERREUR Page RECENT : " + e.getMessage());
            e.printStackTrace();
        }
        
        return "equipment";
    }
    
    /**
     * Une route alternative pour les équipements récents, au cas où l'URL principale ne fonctionne pas
     */
    @GetMapping("/latest")
    public String latestEquipment(Model model) {
        return recentEquipment(model);
    }
    
    // Rental removed: reservation route deleted

    @GetMapping("/{id}/increase-stock")
    public String increaseStock(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int quantity,
            @org.springframework.web.bind.annotation.RequestHeader(value = "Referer", required = false) String referer
    ) {
        try {
            Optional<Equipment> equipmentOpt = equipmentRepository.findById(id);
            
            if (equipmentOpt.isEmpty()) {
                return "redirect:/equipment/stock?error=equipmentNotFound";
            }
            
            Equipment equipment = equipmentOpt.get();
            int currentStock = equipment.getQuantityAvailable();
            equipment.setQuantityAvailable(currentStock + quantity);
            equipment.setAvailable(true);
            equipmentRepository.save(equipment);
            
            // Rediriger de préférence vers la page précédente ou sinon vers le catalogue
            if (referer != null && !referer.isBlank()) {
                return "redirect:" + referer;
            }
            return "redirect:/equipment?stockUpdated=true&equipmentId=" + id;
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du stock : " + e.getMessage());
            return "redirect:/equipment/stock?error=true";
        }
    }

    @GetMapping("/stock")
    public String stockManagement(Model model) {
        try {
            // Récupérer tous les équipements actifs avec une pagination illimitée
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            List<Equipment> allEquipment = equipmentRepository.findByActiveTrue(pageable).getContent();
            model.addAttribute("equipment", allEquipment);
            return "admin/stock-management";
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage de la gestion du stock : " + e.getMessage());
            model.addAttribute("errorMessage", "Erreur lors du chargement de la gestion du stock");
            return "admin/stock-management";
        }
    }

    /**
     * Met à 10 le stock de tous les équipements
     */
    @org.springframework.web.bind.annotation.PostMapping("/stock/set-all-to-ten")
    public String setAllStocksToTen(org.springframework.ui.Model model) {
        try {
            int updated = equipmentService.setAllStocksToTen();
            return "redirect:/equipment/stock?stockUpdated=true&updated=" + updated;
        } catch (Exception e) {
            return "redirect:/equipment/stock?error=true";
        }
    }
}