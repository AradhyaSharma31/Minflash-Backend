package com.flashcard.FlashcardBackend.Service.Implementation;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobItem;
import com.flashcard.FlashcardBackend.Entity.Card;
import com.flashcard.FlashcardBackend.Entity.Deck;
import com.flashcard.FlashcardBackend.Entity.Storage;
import com.flashcard.FlashcardBackend.Entity.User;
import com.flashcard.FlashcardBackend.Repository.CardRepo;
import com.flashcard.FlashcardBackend.Repository.DeckRepo;
import com.flashcard.FlashcardBackend.Repository.UserRepo;
import com.flashcard.FlashcardBackend.Service.AzureBlobService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AzureBlobServiceImpl implements AzureBlobService {

    @Autowired
    private BlobServiceClient blobServiceClient;

    @Value("${azure.blob.container-name}")
    private String containerName;

    @Autowired
    private BlobContainerClient blobContainerClient;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DeckRepo deckRepo;

    @Autowired
    private CardRepo cardRepo;

    @Override
    public String uploadImage(Storage storage) {

        UUID userId = UUID.fromString(storage.getUserId());
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));

        UUID deckId = UUID.fromString(storage.getDeckId());
        Deck deck = deckRepo.findById(deckId)
                .filter(d -> d.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Deck Not Associated With User"));

        UUID cardId = UUID.fromString(storage.getCardId());
        Card card = cardRepo.findById(cardId)
                .filter(c -> c.getDeck().getId().equals(deck.getId()))
                .orElseThrow(() -> new RuntimeException("Card Not Associated With Deck"));

        // setting the card's image
        card.setImage(storage.getFileName());
        cardRepo.save(card);
        log.info("Image has been added to the card");

        String path = getPath(storage);
        BlobClient blob = blobContainerClient.getBlobClient(path);
        blob.upload(storage.getInputStream(), false);

        return path;
    }

    @Override
    public String uploadProfileImage(Storage storage) {

        UUID userId = UUID.fromString(storage.getUserId());
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));

        String path = getProfilePath(storage);
        BlobClient blob = blobContainerClient.getBlobClient(path);
        blob.upload(storage.getInputStream(), false);

        log.info("Image has been added to the card");
        return path;
    }

    @Override
    public String updateImage(Storage storage) {

        UUID userId = UUID.fromString(storage.getUserId());
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));

        UUID deckId = UUID.fromString(storage.getDeckId());
        Deck deck = deckRepo.findById(deckId)
                .filter(d -> d.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Deck Not Associated With User"));

        UUID cardId = UUID.fromString(storage.getCardId());
        Card card = cardRepo.findById(cardId)
                .filter(c -> c.getDeck().getId().equals(deck.getId()))
                .orElseThrow(() -> new RuntimeException("Card Not Associated With Deck"));

        // setting the card's image
        card.setImage(storage.getFileName());
        cardRepo.save(card);
        log.info("Image has been added to the card");

        String path = getPath(storage);
        BlobClient blob = blobContainerClient.getBlobClient(path);
        blob.upload(storage.getInputStream(), true);
        return path;
    }

    @Override
    public String updateProfileImage(Storage storage) {
        UUID userId = UUID.fromString(storage.getUserId());
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));

        user.setProfilePicture(storage.getFileName());
        userRepo.save(user);

        String path = getProfilePath(storage);
        BlobClient blob = blobContainerClient.getBlobClient(path);
        blob.upload(storage.getInputStream(), true);
        return path;
    }

    @Override
    public String getImageUrl(Storage storage) {
        String path = getPath(storage);
        BlobClient blobClient = blobContainerClient.getBlobClient(path);
        BlobHttpHeaders headers = new BlobHttpHeaders();
        String fileName = storage.getFileName();
        int dotIndex = fileName.lastIndexOf(".");
        String fileExtension = "";

        if (dotIndex > 0) {
            fileExtension = fileName.substring(dotIndex + 1).toLowerCase();
            System.out.println("File extension: " + fileExtension);
        } else {
            System.out.println("No extension found.");
        }

        // Set Content-Type based on file extension
        switch (fileExtension) {
            case "png":
                headers.setContentType("image/png");
                break;
            case "jpeg":
            case "jpg":
                headers.setContentType("image/jpeg");
                break;
            case "gif":
                headers.setContentType("image/gif");
                break;
            case "bmp":
                headers.setContentType("image/bmp");
                break;
            case "tiff":
                headers.setContentType("image/tiff");
                break;
            case "svg":
                headers.setContentType("image/svg+xml");
                break;
            case "webp":
                headers.setContentType("image/webp");
                break;
            case "ico":
                headers.setContentType("image/x-icon");
                break;
            default:
                throw new RuntimeException("Unsupported file type: " + fileExtension);
        }

        blobClient.setHttpHeaders(headers);

        // Check if blob exists, then get its URL
        if (blobClient.exists()) {
            return blobClient.getBlobUrl();
        } else {
            throw new RuntimeException("Image not found in Azure Blob Storage");
        }
    }

    @Override
    public String getProfileImageUrl(Storage storage) {
        String path = getProfilePath(storage);
        BlobClient blobClient = blobContainerClient.getBlobClient(path);
        BlobHttpHeaders headers = new BlobHttpHeaders();
        String fileName = storage.getFileName();
        int dotIndex = fileName.lastIndexOf(".");
        String fileExtension = "";

        if (dotIndex > 0) {
            fileExtension = fileName.substring(dotIndex + 1).toLowerCase();
            System.out.println("File extension: " + fileExtension);
        } else {
            System.out.println("No extension found.");
        }

        // Set Content-Type based on file extension
        switch (fileExtension) {
            case "png":
                headers.setContentType("image/png");
                break;
            case "jpeg":
            case "jpg":
                headers.setContentType("image/jpeg");
                break;
            case "gif":
                headers.setContentType("image/gif");
                break;
            case "bmp":
                headers.setContentType("image/bmp");
                break;
            case "tiff":
                headers.setContentType("image/tiff");
                break;
            case "svg":
                headers.setContentType("image/svg+xml");
                break;
            case "webp":
                headers.setContentType("image/webp");
                break;
            case "ico":
                headers.setContentType("image/x-icon");
                break;
            default:
                throw new RuntimeException("Unsupported file type: " + fileExtension);
        }

        blobClient.setHttpHeaders(headers);

        if (blobClient.exists()) {
            return blobClient.getBlobUrl();
        } else {
            throw new RuntimeException("Image not found in Azure Blob Storage");
        }
    }

    @Override
    public byte[] readImage(Storage storage) {
        String path = getPath(storage);
        BlobClient blobClient = blobContainerClient.getBlobClient(path);
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        blobClient.downloadStream(o);
        final byte[] bytes = o.toByteArray();
        return bytes;
    }

    @Override
    public List<String> listFiles(Storage storage) {
        PagedIterable<BlobItem> blobItems = blobContainerClient.listBlobsByHierarchy(storage.getUserId() + "/" + storage.getDeckId() + "/" + storage.getCardId() + "/");

        List<String> blobList = new ArrayList<>();

        for(BlobItem blob : blobItems) {
            blobList.add(blob.getName());
        }

        return blobList;
    }

    @Override
    public void deleteImage(Storage storage) {

        UUID userId = UUID.fromString(storage.getUserId());
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));

        UUID deckId = UUID.fromString(storage.getDeckId());
        Deck deck = deckRepo.findById(deckId)
                .filter(d -> d.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Deck Not Associated With User"));

        UUID cardId = UUID.fromString(storage.getCardId());
        Card card = cardRepo.findById(cardId)
                .filter(c -> c.getDeck().getId().equals(deck.getId()))
                .orElseThrow(() -> new RuntimeException("Card Not Associated With Deck"));

        // setting the card's image
        card.setImage(null);
        cardRepo.save(card);
        log.info("Image has been added to the card");

        String path = getPath(storage);
        BlobClient client = blobContainerClient.getBlobClient(path);
        client.delete();
        log.info("Blob is deleted successfully");
    }

    @Override
    public void deleteProfileImage(Storage storage) {
        String path = getProfilePath(storage);
        BlobClient client = blobContainerClient.getBlobClient(path);
        client.delete();
        log.info("Blob is deleted successfully");
    }

    private String getPath(Storage storage) {
        if (StringUtils.isBlank(storage.getUserId()) ||
                StringUtils.isBlank(storage.getDeckId()) ||
                StringUtils.isBlank(storage.getCardId()) ||
                StringUtils.isBlank(storage.getFileName())) {
            throw new RuntimeException("Invalid path parameters");
        }

        // Validate entities (user, deck, card)
        UUID userId = UUID.fromString(storage.getUserId());
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));

        UUID deckId = UUID.fromString(storage.getDeckId());
        Deck deck = deckRepo.findById(deckId)
                .filter(d -> d.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Deck Not Associated With User"));

        UUID cardId = UUID.fromString(storage.getCardId());
        Card card = cardRepo.findById(cardId)
                .filter(c -> c.getDeck().getId().equals(deck.getId()))
                .orElseThrow(() -> new RuntimeException("Card Not Associated With Deck"));

        return storage.getUserId() + "/" + storage.getDeckId() + "/" + storage.getCardId() + "/" + storage.getFileName();
    }

    private String getProfilePath(Storage storage) {
        if (StringUtils.isBlank(storage.getUserId()) ||
                StringUtils.isBlank(storage.getFileName())) {
            throw new RuntimeException("Invalid path parameters");
        }

        // Validate entities (user, deck, card)
        UUID userId = UUID.fromString(storage.getUserId());
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));

        return storage.getUserId() + "/" + storage.getFileName();
    }
}
