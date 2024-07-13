package com.adalytics.adalytics_backend.services.platformClientImpl;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.Platform;
import com.adalytics.adalytics_backend.exceptions.BadGatewayException;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.external.ApiService;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.repositories.interfaces.IConnectorRepository;
import com.adalytics.adalytics_backend.services.interfaces.IPlatformClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FaceBookClientImpl implements IPlatformClient {

    @Value("${graph.api.version}")
    private String graphApiVersion;
    @Value("${app-id}")
    private String appId;
    @Value("${app-secret}")
    private String appSecret;

    @Autowired
    private ApiService apiService;

    @Autowired
    private IConnectorRepository connectorRepository;

    @Override
    public Platform getPlatform() {
        return Platform.FACEBOOK;
    }

    @Override
    public void refreshAccessToken(Connector connector) {
        JsonNode jsonResponse = null;
        try {
            String url = String.format("https://graph.facebook.com/%s/oauth/access_token?grant_type=fb_exchange_token&client_id=%s&client_secret=%s&fb_exchange_token=%s",
                    graphApiVersion, appId, appSecret, connector.getToken());
            String response = apiService.callExternalApi(url, "GET", null, null);
            ObjectMapper objectMapper = new ObjectMapper();
            jsonResponse = objectMapper.readTree(response);
        } catch (Exception ex) {
            log.error("Failed to fetch Access Token", ex);
            throw new BadGatewayException("Failed to refresh Access Token", ErrorCodes.Client_Not_Responding.getErrorCode());
        }
        if (jsonResponse.has("error")) {
            JsonNode errorNode = jsonResponse.get("error");
            throw new BadRequestException(errorNode.get("message").asText(), ErrorCodes.Platform_Token_Invalid.getErrorCode());
        }
        long expiresAt = System.currentTimeMillis() + (jsonResponse.get("expires_in").longValue() * 1000);
        connector.setToken(jsonResponse.get("access_token").toString());
        connector.setExpirationTime(expiresAt);
        connectorRepository.save(connector);
        log.info("===========================Done========================");
    }
}