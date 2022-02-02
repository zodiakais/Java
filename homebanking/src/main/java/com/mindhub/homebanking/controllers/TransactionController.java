package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> doTransaction(@RequestParam String fromAccountNumber,
                                                @RequestParam String toAccountNumber,
                                                @RequestParam String description,
                                                @RequestParam Long amount,
                                                Authentication authentication) {

        Account orgAccount = this.accountRepository.findByNumber(fromAccountNumber);
        Account destAccount = this.accountRepository.findByNumber(toAccountNumber);


        if (fromAccountNumber.isEmpty() || toAccountNumber.isEmpty() || amount == null || description.isEmpty())
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);


        if (accountRepository.findByNumber(fromAccountNumber) == null)
            return new ResponseEntity<>("La cuenta de origen no existe", HttpStatus.FORBIDDEN);

        if (accountRepository.findByNumber(toAccountNumber) == null)
            return new ResponseEntity<>("La cuenta de destiono no exite", HttpStatus.FORBIDDEN);


        if (fromAccountNumber.equals(toAccountNumber))
            return new ResponseEntity<>("Las cuetnas de origen y destino son las mismas!", HttpStatus.FORBIDDEN);


        Client client = clientRepository.findByEmail(authentication.getName());
        Set<Account> accounts = client.getAccounts();
        Iterator iter = accounts.iterator();
        Boolean found = false;
        Account srcAccount = null;
        while (iter.hasNext()) {
            Account account = (Account) iter.next();
            if (account.getNumber().equals(fromAccountNumber)) {
                found = true;

                srcAccount = account;
                break;
            }
        }
        if (!found) {
            return new ResponseEntity<>("El cliente auntentificado no es dueño de la cuenta de origen!", HttpStatus.FORBIDDEN);
        }


        if (srcAccount.getBalance() < amount) {
            return new ResponseEntity<>("Saldo insuficiente para realizar la operacion", HttpStatus.FORBIDDEN);
        }




        Transaction destTransaction = transactionRepository.save(new Transaction(TransactionType.CREDIT, amount, description, LocalDateTime.now(), destAccount));
        Transaction srcTransaction = transactionRepository.save(new Transaction(TransactionType.DEBIT, -amount, description, LocalDateTime.now(), orgAccount));

        srcAccount.setBalance(srcAccount.getBalance() - amount);
        destAccount.setBalance(destAccount.getBalance() + amount);


        return new ResponseEntity<>("Operación completada con éxito", HttpStatus.ACCEPTED);
    }

}
