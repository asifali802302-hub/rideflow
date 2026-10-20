package rideflow_backend.service;

import rideflow_backend.dto.UpdateProfileRequest;
import rideflow_backend.dto.UserProfileResponse;
import rideflow_backend.entity.User;
import rideflow_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        if (user.getPhone() != null &&
                userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("Phone already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRole(User.Role.RIDER);

        return userRepository.save(user);
    }
public User loginUser(String email, String password) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new IllegalArgumentException("Invalid email or password"));

    if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new IllegalArgumentException("Invalid email or password");
    }

    return user;
}
public UserProfileResponse updateProfile(
        String email,
        UpdateProfileRequest request) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new IllegalArgumentException("User not found"));

    user.setName(request.getName());
    user.setPhone(request.getPhone());

    User updatedUser = userRepository.save(user);

    return new UserProfileResponse(updatedUser);
}
}