package com.flashcard.FlashcardBackend.Service;

import com.flashcard.FlashcardBackend.DTO.CardDTO;
import com.flashcard.FlashcardBackend.Entity.Storage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AzureBlobService {

    public String uploadImage(Storage storage);

    public String updateImage(Storage storage);

    public byte[] readImage(Storage storage);

    public List<String> listFiles(Storage storage);

    public void deleteImage(Storage storage);
}
