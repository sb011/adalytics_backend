package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;

import java.util.List;

public interface IConnectorService {
    void addConnector(ConnectorRequestDTO addRequest);
    List<Connector> fetchAllConnectors(String userId);
    void removeConnector(ConnectorRequestDTO deleteRequest);
}
