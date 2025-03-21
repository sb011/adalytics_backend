package com.adalytics.adalytics_backend.models.requestModels;

import com.adalytics.adalytics_backend.enums.Platform;
import lombok.Data;

@Data
public class ConnectorRequestDTO {
    private String id;
    private String email;
    private String platform;
    private String platformUserId;
    private String token;
    private Long expirationTime;
}
