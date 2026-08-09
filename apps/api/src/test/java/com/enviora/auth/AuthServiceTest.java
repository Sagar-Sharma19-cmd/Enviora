package com.enviora.auth;

import com.enviora.auth.dto.LoginRequest;
import com.enviora.auth.dto.LoginResponse;
import com.enviora.auth.dto.RegisterRequest;
import com.enviora.auth.dto.UserResponse;
import com.enviora.auth.entity.EmailVerificationToken;
import com.enviora.auth.repository.EmailVerificationTokenRepository;
import com.enviora.auth.service.AuthService;
import com.enviora.notification.service.EmailService;
import com.enviora.shared.exception.ApiException;
import com.enviora.shared.ratelimit.RateLimiterService;
import com.enviora.shared.security.JwtService;
import com.enviora.user.entity.User;
import com.enviora.user.entity.UserStatus;
import com.enviora.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    private RateLimiterService rateLimiterService;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "dGhpc19pc19hX3NhbXBsZV9kZXZlbG9wbWVudF9qd3Rfc2VjcmV0X2tleV9mb3JfZW52aW9yYV9wbGF0Zm9ybV8xMjM0NTY3ODkw");
        ReflectionTestUtils.setField(jwtService, "accessTokenExpirationMs", 900000L);

        rateLimiterService = new RateLimiterService();

        authService = new AuthService(
                userRepository,
                tokenRepository,
                passwordEncoder,
                jwtService,
                emailService,
                rateLimiterService
        );
        ReflectionTestUtils.setField(authService, "verificationExpirationMinutes", 15);
    }

    @Test
    @DisplayName("Registration creates user with PENDING_VERIFICATION status and triggers email dispatch")
    void registration_createsPendingVerificationUser() {
        RegisterRequest request = new RegisterRequest("Sagar Sharma", "sagar@example.com", "StrongPassword123");

        UUID generatedId = UUID.randomUUID();
        when(userRepository.existsByEmail("sagar@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(generatedId);
            u.setCreatedAt(Instant.now());
            return u;
        });

        UserResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);

        verify(tokenRepository).save(any(EmailVerificationToken.class));
        verify(emailService).sendVerificationEmail(eq("sagar@example.com"), eq("Sagar Sharma"), anyString());
    }

    @Test
    @DisplayName("Valid token activates account and marks token as used")
    void validToken_activatesAccount() throws Exception {
        String rawToken = "sample-raw-token-123";
        String tokenHash = computeHash(rawToken);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("sagar@example.com");
        user.setStatus(UserStatus.PENDING_VERIFICATION);

        EmailVerificationToken token = new EmailVerificationToken(user, tokenHash, Instant.now().plus(15, ChronoUnit.MINUTES));

        when(tokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(token));

        Map<String, String> response = authService.verifyEmail(rawToken);

        assertThat(response.get("message")).contains("verified successfully");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(token.getUsedAt()).isNotNull();

        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
    }

    @Test
    @DisplayName("Expired token throws 400 Bad Request")
    void expiredToken_fails() throws Exception {
        String rawToken = "expired-token-123";
        String tokenHash = computeHash(rawToken);

        User user = new User();
        user.setStatus(UserStatus.PENDING_VERIFICATION);

        EmailVerificationToken token = new EmailVerificationToken(user, tokenHash, Instant.now().minus(1, ChronoUnit.MINUTES));

        when(tokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.verifyEmail(rawToken))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("expired")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Invalid token throws 400 Bad Request")
    void invalidToken_fails() {
        when(tokenRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.verifyEmail("nonexistent-token"))
                .isInstanceOf(ApiException.class)
                .hasMessage("Invalid or expired verification token")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Used token on already active user returns safe informational message")
    void usedTokenOnActiveUser_returnsInformationalMessage() throws Exception {
        String rawToken = "used-token-123";
        String tokenHash = computeHash(rawToken);

        User user = new User();
        user.setStatus(UserStatus.ACTIVE);

        EmailVerificationToken token = new EmailVerificationToken(user, tokenHash, Instant.now().plus(15, ChronoUnit.MINUTES));
        token.setUsedAt(Instant.now().minus(5, ChronoUnit.MINUTES));

        when(tokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(token));

        Map<String, String> response = authService.verifyEmail(rawToken);
        assertThat(response.get("message")).contains("already verified");
    }

    @Test
    @DisplayName("Resend verification invalidates old tokens and sends new email")
    void resendVerification_invalidatesOldToken() {
        String email = "resend.test@example.com";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setName("Resend User");
        user.setStatus(UserStatus.PENDING_VERIFICATION);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Map<String, String> response = authService.resendVerification(email);

        assertThat(response.get("message")).contains("verification link has been sent");
        verify(tokenRepository).invalidateUnusedTokensByUserId(eq(user.getId()), any(Instant.class));
        verify(tokenRepository).save(any(EmailVerificationToken.class));
        verify(emailService).sendVerificationEmail(eq(email), eq("Resend User"), anyString());
    }

    @Test
    @DisplayName("Resend verification is rate-limited when threshold exceeded")
    void resendVerification_isRateLimited() {
        String email = "ratelimit@example.com";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setStatus(UserStatus.PENDING_VERIFICATION);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Max 3 requests allowed
        authService.resendVerification(email);
        authService.resendVerification(email);
        authService.resendVerification(email);

        // 4th request must throw TOO_MANY_REQUESTS
        assertThatThrownBy(() -> authService.resendVerification(email))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Too many verification requests")
                .extracting("status")
                .isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }

    @Test
    @DisplayName("Unverified user login is blocked with 401 Unauthorized")
    void unverifiedUserLogin_isBlocked() {
        LoginRequest request = new LoginRequest("unverified@example.com", "StrongPassword123");

        User user = new User();
        user.setEmail("unverified@example.com");
        user.setPasswordHash(passwordEncoder.encode("StrongPassword123"));
        user.setStatus(UserStatus.PENDING_VERIFICATION);

        when(userRepository.findByEmail("unverified@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Email verification is required")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Verified user login succeeds and issues JWT token")
    void verifiedUserLogin_succeeds() {
        LoginRequest request = new LoginRequest("active@example.com", "StrongPassword123");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Active User");
        user.setEmail("active@example.com");
        user.setPasswordHash(passwordEncoder.encode("StrongPassword123"));
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(Instant.now());

        when(userRepository.findByEmail("active@example.com")).thenReturn(Optional.of(user));

        LoginResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getUser().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    private String computeHash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }
}
