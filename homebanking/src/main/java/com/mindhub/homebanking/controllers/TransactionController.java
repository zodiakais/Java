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
import java.util.Iterator;
import java.util.Set;

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
        Client client = clientRepository.findByEmail(authentication.getName());
        Account orgAccount = this.accountRepository.findByNumber(fromAccountNumber);
        Account destAccount = this.accountRepository.findByNumber(toAccountNumber);


        if (fromAccountNumber.isEmpty() || toAccountNumber.isEmpty() || amount == null || description.isEmpty())
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);


        if (accountRepository.findByNumber(fromAccountNumber) == null)
            return new ResponseEntity<>("Source account does not exist", HttpStatus.FORBIDDEN);

        if (accountRepository.findByNumber(toAccountNumber) == null)
            return new ResponseEntity<>("Destination account does not exist", HttpStatus.FORBIDDEN);


        if (fromAccountNumber.equals(toAccountNumber))
            return new ResponseEntity<>("Accounts are the same", HttpStatus.FORBIDDEN);


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
            return new ResponseEntity<>("The authenticated client does not own the source account", HttpStatus.FORBIDDEN);
        }


        if (srcAccount.getBalance() < amount) {
            return new ResponseEntity<>("Insufficient balance to carry out the operation", HttpStatus.FORBIDDEN);
        }


        Transaction destTransaction = transactionRepository.save(new Transaction(TransactionType.CREDIT, amount, description, LocalDateTime.now(), destAccount));
        Transaction srcTransaction = transactionRepository.save(new Transaction(TransactionType.DEBIT, -amount, description, LocalDateTime.now(), orgAccount));

        srcAccount.setBalance(srcAccount.getBalance() - amount);
        destAccount.setBalance(destAccount.getBalance() + amount);


        return new ResponseEntity<>("Transaction created", HttpStatus.ACCEPTED);
    }

}