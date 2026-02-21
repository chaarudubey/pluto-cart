package com.plutocart.user_service.service;

import com.plutocart.user_service.dto.LoginRequest;
import com.plutocart.user_service.dto.LoginResponse;
import com.plutocart.user_service.dto.RegistrationRequest;
import com.plutocart.user_service.dto.RegistrationResponse;
import com.plutocart.user_service.exception.InvalidCredentialsException;
import com.plutocart.user_service.exception.UserAlreadyExistsException;
import com.plutocart.user_service.model.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.plutocart.user_service.repository.UsersRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public RegistrationResponse registerUser(RegistrationRequest request) {
        log.info("Registering new user with email: {}", request.email());

        if (usersRepository.existsByEmail(request.email())) {
            log.warn("Registration failed: Email {} already exists", request.email());
            throw new UserAlreadyExistsException("User with Email " + request.email() + " already exists");
        }

        //Need to dig into the password encoder and see how it works,
        // and how to use it to hash the password before saving it to the database
        var hashedPassword = passwordEncoder.encode(request.password());

        var user = Users.builder()
                .email(request.email())
                .passwordHash(hashedPassword)
                .fullName(request.fullName())
                .phoneNumber(request.phoneNumber())
                .isActive(true)
                .userType("CUSTOMER")
                .failedLoginAttempts(0)
                .isDeleted(false)
                .build();

        var savedUser = usersRepository.save(user);
        log.info("User Registered Successfully: ID={}, Email={}", savedUser.getId(), savedUser.getEmail());

        var accessToken = jwtService.generateAccessToken(savedUser.getId(), savedUser.getEmail());
        var refreshToken = jwtService.generateRefreshToken(savedUser.getId(), savedUser.getEmail());

        return new RegistrationResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getPhoneNumber(),
                savedUser.getIsActive(),
                savedUser.getUserType(),
                savedUser.getCreatedAt(),
                accessToken,
                refreshToken
        );
    }

    public LoginResponse loginUser(LoginRequest request) {
        log.info("Attempting login for email: {}", request.username());

        var userOpt = usersRepository.findByEmail(request.username());
        if (userOpt.isEmpty()) {
            log.warn("Login failed: Username {} not found", request.username());
            throw new InvalidCredentialsException("Username or Password Incorrect");
        }

        var user = userOpt.get();
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.info("Login failed: Incorrect password for email {}", request.username());
            throw new InvalidCredentialsException("Username or Password Incorrect");
        }

        var accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        var refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        log.info("Login successful for email: {}", request.username());
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                accessToken,
                refreshToken
        );
    }
}
