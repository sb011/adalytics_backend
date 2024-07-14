package com.adalytics.adalytics_backend.services.factoris;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.Flow;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.services.interfaces.IConnectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class ConnectorServiceFactory {

    private final Map<Flow, IConnectorService> connectorServiceMap;

    public ConnectorServiceFactory(List<IConnectorService> connectorServiceList) {
        Map<Flow, IConnectorService> map = new HashMap<>();
        connectorServiceList.forEach(connectorService -> {
            try {
                map.put(connectorService.getFlow(), connectorService);
            } catch (Exception ex) {
                log.error("Error initializing connector service factory", ex);
            }
        });
        this.connectorServiceMap = map;
    }

    public IConnectorService getConnectorService(Flow flow) {
        IConnectorService connectorService = this.connectorServiceMap.getOrDefault(flow, null);
        if(isNull(connectorService)) {
            throw new BadRequestException("Invalid connector service", ErrorCodes.Platform_Invalid.getErrorCode());
        }
        return connectorService;
    }
}