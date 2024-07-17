package com.adalytics.adalytics_backend.services.connectorServiceImpl;

import com.adalytics.adalytics_backend.enums.Flow;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IConnectorRepository;
import com.adalytics.adalytics_backend.services.ConnectorServiceImpl;
import com.adalytics.adalytics_backend.services.interfaces.IConnectorService;
import com.adalytics.adalytics_backend.services.platformClientImpl.FaceBookClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class FaceBookConnectorServiceImpl extends ConnectorServiceImpl implements IConnectorService {

    @Autowired
    private FaceBookClientImpl faceBookClient;
    @Autowired
    private IConnectorRepository connectorRepository;

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
        faceBookClient.populateUserInfo(connector);
        connectorRepository.save(connector);
        faceBookClient.getLongLiveAccessToken(connector);
        return connector;
    }
}