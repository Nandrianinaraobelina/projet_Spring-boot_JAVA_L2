package com.musique.service;

import com.musique.model.Equipment;
import com.musique.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Autowired
    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }



    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }


    public Optional<Equipment> findById(Long id) {
        return equipmentRepository.findById(id);
    }

    public List<Equipment> findByNameContaining(String name) {
        return equipmentRepository.findByNameContainingIgnoreCase(name);
    }


    // Rental removed: no rentable equipment

    public List<Equipment> findAvailableEquipment() {
        return equipmentRepository.findByAvailableTrueAndActiveTrue();
    }

    @Transactional
    public Equipment save(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    /**
     * Supprime un équipement par son ID
     */
    @Transactional
    public void deleteById(Long id) {
        equipmentRepository.deleteById(id);
    }

    /**
     * Compte le nombre total d'équipements
     */
    public long count() {
        return equipmentRepository.count();
    }

    /**
     * Met à jour la quantité à 10 pour tous les équipements en rupture de stock (quantityAvailable == 0)
     */
    @Transactional
    public int restockAllOutOfStockEquipments() {
        // Récupère ceux à 0 en filtrant
        List<Equipment> zeroStock = equipmentRepository.findAll().stream()
                .filter(eq -> eq.getQuantityAvailable() != null && eq.getQuantityAvailable() == 0)
                .toList();
        zeroStock.forEach(eq -> eq.setQuantityAvailable(10));
        equipmentRepository.saveAll(zeroStock);
        return zeroStock.size(); // Retourne le nombre d'équipements modifiés
    }

    /**
     * Met à jour le stock d'un équipement
     */
    @Transactional
    public void updateStock(Long equipmentId, int quantity) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid equipment ID: " + equipmentId));

        int newQuantity = equipment.getQuantityAvailable() - quantity;
        if (newQuantity < 0) {
            throw new IllegalStateException("Not enough stock available for equipment: " + equipment.getName());
        }
        
        equipment.setQuantityAvailable(newQuantity);
        if (newQuantity == 0) {
            equipment.setAvailable(false);
        }
        equipmentRepository.save(equipment);
    }

    /**
     * Mark equipment as available
     */
    @Transactional
    public void markAsAvailable(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid equipment ID: " + equipmentId));
        
        equipment.setAvailable(true);
        equipmentRepository.save(equipment);
    }

    /**
     * Mark equipment as unavailable
     */
    @Transactional
    public void markAsUnavailable(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid equipment ID: " + equipmentId));
        
        equipment.setAvailable(false);
        equipmentRepository.save(equipment);
    }
}
