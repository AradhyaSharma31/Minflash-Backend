package com.flashcard.FlashcardBackend.Service;

import com.flashcard.FlashcardBackend.DTO.CardDTO;
import com.flashcard.FlashcardBackend.DTO.DeckDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeckService {

    public DeckDTO createDeck(UUID userID, String title, String description);
    void deleteDeck(UUID deckId, UUID userId);
    DeckDTO updateDeck(UUID deckId, UUID userId, DeckDTO deckDTO);
    public List<DeckDTO> readAllDecks();
    DeckDTO readDeckById(UUID deckId, UUID userId);
    public Optional<CardDTO> getNextCard(UUID deckId, UUID userId);
    public boolean validateCard(UUID cardId, UUID deckId, UUID userId);
    public boolean resetDeck(UUID deckId, UUID userId);
    public DeckDTO readReviewDeckById(UUID deckId);
}
