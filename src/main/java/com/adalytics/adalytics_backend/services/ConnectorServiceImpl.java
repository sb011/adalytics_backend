package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.Flow;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.ConnectorResponseDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IConnectorRepository;
import com.adalytics.adalytics_backend.repositories.interfaces.IUserRepository;
import com.adalytics.adalytics_backend.services.interfaces.IConnectorService;
import com.adalytics.adalytics_backend.transformers.ConnectorTransformer;
import com.adalytics.adalytics_backend.utils.ContextUtil;
import com.adalytics.adalytics_backend.utils.FieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
public class ConnectorServiceImpl implements IConnectorService {

    @Autowired
    private IConnectorRepository connectorRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ConnectorTransformer connectorTransformer;

    @Override
    public Flow getFlow() {
        return Flow.DEFAULT;
    }

    @Override
    public Connector addConnector(ConnectorRequestDTO addRequest) {
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
            connector = connectorTransformer.convertToConnector(addRequest);
            connector.setOrganizationId(ContextUtil.getCurrentOrgId());
        }
        return connector;
    }

    @Override
    public List<ConnectorResponseDTO> fetchAllConnectors() {
        String orgId = ContextUtil.getCurrentOrgId();
        List<Connector> connectorList = connectorRepository.findByOrganizationId(orgId);
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
}
