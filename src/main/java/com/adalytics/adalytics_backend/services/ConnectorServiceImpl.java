package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.exceptions.NotFoundException;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.entities.User;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IConnectorRepository;
import com.adalytics.adalytics_backend.repositories.interfaces.IUserRepository;
import com.adalytics.adalytics_backend.services.interfaces.IConnectorService;
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

    @Override
    public void addConnector(ConnectorRequestDTO addRequest) {
        if(isNull(addRequest))
            throw new BadRequestException("Invalid Request", ErrorCodes.Invalid_Request_Body.getErrorCode());
        validateRequest(addRequest);
        Connector connector = null;
        if(isNotBlank(addRequest.getConnectorId())) {
            Optional<Connector> existingConnector = connectorRepository.findById(addRequest.getConnectorId());
            if(existingConnector.isPresent()) {
                connector = existingConnector.get();
                connector.setToken(addRequest.getToken());
            }
        }
        else if(isNull(connector)){
            connector = Connector.builder()
                    .userId(addRequest.getUserId())
                    .token(addRequest.getToken())
                    .platform(addRequest.getPlatform())
                    .platformId(addRequest.getPlatformId())
                    .build();
        }
        if(nonNull(connector)) {
            connectorRepository.save(connector);
        }
    }

    @Override
    public List<Connector> fetchAllConnectors(String userId) {
        FieldValidator.validateUserId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User Not Found", ErrorCodes.User_Not_Found.getErrorCode()));
        return connectorRepository.findByUserId(user.getId()).orElse(new ArrayList<>());
    }

    @Override
    public void removeConnector(ConnectorRequestDTO deleteRequest) {
        if(isNull(deleteRequest))
            throw new BadRequestException("Invalid Request", ErrorCodes.Invalid_Request_Body.getErrorCode());
        FieldValidator.validateConnectorId(deleteRequest.getConnectorId());
        connectorRepository.deleteById(deleteRequest.getConnectorId());
    }

    private void validateRequest(ConnectorRequestDTO connectorRequestDTO) {
        FieldValidator.validateUserId(connectorRequestDTO.getUserId());
        FieldValidator.validatePlatformToken(connectorRequestDTO.getToken());
        FieldValidator.validatePlatformName(connectorRequestDTO.getPlatform());
    }
}
