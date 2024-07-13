package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.models.responseModels.OAuthTokenResponseDTO;
import com.adalytics.adalytics_backend.services.interfaces.IOAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/oauth")
public class OAuthController {
    @Autowired
    private IOAuthService oAuthService;

    @GetMapping("/authorize")
    public ResponseEntity<String> authorize() {
        String authorizationUrl = oAuthService.getAuthorizationUrl();
        return new ResponseEntity<>(authorizationUrl, HttpStatus.OK);
    }

    @GetMapping("/callback")
    public OAuthTokenResponseDTO oauthCallback(@RequestParam("code") String authorizationCode) {
        return oAuthService.exchangeAuthorizationCode(authorizationCode);
    }
}
