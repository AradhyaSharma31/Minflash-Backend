package com.flashcard.FlashcardBackend.Repository;

import com.flashcard.FlashcardBackend.Entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeckRepo extends JpaRepository<Deck, UUID> {

}
