package com.musique.service;

import com.musique.model.Client;
import com.musique.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public Optional<Client> findByEmailIgnoreCase(String email) {
        return clientRepository.findByEmailIgnoreCase(email);
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public long countClients() {
        return clientRepository.count();
    }

    public void incrementPurchaseCount(Client client) {
        client.setPurchaseCount((client.getPurchaseCount() == null ? 0 : client.getPurchaseCount()) + 1);
        clientRepository.save(client);
    }
}


