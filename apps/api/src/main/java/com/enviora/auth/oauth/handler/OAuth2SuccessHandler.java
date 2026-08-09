package com.enviora.auth.oauth.handler;

import com.enviora.auth.dto.LoginResponse;
import com.enviora.auth.oauth.service.OAuthService;
import com.enviora.shared.exception.ApiException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    private final OAuthService oAuthService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public OAuth2SuccessHandler(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String googleSub = null;
        if (oAuth2User instanceof OidcUser oidcUser) {
            googleSub = oidcUser.getSubject();
        }
        if (googleSub == null || googleSub.isBlank()) {
            googleSub = oAuth2User.getAttribute("sub");
        }

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        try {
            LoginResponse loginResponse = oAuthService.processGoogleUser(googleSub, email, name);

            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/login/oauth/callback")
                    .queryParam("token", loginResponse.getToken())
                    .build()
                    .toUriString();

            log.info("OAuth authentication success for recipient: {}. Redirecting to frontend callback.", email);
            response.sendRedirect(targetUrl);
        } catch (ApiException e) {
            String errorCode = e.getStatus().is4xxClientError() && e.getMessage().contains("already exists")
                    ? "account_exists"
                    : "oauth_failed";

            String errorUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/login/oauth/callback")
                    .queryParam("error", errorCode)
                    .build()
                    .toUriString();

            log.warn("OAuth process failed for email: {}. Error: {}", email, e.getMessage());
            response.sendRedirect(errorUrl);
        }
    }
}
