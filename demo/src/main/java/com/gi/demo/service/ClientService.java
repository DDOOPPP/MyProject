package com.gi.demo.service;

import com.gi.demo.dtos.entity.Client;
import com.gi.demo.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findByName(String name) {
        return clientRepository.findById(name).orElse(null);
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public void delete(String name) {
        clientRepository.deleteById(name);
    }
}
