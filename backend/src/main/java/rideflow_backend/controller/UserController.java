
package rideflow_backend.controller;

import jakarta.validation.Valid;
import rideflow_backend.dto.RegisterRequest;
import rideflow_backend.dto.UpdateProfileRequest;
import rideflow_backend.dto.UserProfileResponse;
import rideflow_backend.entity.User;
import rideflow_backend.repository.UserRepository;
import rideflow_backend.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(
            UserService userService,
            UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getPhone(),
                User.Role.RIDER
        );

        userService.registerUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Rider registered successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return ResponseEntity.ok(new UserProfileResponse(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {

        UserProfileResponse updatedProfile =
                userService.updateProfile(
                        authentication.getName(),
                        request);

        return ResponseEntity.ok(updatedProfile);
    }
}