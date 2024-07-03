package com.adalytics.adalytics_backend.transformers;

import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.entities.User;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.ConnectorResponseDTO;
import com.adalytics.adalytics_backend.models.responseModels.UserResponseDTO;
import com.adalytics.adalytics_backend.transformers.mapper.IConnectorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConnectorTransformer {

    private final IConnectorMapper connectorMapper;

    public List<ConnectorResponseDTO> convertToConnectorResponseDTOs(List<Connector> connectorList) {
        return connectorMapper.convertToConnectorResponseDTOs(connectorList);
    }

    public Connector convertToConnector(ConnectorRequestDTO connectorRequestDTO) {
        return connectorMapper.convertToConnector(connectorRequestDTO);
    }

    public List<UserResponseDTO> convertToUserResponseDTOs(List<User> userList) {
        return connectorMapper.convertToUserResponseDTOs(userList);
    }
}