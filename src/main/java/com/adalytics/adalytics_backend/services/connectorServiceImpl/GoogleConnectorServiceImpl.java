package com.adalytics.adalytics_backend.services.connectorServiceImpl;

import com.adalytics.adalytics_backend.enums.Flow;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IConnectorRepository;
import com.adalytics.adalytics_backend.services.ConnectorServiceImpl;
import com.adalytics.adalytics_backend.services.interfaces.IConnectorService;
import com.adalytics.adalytics_backend.services.platformClientImpl.GoogleClientImpl;
import com.adalytics.adalytics_backend.utils.NotificationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class GoogleConnectorServiceImpl extends ConnectorServiceImpl implements IConnectorService {

    @Autowired
    private GoogleClientImpl googleClient;
    @Autowired
    private IConnectorRepository connectorRepository;
    @Autowired
    private NotificationHandler notificationHelper;

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

        googleClient.exchangeAuthorizationCode(connector, addRequest.getToken());
        googleClient.populateUserInfo(connector);
        connectorRepository.save(connector);
        notificationHelper.sendPusherNotification("DATA_UPDATED", "message");
        return connector;
    }
}