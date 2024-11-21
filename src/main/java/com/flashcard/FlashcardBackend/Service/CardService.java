package com.flashcard.FlashcardBackend.Service;

import com.flashcard.FlashcardBackend.DTO.CardDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface CardService {

    public CardDTO createCard(UUID deckId, String term, String definition, String image);
    public CardDTO createCardWithImage(UUID deckId, String term, String definition, String image, MultipartFile file) throws IOException;
    public CardDTO readCardById(UUID deckId, UUID cardId);
    void deleteCard(UUID CardId, UUID DeckId);
    CardDTO updateCard(UUID DeckId, UUID CardId, CardDTO cardDTO);
    public CardDTO updateCardPerformance(UUID cardId, String performance);

}
