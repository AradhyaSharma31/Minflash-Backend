package com.flashcard.FlashcardBackend.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Storage {
    private String UserId;
    private String DeckId;
    private String CardId;
    private String fileName;
    private InputStream inputStream;
}
