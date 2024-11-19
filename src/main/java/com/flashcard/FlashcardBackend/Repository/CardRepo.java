package com.flashcard.FlashcardBackend.Repository;

import com.flashcard.FlashcardBackend.Entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CardRepo extends JpaRepository<Card, UUID> {

    List<Card> findByDeckId(UUID deckId);

//    @Query("SELECT c FROM Card c WHERE c.deck.id = :deckId ORDER BY c.createdAt ASC")
//    List<Card> findAllByDeckIdOrderByCreatedAt(@Param("deckId") UUID deckId);

    List<Card> findAllByDeckIdOrderByCreatedAtAsc(UUID deckId);

}
