package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class LoanController {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/loans")
    public List<LoanDTO> getLoans() {
        return loanRepository.findAll().stream().map(LoanDTO::new).collect(toList());
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> createLoans(Authentication authentication, @RequestBody LoanApplicationDTO loan) {

        Client client = clientRepository.findByEmail(authentication.getName());
        Optional<Loan> requestLoan = loanRepository.findById(loan.getLoanId());
        Account account = this.accountRepository.findByNumber(loan.getToAccountNumber());

        //Verify that the data is correct, that is, they are not empty, that the amount is not 0 or that the installments are not 0.
        if (loan.getAmount() <= 0 || loan.getPayments() <= 0) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        //Verify that the loan exists
        if (!requestLoan.isPresent()) {
            return new ResponseEntity<>("Loan does not exist", HttpStatus.FORBIDDEN);
        }
        //Verify that the number of installments is among those available for the loan
        if (!requestLoan.get().getPayments().contains(loan.getPayments())) {
            return new ResponseEntity<>("Invalid Payment Amount", HttpStatus.FORBIDDEN);
        }
        //Verify that the requested amount does not exceed the maximum loan amount
        if (loan.getAmount() > requestLoan.get().getMaxAmount()) {
            return new ResponseEntity<>("You must choose a maximum amount of: " + requestLoan.get().getMaxAmount(), HttpStatus.FORBIDDEN);
        }
        //Verify that the destination account belongs to the authenticated client
        if (!client.getAccounts().contains(account)) {
            return new ResponseEntity<>("The destination account does not exist", HttpStatus.FORBIDDEN);
        }

        ClientLoan clientLoanRequest = clientLoanRepository.save(new ClientLoan(loan.getAmount(), loan.getPayments(), client, requestLoan.get()));
        Transaction creditTransaction = transactionRepository.save(new Transaction(TransactionType.CREDIT, loan.getAmount(), "loan approved " + requestLoan.get().getName(), LocalDateTime.now(), account));
        transactionRepository.save(creditTransaction);
        account.setBalance(account.getBalance() + loan.getAmount());

        return new ResponseEntity<>("Loan transaction created", HttpStatus.ACCEPTED);


    }


}
