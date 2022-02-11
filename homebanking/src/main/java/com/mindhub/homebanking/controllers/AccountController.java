package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/accounts")
    public List<AccountDTO> getAccount() {
        return this.accountRepository.findAll().stream().map(AccountDTO::new).collect(Collectors.toList());

    }

    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccounts(@PathVariable Long id) {
        return this.accountRepository.findById(id).map(AccountDTO::new).orElse(null);
    }

    @RequestMapping(value = "/clients/current/accounts", method = RequestMethod.GET)
    public List<AccountDTO> getAccounts(Authentication authentication) {
        Client client = this.clientRepository.findByEmail(authentication.getName());
        return client.getAccounts().stream().map(AccountDTO::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/clients/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> createAccounts(Authentication authentication) {
        Client client = this.clientRepository.findByEmail(authentication.getName());
        if (client.getAccounts().size() >= 3) {
            return new ResponseEntity<>("Client´s of accounts limit reached", HttpStatus.FORBIDDEN);
        } else {
            String accountNumber = "VIN" + (int) (Math.random() * 10000000 - 1) + 1;
            accountRepository.save(new Account(accountNumber, LocalDateTime.now(), 0, client));
            return new ResponseEntity<>(HttpStatus.CREATED);

        }

    }
}