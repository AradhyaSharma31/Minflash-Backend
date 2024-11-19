package com.flashcard.FlashcardBackend.Repository;

import com.flashcard.FlashcardBackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUniqueUsername(String uniqueUsername);
}
