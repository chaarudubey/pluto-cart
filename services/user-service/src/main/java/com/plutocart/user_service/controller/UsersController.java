package com.plutocart.user_service.controller;

import com.plutocart.user_service.dto.*;
import com.plutocart.user_service.exception.InvalidCredentialsException;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

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

    @PostMapping("/login")
    @Operation(
            summary = "Login a user",
            description = """
                Login with Username and Password.
                Your username is your email address.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User Login successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized, Invalid username or password",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InvalidCredentialsException.class)
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
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        var response = usersService.loginUser(request);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", response.refreshToken())
                .httpOnly(false)
                .secure(false) // true in production (HTTPS only)
                //.path("/api/auth/refresh") // restrict usage
                .maxAge(Duration.ofDays(7))
                .sameSite("None") // or "Lax"
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
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
