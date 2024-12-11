package com.flashcard.FlashcardBackend.Controller;

import com.flashcard.FlashcardBackend.Entity.Storage;
import com.flashcard.FlashcardBackend.Service.AzureBlobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flashcard/blob")
@Slf4j
public class AzureBlobController {

    @Autowired
    private AzureBlobService azureBlobService;

    // Upload Image
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("userId") String userId,
            @RequestParam("deckId") String deckId,
            @RequestParam("cardId") String cardId,
            @RequestParam("file") MultipartFile file) {
        try {
            Storage storage = createStorage(userId, deckId, cardId, file);
            String path = azureBlobService.uploadImage(storage);
            return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded to path: " + path);
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    // Upload Image
    @PostMapping("/uploadImage")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("userId") String userId,
            @RequestParam("file") MultipartFile file) {
        try {
            Storage storage = createProfileStorage(userId, file);
            String path = azureBlobService.uploadProfileImage(storage);
            return ResponseEntity.status(HttpStatus.CREATED).body("Profile uploaded to path: " + path);
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Profile upload failed");
        }
    }

    // Update Image
    @PutMapping("/update")
    public ResponseEntity<String> updateImage(
            @RequestParam("userId") String userId,
            @RequestParam("deckId") String deckId,
            @RequestParam("cardId") String cardId,
            @RequestParam("file") MultipartFile file) {
        try {
            Storage storage = createStorage(userId, deckId, cardId, file);
            String path = azureBlobService.updateImage(storage);
            return ResponseEntity.ok("File updated at path: " + path);
        } catch (Exception e) {
            log.error("Error updating file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File update failed");
        }
    }

    // Update Profile Image
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfileImage(
            @RequestParam("userId") String userId,
            @RequestParam("file") MultipartFile file) {
        try {
            Storage storage = createProfileStorage(userId, file);
            String path = azureBlobService.updateProfileImage(storage);

            int getPosition = path.lastIndexOf("/");
            String fileName = path.substring(getPosition + 1);

            Map<String, String> profileImage = new HashMap<>();
            profileImage.put("file", fileName);
            return ResponseEntity.ok(profileImage);
        } catch (Exception e) {
            log.error("Error updating file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File update failed");
        }
    }

    // Get Image URL
    @GetMapping("/get-url")
    public ResponseEntity<?> getImageUrl(
            @RequestParam("userId") String userId,
            @RequestParam("deckId") String deckId,
            @RequestParam("cardId") String cardId,
            @RequestParam("file") String file) {
        try {
            Storage storage = new Storage(userId, deckId, cardId, file, null);
            String imageUrl = azureBlobService.getImageUrl(storage);
            Map<String, String> url = new HashMap<>();
            url.put("url", imageUrl);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            log.error("Error getting image URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
        }
    }

    // Get Profile Image URL
    @GetMapping("/get-profile-url")
    public ResponseEntity<?> getProfileImageUrl(
            @RequestParam("userId") String userId,
            @RequestParam("file") String file) {
        try {
            Storage storage = new Storage(userId, null, null, file, null);
            String imageUrl = azureBlobService.getProfileImageUrl(storage);
            Map<String, String> url = new HashMap<>();
            url.put("url", imageUrl);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            log.error("Error getting image URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
        }
    }

    // Read Image
    @GetMapping("/read")
    public ResponseEntity<byte[]> readImage(
            @RequestParam("userId") String userId,
            @RequestParam("deckId") String deckId,
            @RequestParam("cardId") String cardId,
            @RequestParam("file") String file) {
        try {
            Storage storage = new Storage(userId, deckId, cardId, file, null);
            byte[] imageData = azureBlobService.readImage(storage);
            return ResponseEntity.ok(imageData);
        } catch (Exception e) {
            log.error("Error reading file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // List Files
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles(
            @RequestParam("userId") String userId,
            @RequestParam("deckId") String deckId,
            @RequestParam("cardId") String cardId) {
        try {
            Storage storage = new Storage(userId, deckId, cardId, null, null);
            List<String> files = azureBlobService.listFiles(storage);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("Error listing files: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Delete Image
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteImage(
            @RequestParam("userId") String userId,
            @RequestParam("deckId") String deckId,
            @RequestParam("cardId") String cardId,
            @RequestParam("fileName") String fileName) {
        try {
            Storage storage = new Storage(userId, deckId, cardId, fileName, null);
            azureBlobService.deleteImage(storage);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File deletion failed");
        }
    }

    // Delete Profile Image
    @DeleteMapping("/delete-profile")
    public ResponseEntity<String> deleteImage(
            @RequestParam("userId") String userId,
            @RequestParam("fileName") String fileName) {
        try {
            Storage storage = new Storage(userId, null, null, fileName, null);
            azureBlobService.deleteProfileImage(storage);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File deletion failed");
        }
    }

    // Helper Method to Create Storage Object
    private Storage createStorage(String userId, String deckId, String cardId, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("File name is invalid");
        }
        InputStream inputStream = file.getInputStream();
        return new Storage(userId, deckId, cardId, fileName, inputStream);
    }

    private Storage createProfileStorage(String userId, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("File name is invalid");
        }
        InputStream inputStream = file.getInputStream();
        return new Storage(userId, null, null, fileName, inputStream);
    }

}
