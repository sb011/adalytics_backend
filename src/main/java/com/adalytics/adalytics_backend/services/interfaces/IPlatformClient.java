package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.enums.Platform;
import com.adalytics.adalytics_backend.models.entities.Connector;

public interface IPlatformClient {
    Platform getPlatform();
    void refreshAccessToken(Connector connector);
}