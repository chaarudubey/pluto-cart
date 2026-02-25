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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/me")
    @Operation(
            summary = "Get User Profile",
            description = """
                Get the profile information of the currently authenticated user.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User Retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
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
    public ResponseEntity<UserResponse> getUser() {
        Object username = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var response = usersService.getUser(username.toString());

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }



    @PatchMapping("/me")
    @Operation(
            summary = "Update User Profile",
            description = """
                Update the profile information of the currently authenticated user.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User Updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
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
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserRequest userRequest) {
        Object username = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var response = usersService.updateUser(username.toString(), userRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/me")
    @Operation(
            summary = "User Profile Deactivated",
            description = """
                Deactivate the current authenticated user's profile.
                This will mark the user as inactive and prevent them from logging in, but their data will remain in the system for record-keeping and potential reactivation.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User Deactivated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
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
    public ResponseEntity<Boolean> deactivateUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var response = usersService.deactivateUser(username);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }



    @GetMapping("/refresh-token")
    @Operation(
            summary = "Refresh JWT Access Token",
            description = """
                When the access token expires, use the refresh token to obtain a new access token without requiring the user to log in again. The refresh token should be sent in the request (e.g., as an HTTP-only cookie or in the request body). 
                The server will validate the refresh token and, if valid, issue a new access token.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Access Token Refreshed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
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
    public ResponseEntity<String> refreshAccessToken() {
        Object username = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var response = usersService.refreshAccessToken(username.toString());

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
    /**
     * TODO: Implement below Endpoints
     * 1. POST /api/users/login - User Login - Done
     * 2. GET /api/users/me - Get User Profile (Authenticated) - Done
     * 3. PUT /api/users/me - Update User Profile (Authenticated)
     * 4. POST /api/users/refresh-token - Refresh JWT Access Token
     * 5. POST /api/users/logout - User Logout (Invalidate Refresh Token)
     */

}
