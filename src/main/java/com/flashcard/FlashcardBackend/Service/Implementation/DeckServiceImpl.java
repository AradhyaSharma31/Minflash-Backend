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
import com.flashcard.FlashcardBackend.Service.DeckService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeckServiceImpl implements DeckService {

    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private final DeckRepo deckRepo;
    @Autowired
    private final UserRepo userRepo;
    @Autowired
    private final CardRepo cardRepo;

    public DeckServiceImpl(ModelMapper modelMapper, DeckRepo deckRepo, UserRepo userRepo, CardRepo cardRepo) {
        this.modelMapper = modelMapper;
        this.deckRepo = deckRepo;
        this.userRepo = userRepo;
        this.cardRepo = cardRepo;
    }


    @Override
    public DeckDTO createDeck(UUID userId, String title, String description) {
        // Fetch the user using their ID
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create a new Deck entity and associate it with the user
        Deck deck = new Deck();
        deck.setTitle(title);
        deck.setDescription(description);
        deck.setUser(user);  // Set the deck's owner

        Deck savedDeck = deckRepo.save(deck);

        return modelMapper.map(savedDeck, DeckDTO.class);
    }

    @Override
    public void deleteDeck(UUID deckId, UUID userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the deck by its ID and check if it belongs to the user
        Deck deck = deckRepo.findById(deckId)
                .filter(d -> d.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Deck doesn't exist or doesn't belong to this user"));

        deckRepo.delete(deck);
    }

    @Override
    public DeckDTO updateDeck(UUID deckId, UUID userId, DeckDTO deckDTO) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the deck and ensure it belongs to the user
        Deck deck = deckRepo.findById(deckId)
                .filter(d -> d.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Deck doesn't exist or doesn't belong to this user"));

        // Update the deck's fields
        if (deckDTO.getTitle() != null) {
            deck.setTitle(deckDTO.getTitle());
        }
        if (deckDTO.getDescription() != null) {
            deck.setDescription(deckDTO.getDescription());
        }

        Deck savedDeck = deckRepo.save(deck);

        return modelMapper.map(savedDeck, DeckDTO.class);
    }

    @Override
    public List<DeckDTO> readAllDecks() {
        List<Deck> decks = deckRepo.findAll();

        return decks.stream()
                .map(deck -> {
                  List<CardDTO> cardDTOs = deck.getCards().stream()
                   // List<CardDTO> cardDTOs = deck.getCards() != null ? deck.getCards().stream()
                          .map(card -> new CardDTO(
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
                          ))
                            .toList();


                    return new DeckDTO(deck.getId(), deck.getTitle(), deck.getDescription(), cardDTOs);
                })
                .collect(Collectors.toList());
    }

    @Override
    public DeckDTO readDeckById(UUID deckId, UUID userId) {
        // Fetch the user using their ID
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the deck by its ID and ensure it belongs to the user
        Deck deck = deckRepo.findById(deckId)
                .filter(d -> d.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Deck doesn't exist or doesn't belong to this user"));

        // Map the deck and its associated cards to DeckDTO
        List<CardDTO> cardDTOs = deck.getCards().stream()
                .map(card -> new CardDTO(
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
                ))
                .collect(Collectors.toList());

        // Return the mapped DeckDTO
        return new DeckDTO(deck.getId(), deck.getTitle(), deck.getDescription(), cardDTOs);
    }

    @Override
    public DeckDTO readReviewDeckById(UUID deckId) {

        // Fetch the deck by its ID and ensure it belongs to the user
        Deck deck = deckRepo.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck doesn't exist"));

        // Map the deck and its associated cards to DeckDTO
        List<CardDTO> cardDTOs = deck.getCards().stream()
                .map(card -> new CardDTO(
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
                ))
                .collect(Collectors.toList());

        // Return the mapped DeckDTO
        return new DeckDTO(deck.getId(), deck.getTitle(), deck.getDescription(), cardDTOs);
    }

    @Override
    public Optional<CardDTO> getNextCard(UUID deckId, UUID userId) {
        Optional<Deck> deckOpt = deckRepo.findById(deckId);

        if (deckOpt.isPresent()) {
            Deck deck = deckOpt.get();

            // Ensure the user owns this deck
            if (!deck.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("User does not own this deck");
            }

            // Get the next card from the deck's priority queue
            Card nextCard = deck.getNextCard(); // Assuming this returns a Card
            return Optional.ofNullable(modelMapper.map(nextCard, CardDTO.class)); // Using ModelMapper
        }

        return Optional.empty();
    }

    @Override
    public boolean validateCard(UUID cardId, UUID deckId, UUID userId) {
        Optional<Card> cardOpt = cardRepo.findById(cardId);

        if (cardOpt.isPresent()) {
            Card card = cardOpt.get();
            Deck deck = card.getDeck();

            // Ensure the card belongs to the deck and the deck belongs to the user
            return deck.getId().equals(deckId) && deck.getUser().getId().equals(userId);
        }

        return false;
    }

    private boolean validateUserDeck(UUID deckId, UUID userId) {
        Optional<Deck> deck = this.deckRepo.findById(deckId);
        return deck.isPresent() && deck.get().getUser().getId().equals(userId);
    }

    private List<Card> getAllCardsByDeckId(UUID deckId) {
        List<Card> newCards = cardRepo.findByDeckId(deckId);

        return newCards;
    }

    @Override
    public boolean resetDeck(UUID deckId, UUID userId) {

        boolean isValid = validateUserDeck(deckId, userId);
        if(!isValid) {
            return false;
        }

        List<Card> cards = cardRepo.findAllByDeckIdOrderByCreatedAtAsc(deckId);

        for(Card card : cards) {
            card.setEaseFactor(2.5f);
            card.setInterval(0);
            card.setRepetitionCount(0);
            card.setReviewCount(0);
            card.setLapses(0);
            card.setNextReview(LocalDateTime.now());
            card.setStatus(CardStatus.NEW);
            card.setDue(false);
            card.setConsecutiveCorrectAnswers(0);
            card.setLastTimeEasy(null);

            this.cardRepo.save(card);
        }

        return true;
    }
}
