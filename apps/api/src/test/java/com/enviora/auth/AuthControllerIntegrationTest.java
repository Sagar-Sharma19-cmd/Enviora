package com.enviora.auth;

import com.enviora.auth.dto.LoginRequest;
import com.enviora.auth.dto.RegisterRequest;
import com.enviora.auth.dto.ResendVerificationRequest;
import com.enviora.auth.entity.EmailVerificationToken;
import com.enviora.auth.repository.EmailVerificationTokenRepository;
import com.enviora.user.entity.User;
import com.enviora.user.entity.UserStatus;
import com.enviora.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    @Test
    @DisplayName("POST /api/v1/auth/register returns 201 Created with PENDING_VERIFICATION status")
    void successfulRegistration_returns201CreatedAndPendingVerification() throws Exception {
        RegisterRequest request = new RegisterRequest("Alex Rivera", "alex.verify@example.com", "Password123!");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Alex Rivera"))
                .andExpect(jsonPath("$.email").value("alex.verify@example.com"))
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login blocked for PENDING_VERIFICATION user")
    void loginBlockedForPendingVerificationUser() throws Exception {
        RegisterRequest reg = new RegisterRequest("Blocked User", "blocked@example.com", "Password123!");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest("blocked@example.com", "Password123!");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Email verification is required")));
    }

    @Test
    @DisplayName("GET /api/v1/auth/verify-email activates user and allows login")
    void verifyEmail_activatesUserAndEnablesLogin() throws Exception {
        RegisterRequest reg = new RegisterRequest("Flow User", "flow@example.com", "Password123!");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail("flow@example.com").orElseThrow();
        EmailVerificationToken tokenEntity = tokenRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .findFirst().orElseThrow();

        // In test context, token_hash was created from generated rawToken. Manually set raw token matching hash for testing
        String rawToken = "integration-test-raw-token";
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String hash = HexFormat.of().formatHex(digest.digest(rawToken.getBytes(StandardCharsets.UTF_8)));
        tokenEntity.setTokenHash(hash);
        tokenRepository.saveAndFlush(tokenEntity);

        mockMvc.perform(get("/auth/verify-email").param("token", rawToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("verified successfully")));

        LoginRequest login = new LoginRequest("flow@example.com", "Password123!");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/resend-verification returns generic 200 OK response")
    void resendVerification_returnsGeneric200Ok() throws Exception {
        ResendVerificationRequest request = new ResendVerificationRequest("resend.test@example.com");

        mockMvc.perform(post("/auth/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("verification link has been sent")));
    }
}
