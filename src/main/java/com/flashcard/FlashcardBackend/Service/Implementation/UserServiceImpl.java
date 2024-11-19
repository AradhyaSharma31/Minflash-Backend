package com.flashcard.FlashcardBackend.Service.Implementation;

import com.flashcard.FlashcardBackend.Configuration.AppConstants;
import com.flashcard.FlashcardBackend.DTO.CardDTO;
import com.flashcard.FlashcardBackend.DTO.DeckDTO;
import com.flashcard.FlashcardBackend.DTO.RoleDTO;
import com.flashcard.FlashcardBackend.DTO.UserDTO;
import com.flashcard.FlashcardBackend.Entity.Role;
import com.flashcard.FlashcardBackend.Entity.User;
import com.flashcard.FlashcardBackend.Repository.RoleRepo;
import com.flashcard.FlashcardBackend.Repository.UserRepo;
import com.flashcard.FlashcardBackend.Service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepo userRepo;

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepo roleRepo;

    public UserServiceImpl(UserRepo userRepo, ModelMapper modelMapper) {
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDTO getUserById(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO registerUser(UserDTO userDTO) {

        try {
            // map the userDTO to user entity
            User user = this.modelMapper.map(userDTO, User.class);

            // encode the user's password
            user.setPassword(this.passwordEncoder.encode(userDTO.getPassword()));

            // assign the normal role to the user
            Role role = this.roleRepo.findById(AppConstants.NORMAL_USER).get();
            user.getRoles().add(role);

            // save the user to the database
            User savedUser = this.userRepo.save(user);

            // return the saved user as a DTO
            return this.modelMapper.map(savedUser, UserDTO.class);
        } catch(DataIntegrityViolationException e) {
            throw new RuntimeException("Invalid Credentials. Please try again with different credentials");
        }

    }

    @Override
    public UserDTO getUserByEmail(String email) {

        User user = this.userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO createUser(String username, String email, String password, String profilePicture) {

        User user = new User();
        user.setUniqueUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setProfilePicture(profilePicture);

        User savedUser = userRepo.save(user);

        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getRoles().clear();

        userRepo.delete(user);
    }

    @Override
    public UserDTO updateUser(UUID userId, UserDTO userDTO) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(userDTO.getUniqueUsername() != null) {
            user.setUniqueUsername(userDTO.getUniqueUsername());
        }
        if(userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if(userDTO.getPassword() != null) {
            user.setPassword(userDTO.getPassword());
        }
        if(userDTO.getProfilePicture() != null) {
            user.setProfilePicture(userDTO.getProfilePicture());
        }

        User updatedUser = userRepo.save(user);

        return modelMapper.map(updatedUser, UserDTO.class);
    }

    @Override
    public List<UserDTO> readAllUsers() {
        List<User> users = userRepo.findAll();

        return users.stream()
                .map(user -> {
                    List<DeckDTO> deckDTOs = user.getDecks().stream()
                            .map(deck -> {
                                // Map the cards associated with the deck
                                List<CardDTO> cardDTOs = deck.getCards().stream()
                                        .map(card -> new CardDTO(
                                                card.getId(),
                                                card.getTerm(),
                                                card.getDefinition(),
                                                card.getImage(),
                                                card.getCreatedAt(),
                                                card.getEaseFactor(),
                                                card.getInterval() != null ? card.getInterval() : 0,
                                                card.getRepetitionCount(),
                                                card.getReviewCount(),
                                                card.getLapses(),
                                                card.getNextReview(),
                                                card.getStatus(),
                                                card.isDue(),
                                                card.getConsecutiveCorrectAnswers(),
                                                card.getLastTimeEasy(),
                                                card.getPerformance(),
                                                card.getQuality()
                                        ))
                                        .collect(Collectors.toList());

                                // Map the deck including the cards
                                return new DeckDTO(
                                        deck.getId(),
                                        deck.getTitle(),
                                        deck.getDescription(),
                                        cardDTOs // Pass the mapped cards here
                                );
                            })
                            .collect(Collectors.toList());

                    // Map the user's roles
                    Set<RoleDTO> roleDTOs = user.getRoles().stream()
                            .map(role -> new RoleDTO(role.getId(), role.getName()))
                            .collect(Collectors.toSet());

                    // Map the user along with their decks and roles
                    return new UserDTO(
                            user.getId(),
                            user.getUniqueUsername(),
                            user.getEmail(),
                            user.getPassword(),
                            user.getCreatedAt(),
                            user.getProfilePicture(),
                            deckDTOs,  // Pass the mapped decks with cards here
                            roleDTOs   // Pass the mapped roles here
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO readUserById(UUID userId) {
        // Fetch the user by their ID
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Map the decks and their associated cards
        List<DeckDTO> deckDTOs = user.getDecks().stream()
                .map(deck -> {
                    List<CardDTO> cardDTOs = deck.getCards().stream()
                            .map(card -> new CardDTO(
                                    card.getId(),
                                    card.getTerm(),
                                    card.getDefinition(),
                                    card.getImage(),
                                    card.getCreatedAt(),
                                    card.getEaseFactor(),
                                    card.getInterval() != null ? card.getInterval() : 0,
                                    card.getRepetitionCount(),
                                    card.getReviewCount(),
                                    card.getLapses(),
                                    card.getNextReview(),
                                    card.getStatus(),
                                    card.isDue(),
                                    card.getConsecutiveCorrectAnswers(),
                                    card.getLastTimeEasy(),
                                    card.getPerformance(),
                                    card.getQuality()
                            ))
                            .collect(Collectors.toList());

                    return new DeckDTO(
                            deck.getId(),
                            deck.getTitle(),
                            deck.getDescription(),
                            cardDTOs // Pass the mapped cards here
                    );
                })
                .collect(Collectors.toList());

        // Map the user's roles
        Set<RoleDTO> roleDTOs = user.getRoles().stream()
                .map(role -> new RoleDTO(role.getId(), role.getName()))
                .collect(Collectors.toSet());

        // Return the mapped UserDTO with decks and roles
        return new UserDTO(
                user.getId(),
                user.getUniqueUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getCreatedAt(),
                user.getProfilePicture(),
                deckDTOs,
                roleDTOs
        );
    }

}