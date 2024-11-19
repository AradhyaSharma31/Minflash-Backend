package com.flashcard.FlashcardBackend.Payload;

import com.flashcard.FlashcardBackend.DTO.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class JWTAuthResponse {

    private String token;
    private UserDTO userDTO;
}
