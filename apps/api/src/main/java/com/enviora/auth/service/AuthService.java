package com.enviora.auth.service;

import com.enviora.auth.dto.LoginRequest;
import com.enviora.auth.dto.LoginResponse;
import com.enviora.auth.dto.RegisterRequest;
import com.enviora.auth.dto.UserResponse;
import com.enviora.auth.entity.EmailVerificationToken;
import com.enviora.auth.repository.EmailVerificationTokenRepository;
import com.enviora.notification.service.EmailService;
import com.enviora.shared.exception.ApiException;
import com.enviora.shared.ratelimit.RateLimiterService;
import com.enviora.shared.security.JwtService;
import com.enviora.user.entity.User;
import com.enviora.user.entity.UserStatus;
import com.enviora.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final RateLimiterService rateLimiterService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.security.email-verification.expiration-minutes:15}")
    private int verificationExpirationMinutes;

    public AuthService(UserRepository userRepository,
                       EmailVerificationTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       EmailService emailService,
                       RateLimiterService rateLimiterService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.rateLimiterService = rateLimiterService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ApiException("An account with this email address already exists", HttpStatus.CONFLICT);
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordHash);
        user.setStatus(UserStatus.PENDING_VERIFICATION);

        User savedUser = userRepository.save(user);

        // Generate high-entropy raw token & SHA-256 hash
        String rawToken = generateRawToken();
        String tokenHash = hashToken(rawToken);
        Instant expiresAt = Instant.now().plus(verificationExpirationMinutes, ChronoUnit.MINUTES);

        EmailVerificationToken verificationToken = new EmailVerificationToken(savedUser, tokenHash, expiresAt);
        tokenRepository.save(verificationToken);

        // Dispatch verification email with raw token
        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getName(), rawToken);

        log.info("Registered new user with id: {} and status: PENDING_VERIFICATION", savedUser.getId());

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getStatus(),
                savedUser.getCreatedAt()
        );
    }

    @Transactional
    public Map<String, String> verifyEmail(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new ApiException("Verification token is required", HttpStatus.BAD_REQUEST);
        }

        String tokenHash = hashToken(rawToken.trim());
        EmailVerificationToken token = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ApiException("Invalid or expired verification token", HttpStatus.BAD_REQUEST));

        User user = token.getUser();

        if (token.isUsed()) {
            if (user.getStatus() == UserStatus.ACTIVE) {
                return Map.of("message", "Email address is already verified. You may sign in.");
            }
            throw new ApiException("Verification token has already been used", HttpStatus.BAD_REQUEST);
        }

        if (token.isExpired()) {
            throw new ApiException("Verification token has expired. Please request a new verification link.", HttpStatus.BAD_REQUEST);
        }

        // Atomically complete token consumption and account activation
        Instant now = Instant.now();
        token.setUsedAt(now);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);
        tokenRepository.save(token);

        log.info("Successfully verified email for user id: {}", user.getId());

        return Map.of("message", "Email address verified successfully. You may now sign in.");
    }

    @Transactional
    public Map<String, String> resendVerification(String email) {
        if (email == null || email.isBlank()) {
            throw new ApiException("Email address is required", HttpStatus.BAD_REQUEST);
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (!rateLimiterService.tryAcquire("resend:" + normalizedEmail)) {
            throw new ApiException("Too many verification requests. Please try again in a few minutes.", HttpStatus.TOO_MANY_REQUESTS);
        }

        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
                // Invalidate existing unused tokens for user
                tokenRepository.invalidateUnusedTokensByUserId(user.getId(), Instant.now());

                // Generate new token & hash
                String rawToken = generateRawToken();
                String tokenHash = hashToken(rawToken);
                Instant expiresAt = Instant.now().plus(verificationExpirationMinutes, ChronoUnit.MINUTES);

                EmailVerificationToken newToken = new EmailVerificationToken(user, tokenHash, expiresAt);
                tokenRepository.save(newToken);

                emailService.sendVerificationEmail(user.getEmail(), user.getName(), rawToken);
                log.info("Resent verification email for user id: {}", user.getId());
            }
        }

        // Generic safe response to prevent email enumeration
        return Map.of("message", "If an unverified account exists for this email address, a new verification link has been sent.");
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            throw new ApiException("Email verification is required before signing in. Please check your email.", HttpStatus.UNAUTHORIZED);
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException("Account is suspended or inactive", HttpStatus.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

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

    private String generateRawToken() {
        byte[] randomBytes = new byte[32]; // 256 bits entropy
        secureRandom.nextBytes(randomBytes);
        return HexFormat.of().formatHex(randomBytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
