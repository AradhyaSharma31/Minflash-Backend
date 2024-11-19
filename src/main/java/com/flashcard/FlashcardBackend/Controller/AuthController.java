package com.flashcard.FlashcardBackend.Controller;

import com.flashcard.FlashcardBackend.Authentication.JWTTokenHelper;
import com.flashcard.FlashcardBackend.DTO.UserDTO;
import com.flashcard.FlashcardBackend.Entity.OTPDetails;
import com.flashcard.FlashcardBackend.Entity.User;
import com.flashcard.FlashcardBackend.Payload.JWTAuthRequest;
import com.flashcard.FlashcardBackend.Payload.JWTAuthResponse;
import com.flashcard.FlashcardBackend.Payload.OTPGenerator;
import com.flashcard.FlashcardBackend.Repository.UserRepo;
import com.flashcard.FlashcardBackend.Service.Implementation.EmailServiceImpl;
import com.flashcard.FlashcardBackend.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Validated
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private JWTTokenHelper jwtTokenHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private UserRepo userRepo;

    private final Map<String, OTPDetails> otpStorage = new HashMap<>();

    private static final String EMAIL_REGEX =
            "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@" +
                    "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";

    @PostMapping("/login")
    public ResponseEntity<?> createToken(
            @RequestBody JWTAuthRequest request
            ) throws Exception {

        try {
            this.authenticate(request.getEmail(), request.getPassword());

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getEmail());

            String token = this.jwtTokenHelper.generateToken(userDetails);

            UserDTO user = this.userService.getUserByEmail(request.getEmail());

            JWTAuthResponse response = new JWTAuthResponse();
            response.setToken(token);
            response.setUserDTO(this.mapper.map((User)userDetails, UserDTO.class));

            return new ResponseEntity<JWTAuthResponse>(response, HttpStatus.OK);

        } catch(RuntimeException e) {
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("error", "login error: " + e.getMessage());
            errorDetails.put("success", false);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("Authorization").substring(7);

        if(jwtTokenHelper.validateRefreshToken(refreshToken)) {
            String username = jwtTokenHelper.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String newAccessToken = jwtTokenHelper.generateToken(userDetails);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);

            return ResponseEntity.ok(tokens);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        }
    }

    private void authenticate(String email, String password) throws Exception {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        try {
            this.authenticationManager.authenticate(authenticationToken);
        } catch(BadCredentialsException e) {
            System.out.println("Invalid Details");
            throw new Exception("Invalid username or password");
        }
    }

    // Register new user API
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        System.out.println("Registering user: " + userDTO);

        List<String> errors = new ArrayList<>();

        // Validate uniqueUsername
        if (userDTO.getUniqueUsername() == null || userDTO.getUniqueUsername().isBlank()) {
            errors.add("Username cannot be empty");
        } else if (userDTO.getUniqueUsername().length() < 4 || userDTO.getUniqueUsername().length() > 16) {
            errors.add("Invalid length, Username must be 4-16 characters");
        } else if (userRepo.existsByUniqueUsername(userDTO.getUniqueUsername())) {
            errors.add("Username is already taken");
        }

        // Validate email
        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank()) {
            errors.add("Email cannot be empty");
        } else if (!isValidEmail(userDTO.getEmail())) {
            errors.add("Please provide a valid email address");
        } else if (userRepo.existsByEmail(userDTO.getEmail())) {
            errors.add("Email is already in use");
        }

        // Validate password
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            errors.add("Password cannot be blank");
        } else if (userDTO.getPassword().length() < 8) {
            errors.add("Password must be at least 8 characters long");
        }

        // Return errors if any
        if (!errors.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errors", errors);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Proceed with OTP generation and sending
        try {
            String otp = OTPGenerator.generateOTP();
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
            otpStorage.put(userDTO.getEmail(), new OTPDetails(otp, expirationTime));
            emailService.sendEmail(userDTO.getEmail(), "Minflash OTP Code", "Your OTP is: " + otp);
            return ResponseEntity.status(HttpStatus.OK).body("OTP sent to email: " + userDTO.getEmail() + ". Please verify to complete registration.");
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error sending OTP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/complete-registration")
    public ResponseEntity<?> completeRegistration(@Valid @RequestBody UserDTO userDTO, @RequestParam String email, @RequestParam String otp) {
        OTPDetails otpDetails = otpStorage.get((email).toLowerCase());

        Map<String, String> errorResponse = new HashMap<>();

        if (otpDetails == null || otpDetails.isExpired()) {
            errorResponse.put("errors", "Invalid or expired OTP!");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (!otpDetails.getOtp().equals(otp)) {
            errorResponse.put("errors", "Incorrect OTP!");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            otpStorage.remove((email).toLowerCase());
            UserDTO registeredUser = this.userService.registerUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (Exception e) {
            errorResponse.put("errors", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Helper method to validate email format
    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
