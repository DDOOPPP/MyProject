package com.gi.demo.controller;

import com.gi.demo.dtos.entity.Client;
import com.gi.demo.service.ClientService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping ("/api/client")
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    public String addClient(@RequestBody Client client) {
        log.info("addClient: {}", client);

        clientService.save(client);

        return "%s 설정 완료".formatted(client);
    }

    @GetMapping
    public List<Client> getAllClients() {
        log.info("Request Web");
        return clientService.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable String id) {
        clientService.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable String id) {
        log.info("getClient: {}", id);

        Client client = clientService.findByName(id);

        return ResponseEntity.ok(client);
    }
}
