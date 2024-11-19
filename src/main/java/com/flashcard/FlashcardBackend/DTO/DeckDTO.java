package com.flashcard.FlashcardBackend.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeckDTO {

    private UUID id;  // Add UUID field

    @NotBlank(message = "Title Cannot Be Empty")
    private String title;

    private String description;
    private List<CardDTO> cards;  // Optional, if you want to return the cards as well

}
