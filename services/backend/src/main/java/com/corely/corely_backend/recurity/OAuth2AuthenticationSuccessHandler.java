package com.corely.corely_backend.recurity;

import com.corely.corely_backend.dto.response.auth.AuthenticateResponse;
import com.corely.corely_backend.entity.User;
import com.corely.corely_backend.service.AuthenticationService;
import com.corely.corely_backend.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = customOAuth2User.getUser();

        AuthenticateResponse tokens = jwtService.generateTokenPair(user);

        String targetUrl = frontendUrl + "/auth/oauth2/redirect"
                + "?token=" + tokens.getToken()
                + "&refreshToken=" + tokens.getRefreshToken();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
