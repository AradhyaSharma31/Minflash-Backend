package com.flashcard.FlashcardBackend.Payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWTAuthRequest {

    private String email;
    private String password;

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }
}
