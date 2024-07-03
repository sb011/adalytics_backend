package com.adalytics.adalytics_backend.models.responseModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectorResponseDTO {
    private String id;
    /**
     * {@link com.adalytics.adalytics_backend.enums.Platform}
     */
    private String email;
    private String platform;
    private String platformUserId;
    private String tokenExpiry;
}