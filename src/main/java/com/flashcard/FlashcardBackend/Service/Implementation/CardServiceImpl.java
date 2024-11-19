package com.flashcard.FlashcardBackend.Service.Implementation;

import com.flashcard.FlashcardBackend.DTO.CardDTO;
import com.flashcard.FlashcardBackend.DTO.DeckDTO;
import com.flashcard.FlashcardBackend.Entity.Card;
import com.flashcard.FlashcardBackend.Entity.Deck;
import com.flashcard.FlashcardBackend.Entity.User;
import com.flashcard.FlashcardBackend.Enumeration.CardStatus;
import com.flashcard.FlashcardBackend.Repository.CardRepo;
import com.flashcard.FlashcardBackend.Repository.DeckRepo;
import com.flashcard.FlashcardBackend.Repository.UserRepo;
import com.flashcard.FlashcardBackend.Service.CardService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private final CardRepo cardRepo;
    @Autowired
    private final DeckRepo deckRepo;
    @Autowired
    private final UserRepo userRepo;
    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    public CardServiceImpl(CardRepo cardRepo, DeckRepo deckRepo, UserRepo userRepo, ModelMapper modelMapper) {
        this.cardRepo = cardRepo;
        this.deckRepo = deckRepo;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public CardDTO createCard(UUID deckId, String term, String definition, String image) {
        Deck deck = deckRepo.findById(deckId).orElseThrow(() -> new RuntimeException("Deck not found"));

        Card card = new Card();
        card.setDeck(deck);
        card.setTerm(term);
        card.setDefinition(definition);
        card.setImage(image);
        card.setCreatedAt(card.getCreatedAt());

        // Default spaced repetition values
        card.setEaseFactor(2.5f);
        card.setInterval(0);
        card.setRepetitionCount(0);
        card.setReviewCount(0);
        card.setLapses(0);
        card.setNextReview(LocalDateTime.now());
        card.setStatus(CardStatus.NEW);
        card.setDue(false);

        // Additional fields
        card.setConsecutiveCorrectAnswers(0);
        card.setLastTimeEasy(null);

        Card savedCard = cardRepo.save(card);
        return modelMapper.map(savedCard, CardDTO.class);
    }

    @Override
    public CardDTO readCardById(UUID deckId, UUID cardId) {

        Deck deck = deckRepo.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck doesn't exist or doesn't belong to this user"));

        Card card = cardRepo.findById(cardId)
                .filter(c -> c.getDeck().getId().equals(deck.getId()))
                .orElseThrow(() -> new RuntimeException("Card doesn't exist or doesn't belong to this deck"));

        CardDTO cardDTO = new CardDTO(
                        card.getId(),
                        card.getTerm(),
                        card.getDefinition(),
                        card.getImage(),
                        card.getCreatedAt(),
                        card.getEaseFactor(),
                        card.getInterval() != null ? card.getInterval() : 0,
                        card.getRepetitionCount(),
                        card.getReviewCount(),
                        card.getLapses(),
                        card.getNextReview(),
                        card.getStatus(),
                        card.isDue(),
                        card.getConsecutiveCorrectAnswers(),
                        card.getLastTimeEasy(),
                        card.getPerformance(),
                        card.getQuality()
                );

        return cardDTO;
    }

    @Override
    public void deleteCard(UUID cardId, UUID deckId) {
        Deck deck = deckRepo.findById(deckId).orElseThrow(() -> new RuntimeException("Deck not found"));

        Card card = cardRepo.findById(cardId)
                .filter(c -> c.getDeck().getId().equals(deck.getId()))
                .orElseThrow(() -> new RuntimeException("Card doesn't exist inside the deck"));

        cardRepo.delete(card);
    }

    @Override
    public CardDTO updateCard(UUID deckId, UUID cardId, CardDTO cardDTO) {
        Deck deck = deckRepo.findById(deckId).orElseThrow(() -> new RuntimeException("Deck not found"));

        Card card = cardRepo.findById(cardId)
                .filter(c -> c.getDeck().getId().equals(deck.getId()))
                .orElseThrow(() -> new RuntimeException("Card doesn't exist inside the deck"));

        if (cardDTO.getTerm() != null) {
            card.setTerm(cardDTO.getTerm());
        }
        if (cardDTO.getDefinition() != null) {
            card.setDefinition(cardDTO.getDefinition());
        }
        if (cardDTO.getImage() != null) {
            card.setImage(cardDTO.getImage());
        }

        Card savedCard = cardRepo.save(card);
        return modelMapper.map(savedCard, CardDTO.class);
    }

    @Transactional
    @Override
    public CardDTO updateCardPerformance(UUID cardId, String performance) {
        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        // Update card based on performance (KNOW_IT, KNOW_BUT_WILL_FORGET, DONT_KNOW)
        card.updateCard(performance);

        // Save the updated card back to the repository
        Card updatedCard = cardRepo.save(card);

        // Convert entity to DTO (using modelMapper or manually)
        CardDTO updatedCardDTO = modelMapper.map(updatedCard, CardDTO.class);
        return updatedCardDTO;
    }
}
