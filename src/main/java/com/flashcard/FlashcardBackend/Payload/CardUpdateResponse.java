package com.flashcard.FlashcardBackend.Payload;

import com.flashcard.FlashcardBackend.DTO.CardDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CardUpdateResponse {

    private CardDTO updateCard;
    private CardDTO nextCard;

}
