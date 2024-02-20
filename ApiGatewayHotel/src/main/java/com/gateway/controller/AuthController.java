package com.gateway.controller;

import com.gateway.model.AuthResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private Logger logger= LoggerFactory.getLogger(AuthController.class);
    @GetMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OidcUser user,
            Model Model){
        logger.info("user email id {}",user.getEmail());
        AuthResponse authResponse=new AuthResponse();
        authResponse.setUserId(user.getEmail());
        authResponse.setAccessToken(authorizedClient.getAccessToken().getTokenValue());
        authResponse.setRefreshToken(authorizedClient.getRefreshToken().getTokenValue());
        authResponse.setExpireAt(authorizedClient.getAccessToken().getExpiresAt().getEpochSecond());
        List<String> authorities = user.getAuthorities().stream().map(grantedAuthority -> {
            return grantedAuthority.getAuthority();
        }).toList();
        authResponse.setAuthorities(authorities);
        return ResponseEntity.status(HttpStatus.SC_OK).body(authResponse);

    }
}
