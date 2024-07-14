package com.adalytics.adalytics_backend.services.connectorServiceImpl;

import com.adalytics.adalytics_backend.enums.Flow;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.services.ConnectorServiceImpl;
import com.adalytics.adalytics_backend.services.platformClientImpl.GoogleClientImpl;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Objects.isNull;

public class GoogleConnectorServiceImpl extends ConnectorServiceImpl{

    @Autowired
    private GoogleClientImpl googleClient;

    @Override
    public Flow getFlow() {
        return Flow.GOOGLE;
    }
    @Override
    public Connector addConnector(ConnectorRequestDTO addRequest) {
        Connector connector = super.addConnector(addRequest);
        if(isNull(connector)) {
            return null;
        }
        googleClient.exchangeAuthorizationCode(connector, addRequest.getAuthorizationCode());
        return connector;
    }
}