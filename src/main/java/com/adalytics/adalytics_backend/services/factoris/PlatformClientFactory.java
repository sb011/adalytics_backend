package com.adalytics.adalytics_backend.services.factoris;

import com.adalytics.adalytics_backend.enums.Platform;
import com.adalytics.adalytics_backend.services.interfaces.IPlatformClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PlatformClientFactory {

    private final Map<Platform, IPlatformClient> platformClientMap;

    public PlatformClientFactory(List<IPlatformClient> platformClientList) {
        Map<Platform, IPlatformClient> map = new HashMap<>();
        platformClientList.forEach(platformClient -> {
            try {
                map.put(platformClient.getPlatform(), platformClient);
            } catch (Exception ex) {
                log.error("Error initializing platform client factory", ex);
            }
        });
        this.platformClientMap = map;
    }

    public IPlatformClient getPlatformClient(Platform platform) {
        return this.platformClientMap.get(platform);
    }
}