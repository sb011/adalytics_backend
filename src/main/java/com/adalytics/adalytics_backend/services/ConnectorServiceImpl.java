package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.Platform;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.external.ApiService;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.ConnectorResponseDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IConnectorRepository;
import com.adalytics.adalytics_backend.repositories.interfaces.IUserRepository;
import com.adalytics.adalytics_backend.services.factoris.PlatformClientFactory;
import com.adalytics.adalytics_backend.services.interfaces.IConnectorService;
import com.adalytics.adalytics_backend.transformers.ConnectorTransformer;
import com.adalytics.adalytics_backend.utils.ContextUtil;
import com.adalytics.adalytics_backend.utils.FieldValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
public class ConnectorServiceImpl implements IConnectorService {

    @Autowired
    private IConnectorRepository connectorRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ConnectorTransformer connectorTransformer;
    @Autowired
    private PlatformClientFactory platformClientFactory;
    @Autowired
    private ApiService apiService;
    @Value("${graph.api.version}")
    private String graphApiVersion;
    @Value("${app-id}")
    private String appId;
    @Value("${app-secret}")
    private String appSecret;
    @Value("${google.client.id}")
    private String clientId;
    @Value("${google.client.secret}")
    private String clientSecret;
    @Value("${google.redirect.uri}")
    private String redirectUri;
    @Value("${google.scope}")
    private String scope;
    @Value("${google.token.server.utl}")
    private String tokenUrl;

    @Override
    public void addConnector(ConnectorRequestDTO addRequest) throws Exception {
        if (isNull(addRequest))
            throw new BadRequestException("Invalid Request", ErrorCodes.Invalid_Request_Body.getErrorCode());
        validateRequest(addRequest);
        Connector connector = null;
        if (isNotBlank(addRequest.getId())) {
            Optional<Connector> existingConnector = connectorRepository.findById(addRequest.getId());
            if (existingConnector.isPresent()) {
                connector = existingConnector.get();
                connector.setToken(addRequest.getToken());
                connector.setExpirationTime(addRequest.getExpirationTime());
            }
        } else {
            Optional<Connector> isExistingConnector = connectorRepository.findByPlatformUserIdAndOrganizationId(addRequest.getPlatformUserId(), ContextUtil.getCurrentOrgId());
            if (isExistingConnector.isPresent()) {
                throw new BadRequestException("Connector is already present.", ErrorCodes.Connector_Already_Present.getErrorCode());
            }
            connector = connectorTransformer.convertToConnector(addRequest);
            connector.setOrganizationId(ContextUtil.getCurrentOrgId());
        }
        if (nonNull(connector)) {
            connectorRepository.save(connector);
            if (addRequest.getPlatform().equals(Platform.FACEBOOK.getDisplayName())) {
                updateConnectorToken(connector);
            } else if (addRequest.getPlatform().equals(Platform.GOOGLE.getDisplayName())) {
                exchangeAuthorizationCode(connector, addRequest.getAuthorizationCode());
            }
        }
    }

    @Async
    private void updateConnectorToken(Connector connector) throws JsonProcessingException {
        String url = String.format("https://graph.facebook.com/%s/oauth/access_token?grant_type=fb_exchange_token&client_id=%s&client_secret=%s&fb_exchange_token=%s",
                graphApiVersion, appId, appSecret, connector.getToken());
        String response = apiService.callExternalApi(url, "GET", null, null);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response);
        if (jsonResponse.has("error")) {
            JsonNode errorNode = jsonResponse.get("error");
            throw new BadRequestException(errorNode.get("message").asText(), ErrorCodes.Platform_Token_Invalid.getErrorCode());
        }
        long expiresAt = System.currentTimeMillis() + (jsonResponse.get("expires_in").longValue() * 1000);
        connector.setToken(jsonResponse.get("access_token").toString());
        connector.setExpirationTime(expiresAt);
        connectorRepository.save(connector);
    }

    @Async
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
            connectorRepository.save(connector);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), ErrorCodes.Platform_Token_Invalid.getErrorCode());
        }
    }

    @Override
    public List<ConnectorResponseDTO> fetchAllConnectors() {
        String orgId = ContextUtil.getCurrentOrgId();
        List<Connector> connectorList = connectorRepository.findByOrganizationId(orgId).orElse(new ArrayList<>());
        return connectorTransformer.convertToConnectorResponseDTOs(connectorList);
    }

    @Override
    public void removeConnector(String id) {
        FieldValidator.validateConnectorId(id);
        String orgId = ContextUtil.getCurrentOrgId();
        String connectorId = connectorRepository.deleteByIdAndOrganizationId(id, orgId);
        FieldValidator.validateConnectorId(connectorId);
    }

    @Override
    public void refreshAccessToken(String id) {
        String orgId = ContextUtil.getCurrentOrgId();
        Connector connector = connectorRepository.findByIdAndOrganizationId(id,orgId).orElseThrow(() -> new BadRequestException("Connector Not Found", ErrorCodes.Connector_Not_Found.getErrorCode()));
        if(!EnumUtils.isValidEnum(Platform.class, connector.getPlatform())) {
            throw new BadRequestException("Invalid Platform", ErrorCodes.Platform_Invalid.getErrorCode());
        }
        platformClientFactory.getPlatformClient(Platform.valueOf(connector.getPlatform())).refreshAccessToken(connector);
    }

    private void validateRequest(ConnectorRequestDTO connectorRequestDTO) {
        FieldValidator.validatePlatformToken(connectorRequestDTO.getToken());
        FieldValidator.validatePlatformName(connectorRequestDTO.getPlatform());
    }
}
