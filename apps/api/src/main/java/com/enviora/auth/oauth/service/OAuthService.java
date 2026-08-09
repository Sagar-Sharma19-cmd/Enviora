package com.enviora.auth.oauth.service;

import com.enviora.auth.dto.LoginResponse;
import com.enviora.auth.dto.UserResponse;
import com.enviora.auth.entity.AuthIdentity;
import com.enviora.auth.entity.AuthProvider;
import com.enviora.auth.repository.AuthIdentityRepository;
import com.enviora.shared.exception.ApiException;
import com.enviora.shared.security.JwtService;
import com.enviora.user.entity.User;
import com.enviora.user.entity.UserStatus;
import com.enviora.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OAuthService {

    private static final Logger log = LoggerFactory.getLogger(OAuthService.class);

    private final UserRepository userRepository;
    private final AuthIdentityRepository identityRepository;
    private final JwtService jwtService;

    public OAuthService(UserRepository userRepository,
                        AuthIdentityRepository identityRepository,
                        JwtService jwtService) {
        this.userRepository = userRepository;
        this.identityRepository = identityRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse processGoogleUser(String googleSub, String rawEmail, String name) {
        if (googleSub == null || googleSub.isBlank()) {
            throw new ApiException("Invalid Google identity subject claim", HttpStatus.BAD_REQUEST);
        }
        if (rawEmail == null || rawEmail.isBlank()) {
            throw new ApiException("Google email claim is missing or invalid", HttpStatus.BAD_REQUEST);
        }

        String normalizedEmail = rawEmail.trim().toLowerCase();

        // 1. Search for existing AuthIdentity by (GOOGLE, googleSub)
        Optional<AuthIdentity> identityOpt = identityRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, googleSub);

        User user;

        if (identityOpt.isPresent()) {
            // Case A: Existing Google identity linked to Enviora user -> Sign in
            user = identityOpt.get().getUser();
            log.info("Google OAuth sign-in for existing user id: {}", user.getId());
        } else {
            // Check if an Enviora user with this email already exists
            Optional<User> existingUserOpt = userRepository.findByEmail(normalizedEmail);

            if (existingUserOpt.isPresent()) {
                // Case C: User with email exists, but Google identity is not linked -> DO NOT AUTO-LINK
                log.warn("Google OAuth login attempted for existing password account: {}", normalizedEmail);
                throw new ApiException(
                        "An account with this email address already exists. Please sign in with your password first before connecting Google.",
                        HttpStatus.CONFLICT
                );
            }

            // Case B: Completely new user -> Create User + AuthIdentity
            user = new User();
            user.setName(name != null && !name.isBlank() ? name.trim() : normalizedEmail);
            user.setEmail(normalizedEmail);
            user.setPasswordHash(null); // OAuth-only user
            user.setStatus(UserStatus.ACTIVE); // Google verified OIDC email

            user = userRepository.save(user);

            AuthIdentity identity = new AuthIdentity(user, AuthProvider.GOOGLE, googleSub, normalizedEmail);
            identityRepository.save(identity);

            log.info("Created new user via Google OAuth with id: {}", user.getId());
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException("Account is suspended or inactive", HttpStatus.UNAUTHORIZED);
        }

        // Issue standard Enviora JWT access token
        String token = jwtService.generateToken(user.getEmail());

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getStatus(),
                user.getCreatedAt()
        );

        return new LoginResponse(token, userResponse);
    }
}
