package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/clients")
    public List<ClientDTO> getClients() {
        return this.clientRepository.findAll().stream().map(ClientDTO::new).collect(toList());
    }

    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id) {
        return this.clientRepository.findById(id).map(ClientDTO::new).orElse(null);
    }

    @RequestMapping("/clients/find/{email}")
    public Client getClientByEmail(@PathVariable String email) {
        return this.clientRepository.findByEmail(email);

    }

    //show authenticated clients
    @RequestMapping("/clients/current")
    public ClientDTO getClients(Authentication authentication) {
        Client client = this.clientRepository.findByEmail(authentication.getName());
        return new ClientDTO(client);
    }

    @RequestMapping(path = "/clients", method = RequestMethod.POST)
    public ResponseEntity<Object> createClients(@RequestParam String firstName, @RequestParam String lastName,
                                               @RequestParam String email, @RequestParam String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (clientRepository.findByEmail(email) != null) {
            return new ResponseEntity<>("Name is already in use", HttpStatus.FORBIDDEN);
        }
        String accountNumber = "VIN" + (int) (Math.random() * 10000000 - 1) + 1;
        Client client = clientRepository.save(new Client(firstName, lastName, email, passwordEncoder.encode(password)));
        accountRepository.save(new Account(accountNumber, LocalDateTime.now(), 0, client));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}
