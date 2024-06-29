package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
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

    @Override
    public void addConnector(ConnectorRequestDTO addRequest) {
        if(isNull(addRequest))
            throw new BadRequestException("Invalid Request", ErrorCodes.Invalid_Request_Body.getErrorCode());
        validateRequest(addRequest);
        Connector connector = null;
        if(isNotBlank(addRequest.getId())) {
            Optional<Connector> existingConnector = connectorRepository.findById(addRequest.getId());
            if(existingConnector.isPresent()) {
                connector = existingConnector.get();
                connector.setToken(addRequest.getToken());
                connector.setExpirationTime(addRequest.getExpirationTime());
            }
        }
        else if(isNull(connector)){
            connector = connectorTransformer.convertToConnector(addRequest);
            connector.setUserId(ContextUtil.getCurrentUserId());
        }
        if(nonNull(connector)) {
            connectorRepository.save(connector);
        }
    }

    @Override
    public List<ConnectorResponseDTO> fetchAllConnectors() {
        String userId = ContextUtil.getCurrentUserId();
        List<Connector> connectorList = connectorRepository.findByUserId(userId).orElse(new ArrayList<>());
        return connectorTransformer.convertToConnectorResponseDTOs(connectorList);
    }

    @Override
    public void removeConnector(String id) {
        FieldValidator.validateConnectorId(id);
        String userId = ContextUtil.getCurrentUserId();
        String connectorId = connectorRepository.deleteByIdAndUserId(id, userId);
        FieldValidator.validateConnectorId(connectorId);
    }

    private void validateRequest(ConnectorRequestDTO connectorRequestDTO) {
        FieldValidator.validatePlatformToken(connectorRequestDTO.getToken());
        FieldValidator.validatePlatformName(connectorRequestDTO.getPlatform());
    }
}
