
package rideflow_backend.controller;

import jakarta.validation.Valid;
import rideflow_backend.dto.LoginRequest;
import rideflow_backend.dto.LoginResponse;
import rideflow_backend.entity.User;
import rideflow_backend.security.JwtService;
import rideflow_backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        User user = userService.loginUser(
                request.getEmail(),
                request.getPassword()
        );

        String token = jwtService.generateToken(user.getEmail());

        LoginResponse response = new LoginResponse(
                "Login successful",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                token
        );

        return ResponseEntity.ok(response);
    }
}