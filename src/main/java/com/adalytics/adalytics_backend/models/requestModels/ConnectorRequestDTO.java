package com.adalytics.adalytics_backend.models.requestModels;

import com.adalytics.adalytics_backend.enums.Platform;
import lombok.Data;

@Data
public class ConnectorRequestDTO {
    private String userId;
    private String connectorId;
    private String token;
    private String platformId;
    /**
     * {@link Platform}
     */
    private String platform;
}
