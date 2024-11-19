package com.flashcard.FlashcardBackend.Service;

import com.flashcard.FlashcardBackend.DTO.CardDTO;

import java.util.UUID;

public interface CardService {

    public CardDTO createCard(UUID deckId, String term, String definition, String image);
    public CardDTO readCardById(UUID deckId, UUID cardId);
    void deleteCard(UUID CardId, UUID DeckId);
    CardDTO updateCard(UUID DeckId, UUID CardId, CardDTO cardDTO);
    public CardDTO updateCardPerformance(UUID cardId, String performance);

}
