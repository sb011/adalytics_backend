package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.models.responseModels.OAuthTokenResponseDTO;
import com.adalytics.adalytics_backend.services.interfaces.IOAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Service
@Slf4j
public class OAuthServiceImpl implements IOAuthService {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    @Value("${google.authorization.url}")
    private String authorizationUrl;

    @Override
    public String getAuthorizationUrl() {
        return authorizationUrl + "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=https://www.googleapis.com/auth/adwords" +
                "&access_type=offline";
    }

    @Override
    public OAuthTokenResponseDTO exchangeAuthorizationCode(String authorizationCode) {
        System.out.println("========================");
        System.out.println(authorizationCode);
        System.out.println("==========================");
        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    "https://oauth2.googleapis.com/token",
                    clientId,
                    clientSecret,
                    authorizationCode,
                    redirectUri
            ).setScopes(Collections.singletonList("https://www.googleapis.com/auth/adwords")).execute();

            OAuthTokenResponseDTO oAuthTokenResponseDTO = new OAuthTokenResponseDTO(
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getExpiresInSeconds()
            );

            System.out.println(oAuthTokenResponseDTO);

            return oAuthTokenResponseDTO;
        } catch (IOException e) {
            System.out.println("============================================");
            e.printStackTrace();
            return null;
        }
    }
}


//https://oauth2.googleapis.com/token?code=4%2F0ATx3LY7duZROcMpkpmcxjPKRXsgTbIybWzHW_F6PBnWf2rO9rGzDWObOKjGsxPK_0ZukEg&redirect_uri=http%3A%2F%2Flocalhost&client_id=699999671467-db7htgrmgip31o1qbjmk5ooju1smgbbd.apps.googleusercontent.com&client_secret=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&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fadwords&grant_type=authorization_code