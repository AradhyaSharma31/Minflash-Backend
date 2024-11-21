package com.flashcard.FlashcardBackend.Controller;

import com.flashcard.FlashcardBackend.DTO.CardDTO;
import com.flashcard.FlashcardBackend.Payload.CardUpdateResponse;
import com.flashcard.FlashcardBackend.Service.CardService;
import com.flashcard.FlashcardBackend.Service.DeckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/flashcard/edit")
@Slf4j
public class CardController {

    @Autowired
    private final CardService cardService;

    @Autowired
    private final DeckService deckService;

    @Autowired
    public CardController(CardService cardService, DeckService deckService) {
        this.cardService = cardService;
        this.deckService = deckService;
    }

    @PostMapping("/createCard/{deckId}")
    public ResponseEntity<CardDTO> createCard(@PathVariable UUID deckId, @Valid @RequestBody CardDTO cardDTO) {
        try {
            CardDTO createdCard = cardService.createCard(deckId, cardDTO.getTerm(), cardDTO.getDefinition(), cardDTO.getImage());
            return ResponseEntity.ok(createdCard);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @PostMapping("/createCardWithImage")
    public ResponseEntity<CardDTO> createCardWithImage(
            @RequestParam UUID deckId,
            @RequestParam String term,
            @RequestParam String definition,
            @RequestParam String image,
            @RequestParam(required = false) MultipartFile file
            ) {
        try {
            CardDTO createdCard = cardService.createCardWithImage(deckId, term, definition, image, file);
            log.info("Image: " + image);
            return ResponseEntity.ok(createdCard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/getCard")
    public ResponseEntity<CardDTO> getCard(
            @RequestParam("deckId") UUID deckId,
            @RequestParam("cardId") UUID cardId
    ) {
        try {
            CardDTO cardDTO = cardService.readCardById(deckId, cardId);
            return ResponseEntity.status(201).body(cardDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/update/{deckId}/{cardId}")
    public ResponseEntity<CardDTO> updateCard(
            @PathVariable UUID deckId,
            @PathVariable UUID cardId,
            @Valid @RequestBody CardDTO cardDTO) {
        try {
            CardDTO updatedCard = cardService.updateCard(deckId, cardId, cardDTO);
            return ResponseEntity.ok(updatedCard);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @DeleteMapping("/delete/{deckId}/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable UUID deckId, @PathVariable UUID cardId) {
        try {
            cardService.deleteCard(cardId, deckId);
            return ResponseEntity.ok("Card successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("{cardId}/updatePerformance")
    public ResponseEntity<CardUpdateResponse> updateCardPerformance(
            @PathVariable UUID cardId,
            @RequestParam("performance") String performance,
            @RequestParam UUID deckId,
            @RequestParam UUID userId) {
        try {
            // Validate if the user can update the card
            boolean isValid = deckService.validateCard(cardId, deckId, userId);
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            // Update the performance of the current card
            CardDTO updatedCard = cardService.updateCardPerformance(cardId, performance);

            // Retrieve the next card after updating
            Optional<CardDTO> nextCardOpt = deckService.getNextCard(deckId, userId);
            CardDTO nextCard = nextCardOpt.orElse(null); // Handle absence as needed

            // Create the response object
            CardUpdateResponse response = new CardUpdateResponse(updatedCard, nextCard);

            // Return the response
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the error message
            System.err.println("Error updating card performance: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
