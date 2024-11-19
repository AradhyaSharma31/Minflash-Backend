package com.flashcard.FlashcardBackend.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OTPDetails {
    private String otp;
    private LocalDateTime expirationTime;

    public Boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }
}
