package com.adalytics.adalytics_backend.services.platformClientImpl;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.Platform;
import com.adalytics.adalytics_backend.exceptions.BadGatewayException;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.repositories.interfaces.IConnectorRepository;
import com.adalytics.adalytics_backend.services.interfaces.IPlatformClient;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class GoogleClientImpl implements IPlatformClient {

    @Value("${google.client.id}")
    private String clientId;
    @Value("${google.client.secret}")
    private String clientSecret;
    @Value("${google.scope}")
    private String scope;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    @Autowired
    private IConnectorRepository connectorRepository;

    @Override
    public Platform getPlatform() {
        return Platform.GOOGLE;
    }

    @Override
    public void refreshAccessToken(Connector connector){
        // Load client secrets
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setInstalled(
                new GoogleClientSecrets.Details()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret));

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singletonList(scope))
                .setAccessType("offline")
                .build();

        // Load the refresh token and use it to get a new access token
        GoogleTokenResponse tokenResponse = new GoogleTokenResponse();
        tokenResponse.setRefreshToken(connector.getRefreshToken());

        try {
            Credential credential = flow.createAndStoreCredential(tokenResponse, connector.getPlatformUserId());
            // Refresh the access token
            if (credential.refreshToken()) {
                connector.setToken(credential.getAccessToken());
                connector.setAccessTokenExpirationTime(System.currentTimeMillis() + credential.getExpirationTimeMilliseconds());
                connectorRepository.save(connector);
            } else {
                throw new RuntimeException("Failed to refresh access token");
            }
        } catch (Exception ex) {
            log.error("Failed to refresh Access Token", ex);
            throw new BadGatewayException("Failed to refresh Access Token", ErrorCodes.Client_Not_Responding.getErrorCode());
        }
        log.info("===========================Done========================");
    }
}