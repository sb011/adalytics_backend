package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.external.ApiService;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.ConnectorResponseDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IConnectorRepository;
import com.adalytics.adalytics_backend.repositories.interfaces.IUserRepository;
import com.adalytics.adalytics_backend.services.interfaces.IConnectorService;
import com.adalytics.adalytics_backend.transformers.ConnectorTransformer;
import com.adalytics.adalytics_backend.utils.ContextUtil;
import com.adalytics.adalytics_backend.utils.FieldValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private ApiService apiService;
    @Value("${graph.api.version}")
    private String graphApiVersion;
    @Value("${app-id}")
    private String appId;
    @Value("${app-secret}")
    private String appSecret;

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
            Optional<Connector> isExistingConnector = connectorRepository.findByPlatformUserId(addRequest.getPlatformUserId());
            if (isExistingConnector.isPresent()) {
                throw new BadRequestException("Connector is already present.", ErrorCodes.Connector_Already_Present.getErrorCode());
            }
            connector = connectorTransformer.convertToConnector(addRequest);
            connector.setOrganizationId(ContextUtil.getCurrentOrgId());
        }
        if (nonNull(connector)) {
            JsonNode response = getLongLivedToken(connector.getToken());
            if (response.has("error")) {
                JsonNode errorNode = response.get("error");
                throw new BadRequestException(errorNode.get("message").asText(), ErrorCodes.Platform_Token_Invalid.getErrorCode());
            }
            long expiresAt = System.currentTimeMillis() + (response.get("expires_in").longValue() * 1000);
            connector.setToken(String.valueOf(response.get("access_token")));
            connector.setExpirationTime(Long.toString(expiresAt));
            connectorRepository.save(connector);
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

    private void validateRequest(ConnectorRequestDTO connectorRequestDTO) {
        FieldValidator.validatePlatformToken(connectorRequestDTO.getToken());
        FieldValidator.validatePlatformName(connectorRequestDTO.getPlatform());
    }

    private JsonNode getLongLivedToken(String token) throws Exception {
        String url = String.format("https://graph.facebook.com/%s/oauth/access_token?grant_type=fb_exchange_token&client_id=%s&client_secret=%s&fb_exchange_token=%s",
                graphApiVersion, appId, appSecret, token);
        String response = apiService.callExternalApi(url, "GET", null, null);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(response);
    }
}
