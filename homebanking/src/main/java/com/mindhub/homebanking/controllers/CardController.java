package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CardRepository cardRepository;


    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> createCard(Authentication authentication, @RequestParam CardType cardType, @RequestParam CardColor cardColor) {
        Client client = this.clientRepository.findByEmail(authentication.getName());
        if (client.getCards().stream().filter(card -> card.getType() == cardType).count() >= 3) {
            return new ResponseEntity<>("Client´s of cards limit reached", HttpStatus.FORBIDDEN);
        } else {
            String cardNumber = (int) ((Math.random() * (9999 - 1000)) + 1000) + "-" + (int) ((Math.random() * (9999 - 1000)) + 1000) + "-" + (int) ((Math.random() * (9999 - 1000)) + 1000) + "-" + (int) ((Math.random() * (9999 - 1000)) + 1000);
            int cvv = (int) ((Math.random() * (999 - 100)) + 100);
            cardRepository.save(new Card(client.getFirstName() + " " + client.getLastName(), cardType, cardColor, cardNumber, cvv, LocalDateTime.now(), LocalDateTime.now().plusYears(5), client));
            return new ResponseEntity<>(HttpStatus.CREATED);


        }


    }
}
