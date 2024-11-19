package com.flashcard.FlashcardBackend.DTO;

import com.flashcard.FlashcardBackend.Enumeration.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDTO {

        private UUID id;

        @NotBlank(message = "Term cannot be empty")
        private String term;

        @NotBlank(message = "Definition cannot be empty")
        private String definition;

        private String image;

        private LocalDateTime createdAt;

        // New spaced repetition fields
        private float easeFactor;
        private int interval;
        private int repetitionCount;
        private int reviewCount;
        private int lapses;
        private LocalDateTime nextReview;
        private CardStatus status;
        private boolean isDue;

        // Additional fields
        private int consecutiveCorrectAnswers;
        private LocalDateTime lastTimeEasy;
        private String performance;
        private byte quality;
}
