package com.gi.demo.repository;

import com.gi.demo.dtos.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, String> {
    public Client deleteClientByName(String id);

}
