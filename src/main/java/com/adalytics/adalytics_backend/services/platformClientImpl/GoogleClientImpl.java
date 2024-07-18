package com.adalytics.adalytics_backend.services.platformClientImpl;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.exceptions.BadGatewayException;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.external.ApiService;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.externalDTOs.googleDTOs.GoogleUserInfoDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IConnectorRepository;
import com.adalytics.adalytics_backend.utils.ContextUtil;
import com.adalytics.adalytics_backend.utils.JsonUtil;
import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v17.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v17.services.SearchGoogleAdsRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.Credentials;
import com.google.auth.oauth2.UserCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class GoogleClientImpl{

    @Value("${google.client.id}")
    private String clientId;
    @Value("${google.client.secret}")
    private String clientSecret;
    @Value("${google.scope}")
    private String scope;
    @Value("${google.redirect.uri}")
    private String redirectUri;
    @Value("${google.token.server.utl}")
    private String tokenUrl;

    @Autowired
    private ApiService apiService;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    @Autowired
    private IConnectorRepository connectorRepository;

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

    public void exchangeAuthorizationCode(Connector connector, String authorizationCode) {
        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    tokenUrl,
                    clientId,
                    clientSecret,
                    authorizationCode,
                    redirectUri
            ).setScopes(Collections.singletonList(scope))
                    .execute();

            connector.setRefreshToken(tokenResponse.getRefreshToken());
            connector.setToken(tokenResponse.getAccessToken());
            connector.setExpirationTime(Long.MAX_VALUE);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), ErrorCodes.Platform_Token_Invalid.getErrorCode());
        }
    }

    public Connector populateUserInfo(Connector connector) {
        String url = String.format("https://www.googleapis.com/oauth2/v3/userinfo?access_token=%s",connector.getToken());
        String response = apiService.callExternalApi(url, "GET", null, null);
        GoogleUserInfoDTO googleUserInfoDTO = JsonUtil.getObjectFromJsonString(response, GoogleUserInfoDTO.class);
        if (nonNull(googleUserInfoDTO.getError())) {
            throw new BadRequestException(googleUserInfoDTO.getError().getMessage(), ErrorCodes.Platform_Invalid.getErrorCode());
        }
        connector.setPlatformUserId(googleUserInfoDTO.getSub());
        connector.setEmail(googleUserInfoDTO.getEmail());
        Optional<Connector> isExistingConnector = connectorRepository.findByPlatformUserIdAndOrganizationId(connector.getPlatformUserId(), ContextUtil.getCurrentOrgId());
        if (isExistingConnector.isPresent()) {
            throw new BadRequestException("Connector is already present.", ErrorCodes.Connector_Already_Present.getErrorCode());
        }
        return connector;
    }

    public void fetchCampaigns(Connector connector){
        if(isNull(connector)) {
            return;
        }

        UserCredentials userCredentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(connector.getRefreshToken())
                .build();

//        Long customerId = 105156177423765794362;
        GoogleAdsClient googleAdsClient = GoogleAdsClient.newBuilder()
                .setLoginCustomerId(3126015003L)
                .setCredentials(userCredentials)
                .setDeveloperToken("t78")
                .build();

        String query = "SELECT campaign.id, campaign.name, campaign.status FROM campaign ORDER BY campaign.id";
        try (GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient()) {
            SearchGoogleAdsRequest request = SearchGoogleAdsRequest.newBuilder()
                    .setCustomerId(connector.getPlatformUserId())
                    .setQuery(query)
                    .build();
            GoogleAdsServiceClient.SearchPagedResponse response = googleAdsServiceClient.search(request);
            log.info("response from google {}", response.toString());
        }
    }
}