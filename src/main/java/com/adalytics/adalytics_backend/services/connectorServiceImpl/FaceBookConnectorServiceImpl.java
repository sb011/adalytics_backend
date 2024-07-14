package com.adalytics.adalytics_backend.services.connectorServiceImpl;

import com.adalytics.adalytics_backend.enums.Flow;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.services.ConnectorServiceImpl;
import com.adalytics.adalytics_backend.services.platformClientImpl.FaceBookClientImpl;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Objects.isNull;

public class FaceBookConnectorServiceImpl extends ConnectorServiceImpl {

    @Autowired
    private FaceBookClientImpl faceBookClient;
    @Override
    public Flow getFlow() {
        return Flow.FACEBOOK;
    }

    @Override
    public Connector addConnector(ConnectorRequestDTO addRequest) {
        Connector connector = super.addConnector(addRequest);
        if(isNull(connector)) {
            return null;
        }
        faceBookClient.refreshAccessToken(connector);
        return connector;
    }
}