package com.adalytics.adalytics_backend.models.externalDTOs.googleDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleExchangeTokenDTO {
    private String access_token;
    private String refresh_token;
}
