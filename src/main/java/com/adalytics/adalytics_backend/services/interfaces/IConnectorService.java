package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.ConnectorResponseDTO;

import java.util.List;

public interface IConnectorService {
    Connector addConnector(ConnectorRequestDTO addRequest);
    List<ConnectorResponseDTO> fetchAllConnectors();
    void removeConnector(String id);
}
