package com.flashcard.FlashcardBackend.Controller;

import com.flashcard.FlashcardBackend.DTO.DeckDTO;
import com.flashcard.FlashcardBackend.DTO.PasswordRequest;
import com.flashcard.FlashcardBackend.DTO.UserDTO;
import com.flashcard.FlashcardBackend.Entity.User;
import com.flashcard.FlashcardBackend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/flashcard/user")
public class UserController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // Create a new user
    @PostMapping("/createUser")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.createUser(userDTO.getUniqueUsername(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getProfilePicture());
            return ResponseEntity.status(201).body(createdUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(null);
        }
    }

    // Update an existing user
    @PutMapping("/updateUser/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID userId, @Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(userId, userDTO);
            return ResponseEntity.status(200).body(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Delete a user
//    @PreAuthorize("hasRole('ADMIN')") // Only admin is allowed to delete users now
    @PostMapping("/deleteUser/{userId}")
    public ResponseEntity<?> deleteAccount(@RequestBody PasswordRequest passwordRequest, @PathVariable UUID userId) {
        UserDTO user = userService.getUserById(userId);
        String storedPassword = user.getPassword();

        System.out.println("Stored Password (Hashed): " + storedPassword);
        System.out.println("Entered Password (Raw): " + passwordRequest.getPassword());

        // Use password encoder's matches method
        if (passwordEncoder.matches(passwordRequest.getPassword(), storedPassword)) {
            try {
                userService.deleteUser(userId);
                return ResponseEntity.ok("User Deleted Successfully");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(400).body("Error: " + e.getMessage());
            }
        }
        return ResponseEntity.status(400).body("Invalid password");
    }

    // Read all users
    @GetMapping("/readAllUsers")
    public ResponseEntity<List<UserDTO>> readAllUsers() {
        try {
            List<UserDTO> users = userService.readAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            // Log the error and send an appropriate response
            return ResponseEntity.status(400).body(null);
        }
    }

    @GetMapping("/readUser/{userId}")
    public ResponseEntity<UserDTO> readUserById(@PathVariable UUID userId) {
        try {
            UserDTO userDTO = userService.readUserById(userId);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            // Log the error and send an appropriate response
            return ResponseEntity.status(400).body(null);
        }
    }

    // Exception handler for runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }
}
