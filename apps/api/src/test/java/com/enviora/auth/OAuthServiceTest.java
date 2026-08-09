package com.enviora.auth;

import com.enviora.auth.dto.LoginResponse;
import com.enviora.auth.entity.AuthIdentity;
import com.enviora.auth.entity.AuthProvider;
import com.enviora.auth.oauth.service.OAuthService;
import com.enviora.auth.repository.AuthIdentityRepository;
import com.enviora.shared.exception.ApiException;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthIdentityRepository identityRepository;

    private JwtService jwtService;
    private OAuthService oAuthService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "dGhpc19pc19hX3NhbXBsZV9kZXZlbG9wbWVudF9qd3Rfc2VjcmV0X2tleV9mb3JfZW52aW9yYV9wbGF0Zm9ybV8xMjM0NTY3ODkw");
        ReflectionTestUtils.setField(jwtService, "accessTokenExpirationMs", 900000L);

        oAuthService = new OAuthService(userRepository, identityRepository, jwtService);
    }

    @Test
    @DisplayName("Case A: Existing Google identity signs in existing Enviora user and returns Enviora JWT")
    void existingGoogleIdentity_signsInUser() {
        String googleSub = "google-sub-123456";
        String email = "existing.google@example.com";

        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setName("Google User");
        existingUser.setEmail(email);
        existingUser.setStatus(UserStatus.ACTIVE);

        AuthIdentity identity = new AuthIdentity(existingUser, AuthProvider.GOOGLE, googleSub, email);

        when(identityRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, googleSub))
                .thenReturn(Optional.of(identity));

        LoginResponse response = oAuthService.processGoogleUser(googleSub, email, "Google User");

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getUser().getEmail()).isEqualTo(email);
        assertThat(jwtService.validateToken(response.getToken())).isTrue();

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Case B: New Google identity creates new active user without password hash and links identity")
    void newGoogleIdentity_createsNewActiveUserAndIdentity() {
        String googleSub = "new-google-sub-789";
        String email = "new.google@example.com";
        String name = "New Google User";

        when(identityRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, googleSub))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UUID generatedId = UUID.randomUUID();
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(generatedId);
            u.setCreatedAt(Instant.now());
            return u;
        });

        LoginResponse response = oAuthService.processGoogleUser(googleSub, email, name);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getUser().getEmail()).isEqualTo(email);
        assertThat(response.getUser().getStatus()).isEqualTo(UserStatus.ACTIVE);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User createdUser = userCaptor.getValue();

        assertThat(createdUser.getPasswordHash()).isNull(); // No fake password
        assertThat(createdUser.getStatus()).isEqualTo(UserStatus.ACTIVE); // Auto-verified by Google

        ArgumentCaptor<AuthIdentity> identityCaptor = ArgumentCaptor.forClass(AuthIdentity.class);
        verify(identityRepository).save(identityCaptor.capture());
        AuthIdentity createdIdentity = identityCaptor.getValue();

        assertThat(createdIdentity.getProvider()).isEqualTo(AuthProvider.GOOGLE);
        assertThat(createdIdentity.getProviderUserId()).isEqualTo(googleSub);
    }

    @Test
    @DisplayName("Case C: Existing password user with same email is NOT silently linked (returns 409 Conflict)")
    void existingPasswordUser_doesNotAutoLink() {
        String googleSub = "unlinked-sub-999";
        String email = "password.user@example.com";

        when(identityRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, googleSub))
                .thenReturn(Optional.empty());

        User existingPasswordUser = new User();
        existingPasswordUser.setId(UUID.randomUUID());
        existingPasswordUser.setEmail(email);
        existingPasswordUser.setPasswordHash("$2a$10$hashedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingPasswordUser));

        assertThatThrownBy(() -> oAuthService.processGoogleUser(googleSub, email, "Password User"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Please sign in with your password first")
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);

        verify(identityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Missing Google subject claim throws 400 Bad Request")
    void missingGoogleSub_throwsBadRequest() {
        assertThatThrownBy(() -> oAuthService.processGoogleUser("", "test@example.com", "Test"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("subject claim")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
