package com.flashcard.FlashcardBackend.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private UUID id;

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 4, max = 16, message = "Invalid length, Username must contain a minimum of 4 characters and a maximum of 6 characters")
    private String uniqueUsername;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private Date createdAt;

    private String profilePicture;

    private List<DeckDTO> decks;

    private Set<RoleDTO> roles = new HashSet<>();

    @Override
    public String toString() {
        return "UserDTO{" +
                "uniqueUsername='" + uniqueUsername + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' + // Be careful with printing passwords in logs
                '}';
    }

    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }
}
