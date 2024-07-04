package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.ConnectorResponseDTO;

import java.util.List;

public interface IConnectorService {
    void addConnector(ConnectorRequestDTO addRequest) throws Exception;
    List<ConnectorResponseDTO> fetchAllConnectors();
    void removeConnector(String id);
}
