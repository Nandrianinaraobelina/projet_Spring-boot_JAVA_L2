package com.musique.controller;

import com.musique.model.Client;
import com.musique.repository.ClientRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ClientsController {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/clients")
    public String listClients(Model model) {
        List<Client> clients = clientRepository.findAll();
        model.addAttribute("clients", clients);
        return "admin/clients";
    }

    @GetMapping("/clients/new")
    public String newClient(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("title", "Nouveau client");
        return "admin/client-form";
    }

    @GetMapping("/clients/edit/{id}")
    public String editClient(@PathVariable Long id, Model model) {
        Client client = clientRepository.findById(id).orElse(new Client());
        model.addAttribute("client", client);
        model.addAttribute("title", "Modifier le client");
        return "admin/client-form";
    }

    @PostMapping("/clients/save")
    public String saveClient(@ModelAttribute Client client) {
        clientRepository.save(client);
        return "redirect:/clients";
    }

    @GetMapping("/clients/delete/{id}")
    public String deleteClient(@PathVariable Long id) {
        try {
            clientRepository.deleteById(id);
        } catch (Exception ignored) {}
        return "redirect:/clients";
    }
}


