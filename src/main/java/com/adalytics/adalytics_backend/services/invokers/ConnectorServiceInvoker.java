package com.adalytics.adalytics_backend.services.invokers;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.Flow;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.services.factoris.ConnectorServiceFactory;
import com.adalytics.adalytics_backend.services.interfaces.IConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.adalytics.adalytics_backend.constants.CommonConstants.platformToFlowMap;
import static java.util.Objects.isNull;

@Component
public class ConnectorServiceInvoker {

    @Autowired
    ConnectorServiceFactory connectorServiceFactory;

    public IConnectorService invoke(String platform) {
        Flow flow = platformToFlowMap.get(platform);
        if(isNull(flow)) {
            throw new BadRequestException("Invalid Flow", ErrorCodes.Platform_Invalid.getErrorCode());
        }
        return connectorServiceFactory.getConnectorService(flow);
    }
}