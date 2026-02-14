package com.plutocart.user_service.controller;

import com.plutocart.user_service.dto.ErrorResponse;
import com.plutocart.user_service.dto.RegistrationRequest;
import com.plutocart.user_service.dto.RegistrationResponse;
import com.plutocart.user_service.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users Management", description = "APIs for user registration, login, and profile management")
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = """
                Register a New User with email, password, full name and phone number.
                
                Password Requirements:
                - Minimum 8 characters
                - At least one uppercase letter
                - At least one lowercase letter
                - At least one number
                - At least one special character (e.g., !@#$%^&*)
                
                Returns User details with JWT access and refresh tokens upon successful registration.
                """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                description = "User registered successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RegistrationResponse.class)
                )
        ),
        @ApiResponse(responseCode = "400",
                description = "Invalid input or email already exists",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
        ),
        @ApiResponse(responseCode = "500",
                description = "Internal Server Error",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
        )
    })
    public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegistrationRequest request)
    {
        log.info("Received registration request for email: {}", request.email());

        var response = usersService.registerUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * TODO: Implement below Endpoints
     * 1. POST /api/users/login - User Login
     * 2. GET /api/users/me - Get User Profile (Authenticated)
     * 3. PUT /api/users/me - Update User Profile (Authenticated)
     * 4. POST /api/users/refresh-token - Refresh JWT Access Token
     * 5. POST /api/users/logout - User Logout (Invalidate Refresh Token)
     */

}
