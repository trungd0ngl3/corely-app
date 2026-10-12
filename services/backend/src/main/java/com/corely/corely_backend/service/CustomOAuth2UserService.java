package com.corely.corely_backend.service;

import com.corely.corely_backend.recurity.CustomOAuth2User;
import com.corely.corely_backend.dto.response.auth.OAuth2UserInfo;
import com.corely.corely_backend.entity.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    static String GOOGLE = "google";
    static String FACEBOOK = "facebook";
    AuthenticationService authenticationService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oauth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo userInfo = extractUserInfo(provider, oauth2User);

        User user = authenticationService.processOAuth2User(
                userInfo.getEmail(),
                userInfo.getName(),
                userInfo.getAvatar(),
                provider,
                userInfo.getProviderId());

        return new CustomOAuth2User(oauth2User, user);
    }

    @SuppressWarnings("unchecked")
    private OAuth2UserInfo extractUserInfo(String provider, OAuth2User user) {

        if (GOOGLE.equals(provider)) {
            return OAuth2UserInfo.builder()
                    .providerId(user.getAttribute("sub"))
                    .email(user.getAttribute("email"))
                    .name(user.getAttribute("name"))
                    .avatar(user.getAttribute("picture"))
                    .build();
        }

        if (FACEBOOK.equals(provider)) {

            String avatar = null;

            Map<String, Object> picture = user.getAttribute("picture");
            if (picture != null) {
                Map<String, Object> data = (Map<String, Object>) picture.get("data");
                if (data != null) {
                    avatar = (String) data.get("url");
                }
            }

            return OAuth2UserInfo.builder()
                    .providerId(user.getAttribute("id"))
                    .email(user.getAttribute("email"))
                    .name(user.getAttribute("name"))
                    .avatar(avatar)
                    .build();
        }

        throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
    }
}
