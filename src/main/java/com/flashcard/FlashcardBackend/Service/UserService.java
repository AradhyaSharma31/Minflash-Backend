package com.flashcard.FlashcardBackend.Service;

import com.flashcard.FlashcardBackend.DTO.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {

    public UserDTO getUserById(UUID userId);
    public UserDTO registerUser(UserDTO userDTO);
    public UserDTO createUser(String username, String email, String password, String profilePicture);
    public void deleteUser(UUID UserId);
    public UserDTO updateUser(UUID userId, UserDTO userDTO);
    public List<UserDTO> readAllUsers();
    public UserDTO readUserById(UUID userId);
    public UserDTO getUserByEmail(String email);
}
