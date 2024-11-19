package com.flashcard.FlashcardBackend.Controller;

import com.flashcard.FlashcardBackend.DTO.DeckDTO;
import com.flashcard.FlashcardBackend.Service.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/flashcard/edit")
@CrossOrigin(origins = "http://localhost:5173")
public class DeckController {

    @Autowired
    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    // Create a new deck for a user
    @PostMapping("/createDeck/{userId}")
    public ResponseEntity<DeckDTO> createDeck(@PathVariable UUID userId, @Valid @RequestBody DeckDTO deckDTO) {
        try {
            DeckDTO createdDeck = deckService.createDeck(userId, deckDTO.getTitle(), deckDTO.getDescription());
            return ResponseEntity.status(201).body(createdDeck);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(400).body(null);
        }
    }

    // Update an existing deck
    @PutMapping("/updateDeck/{userId}/{deckId}")
    public ResponseEntity<DeckDTO> updateDeck(@PathVariable UUID deckId, @PathVariable UUID userId, @Valid @RequestBody DeckDTO deckDTO) {
        try {
            DeckDTO updatedDeck = deckService.updateDeck(deckId, userId, deckDTO);
            return ResponseEntity.status(201).body(updatedDeck);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Delete a deck
    @DeleteMapping("/deleteDeck/{deckId}")
    public ResponseEntity<String> deleteDeck(@PathVariable UUID deckId, @RequestParam UUID userId) {
        try {
            deckService.deleteDeck(deckId, userId);
            return ResponseEntity.ok("Deck Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/getAllDecks")
    public ResponseEntity<List<DeckDTO>> getAllDecks() {
        try {
            List<DeckDTO> deckDTOS = deckService.readAllDecks();
            return ResponseEntity.status(200).body(deckDTOS);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(null);
        }
    }

    @GetMapping("/getDeck/{deckId}")
    public ResponseEntity<DeckDTO> getDeckById(@PathVariable UUID deckId, @RequestParam UUID userId) {
        try {
            DeckDTO deckDTO = deckService.readDeckById(deckId, userId);
            return ResponseEntity.ok(deckDTO);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @GetMapping("/getReviewDeck/{deckId}")
    public ResponseEntity<?> getReviewDeckById(@PathVariable UUID deckId) {
        try {
            DeckDTO deckDTO = deckService.readReviewDeckById(deckId);
            return ResponseEntity.ok(deckDTO);
        } catch(Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/resetDeck/{deckId}")
    public ResponseEntity<String> resetDeck(
            @PathVariable UUID deckId,
            @RequestParam UUID userId) {
        try {
            // Reset the deck
            boolean isResetSuccessful = deckService.resetDeck(deckId, userId);

            if (!isResetSuccessful) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid deck or user.");
            }

            return ResponseEntity.ok("Deck reset successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error resetting deck: " + e.getMessage());
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }
}
