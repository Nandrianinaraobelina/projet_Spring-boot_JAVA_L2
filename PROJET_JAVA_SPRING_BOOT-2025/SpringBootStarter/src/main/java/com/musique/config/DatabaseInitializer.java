package com.musique.config;

import com.musique.model.Equipment;
import com.musique.model.Order;
import com.musique.model.OrderItem;
import com.musique.model.User;
import com.musique.repository.EquipmentRepository;
import com.musique.repository.OrderItemRepository;
import com.musique.repository.OrderRepository;
import com.musique.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Initializes the database with sample data when the application starts.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EquipmentRepository equipmentRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Only initialize if the database is empty
        if (userRepository.count() == 0) {
            initializeUsers();
        }
        
        if (equipmentRepository.count() == 0) {
            initializeEquipment();
        }
        
        if (orderRepository.count() == 0) {
            initializeOrders();
        }
    }
    
    private void initializeUsers() {
        // Create admin user
        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@musique.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ROLE_ADMIN");
        
        // Create regular user
        User customer = new User();
        customer.setName("John Smith");
        customer.setEmail("john@example.com");
        customer.setPassword(passwordEncoder.encode("password123"));
        customer.setRole("ROLE_USER");
        
        userRepository.saveAll(Arrays.asList(admin, customer));
        
        System.out.println("Sample users created successfully.");
    }
    
    private void initializeEquipment() {
        List<Equipment> equipmentList = Arrays.asList(
            createEquipment("Fender Stratocaster", "Professional electric guitar with legendary tone and playability", new BigDecimal("999.99"), "GUITAR", 5, "/img/guitar.svg"),
            createEquipment("Gibson Les Paul", "Classic electric guitar with rich, warm tone", new BigDecimal("1299.99"), "GUITAR", 3, "/img/guitar.svg"),
            createEquipment("Yamaha P-125 Digital Piano", "Compact digital piano with 88 weighted keys", new BigDecimal("699.99"), "KEYBOARD", 8, "/img/keyboard.svg"),
            createEquipment("Pearl Export Drum Set", "Complete 5-piece drum kit with hardware", new BigDecimal("799.99"), "DRUMS", 4, "/img/drums.svg"),
            createEquipment("Shure SM58 Microphone", "Industry-standard dynamic vocal microphone", new BigDecimal("99.99"), "MICROPHONE", 20, "/img/microphone.svg"),
            createEquipment("Ibanez RG550", "High-performance electric guitar for fast players", new BigDecimal("899.99"), "GUITAR", 7, "/img/guitar.svg"),
            createEquipment("Roland TD-17KVX Electronic Drum Kit", "Professional electronic drum kit with mesh heads", new BigDecimal("1599.99"), "DRUMS", 3, "/img/drums.svg"),
            createEquipment("Korg Minilogue Synthesizer", "Polyphonic analog synthesizer with versatile sound", new BigDecimal("549.99"), "KEYBOARD", 6, "/img/keyboard.svg"),
            createEquipment("Martin D-28 Acoustic Guitar", "Premium acoustic guitar with rich tone", new BigDecimal("2799.99"), "GUITAR", 2, "/img/guitar.svg"),
            createEquipment("Fender Jazz Bass", "Classic electric bass with versatile sound", new BigDecimal("1199.99"), "BASS", 4, "/img/guitar.svg"),
            createEquipment("Audio-Technica AT2020 Condenser Microphone", "Studio condenser microphone for recording", new BigDecimal("149.99"), "MICROPHONE", 15, "/img/microphone.svg"),
            createEquipment("Akai MPC Live", "Standalone music production center", new BigDecimal("1199.99"), "PRODUCTION", 5, "/img/keyboard.svg")
        );
        
        equipmentRepository.saveAll(equipmentList);
        
        System.out.println("Sample equipment created successfully.");
    }
    
    private Equipment createEquipment(String name, String description, BigDecimal priceSale, String category, int quantity) {
        Equipment equipment = new Equipment();
        equipment.setName(name);
        equipment.setDescription(description);
        equipment.setPriceSale(priceSale);
        equipment.setCategory(category);
        equipment.setQuantityAvailable(quantity);
        equipment.setActive(true);
        return equipment;
    }
    
    private Equipment createEquipment(String name, String description, BigDecimal priceSale, String category, int quantity, String imageUrl) {
        Equipment equipment = createEquipment(name, description, priceSale, category, quantity);
        equipment.setImageUrl(imageUrl);
        return equipment;
    }
    
    /**
     * Initializes sample orders for the demo users
     */
    private void initializeOrders() {
        try {
            // Get the users
            User user = userRepository.findByEmail("john@example.com").orElse(null);
            User admin = userRepository.findByEmail("admin@musique.com").orElse(null);
            
            if (user == null || admin == null) {
                System.out.println("Cannot create sample orders - users not found");
                return;
            }
            
            // Get some equipment
            List<Equipment> allEquipment = (List<Equipment>) equipmentRepository.findAll();
            if (allEquipment.isEmpty()) {
                System.out.println("Cannot create sample orders - no equipment found");
                return;
            }
            
            // Create a completed sale order for the regular user
            Order saleOrder = new Order();
            saleOrder.setUser(user);
            saleOrder.setOrderDate(LocalDateTime.now().minusDays(5));
            saleOrder.setStatus("COMPLETED");
            saleOrder.setOrderType("SALE");
            
            Equipment guitar = allEquipment.get(0); // Fender Stratocaster
            Equipment mic = allEquipment.get(4);    // Shure SM58
            
            BigDecimal totalAmount = guitar.getPriceSale().add(mic.getPriceSale());
            saleOrder.setTotalAmount(totalAmount);
            
            Order savedSaleOrder = orderRepository.save(saleOrder);
            
            // Add items to the sale order
            OrderItem guitarItem = new OrderItem();
            guitarItem.setOrder(savedSaleOrder);
            guitarItem.setEquipment(guitar);
            guitarItem.setQuantity(1);
            guitarItem.setPrice(guitar.getPriceSale());
            guitarItem.setSubtotal(guitar.getPriceSale());
            guitarItem.setRental(false);
            orderItemRepository.save(guitarItem);
            
            OrderItem micItem = new OrderItem();
            micItem.setOrder(savedSaleOrder);
            micItem.setEquipment(mic);
            micItem.setQuantity(1);
            micItem.setPrice(mic.getPriceSale());
            micItem.setSubtotal(mic.getPriceSale());
            micItem.setRental(false);
            orderItemRepository.save(micItem);
            
            // Rental demo removed
            
            // Create a pending order for the admin (for demonstration)
            Order adminOrder = new Order();
            adminOrder.setUser(admin);
            adminOrder.setOrderDate(LocalDateTime.now().minusHours(3));
            adminOrder.setStatus("PENDING");
            adminOrder.setOrderType("SALE");
            
            Equipment synth = allEquipment.get(7); // Korg Minilogue
            adminOrder.setTotalAmount(synth.getPriceSale());
            
            Order savedAdminOrder = orderRepository.save(adminOrder);
            
            OrderItem synthItem = new OrderItem();
            synthItem.setOrder(savedAdminOrder);
            synthItem.setEquipment(synth);
            synthItem.setQuantity(1);
            synthItem.setPrice(synth.getPriceSale());
            synthItem.setSubtotal(synth.getPriceSale());
            synthItem.setRental(false);
            orderItemRepository.save(synthItem);
            
            System.out.println("Sample orders created successfully");
            
        } catch (Exception e) {
            System.err.println("Error creating sample orders: " + e.getMessage());
            e.printStackTrace();
        }
    }
}