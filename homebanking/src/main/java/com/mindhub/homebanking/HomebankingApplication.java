package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class HomebankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomebankingApplication.class, args);
    }


    @Bean
    public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository) {
        return (args) -> {
            //clientRepository.save(new Client("Melba","Morel","melba@mindhub.com"));
            Client cliente1 = new Client("Melba", "Morel", "melba@mindhub.com");
            Client cliente2 = new Client("Juan", "Morel", "juan@mindhub.com");
            clientRepository.save(cliente1);
            clientRepository.save(cliente2);

            Account account1 = new Account("VIN001", LocalDateTime.now(), 5000, cliente1);
            Account account2 = new Account("VIN002", LocalDateTime.now().plusDays(1), 7500, cliente1);
            accountRepository.save(account1);
            accountRepository.save(account2);
            Account account3 = new Account("VIN003", LocalDateTime.now(), 5000, cliente2);
            Account account4 = new Account("VIN004", LocalDateTime.now().plusDays(1), 7500, cliente2);
            accountRepository.save(account2);
            accountRepository.save(account2);
            accountRepository.save(account3);
            accountRepository.save(account4);


            Transaction transaction1 = new Transaction(TransactionType.CREDIT, 800, "Compra de alimentos", LocalDateTime.now(), account1);
            Transaction transaction2 = new Transaction(TransactionType.DEBIT, -10000, "Compra de insumos", LocalDateTime.now(), account1);
            Transaction transaction3 = new Transaction(TransactionType.CREDIT, 4500, "Compra de gas", LocalDateTime.now(), account1);
            Transaction transaction4 = new Transaction(TransactionType.CREDIT, -8000, "Compra de alimentos", LocalDateTime.now(), account2);
            Transaction transaction5 = new Transaction(TransactionType.DEBIT, 77450, "Compra de materiales de aseo", LocalDateTime.now(), account2);
            Transaction transaction6 = new Transaction(TransactionType.CREDIT, -15000, "Compra de repuestos", LocalDateTime.now(), account2);
            Transaction transaction7 = new Transaction(TransactionType.DEBIT, 13450, "Compra de componentes", LocalDateTime.now(), account1);
            Transaction transaction8 = new Transaction(TransactionType.CREDIT, 10000, "Compra de materiales de contruccion", LocalDateTime.now(), account4);
            Transaction transaction9 = new Transaction(TransactionType.DEBIT, 15000, "Compra de alimentos", LocalDateTime.now(), account1);
            Transaction transaction10 = new Transaction(TransactionType.CREDIT, 23000, "Compra de alcohol", LocalDateTime.now(), account4);
            transactionRepository.save(transaction1);
            transactionRepository.save(transaction2);
            transactionRepository.save(transaction3);
            transactionRepository.save(transaction4);
            transactionRepository.save(transaction5);
            transactionRepository.save(transaction6);
            transactionRepository.save(transaction7);
            transactionRepository.save(transaction8);
            transactionRepository.save(transaction9);
            transactionRepository.save(transaction10);

            Loan loan1 = new Loan("Hipotecario", 500000, Arrays.asList(12, 24, 36, 48, 60));

            Loan loan2 = new Loan("Personal", 750000, Arrays.asList(6, 12, 24));

            Loan loan3 = new Loan("Automotriz", 1000000, Arrays.asList(12, 24, 36));
            loanRepository.save(loan1);
            loanRepository.save(loan2);
            loanRepository.save(loan3);

			ClientLoan clientLoan1 = new ClientLoan(400000,60,cliente1,loan1);
			ClientLoan clientLoan2 = new ClientLoan(50000,12,cliente1,loan2);
			ClientLoan clientLoan3 = new ClientLoan(100000,24,cliente2,loan1);
            ClientLoan clientLoan4 = new ClientLoan(200000,36,cliente2,loan3);
			clientLoanRepository.save(clientLoan1);
			clientLoanRepository.save(clientLoan2);
			clientLoanRepository.save(clientLoan3);
            clientLoanRepository.save(clientLoan4);

        };
    }
}
