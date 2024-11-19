package com.flashcard.FlashcardBackend.Enumeration;

public enum CardStatus {
    NEW, // new card (initial status of any card)
    LEARNING, // learning but no learned (when user selects good)
    REVIEWING, // learned now reviewing (when user selects good or easy after learning)
    RELEARNING, // learned but forgot (when user selects hard or again after learning)
    MATURE,       // Cards with intervals of 21 days or more
}


