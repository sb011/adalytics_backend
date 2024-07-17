package com.adalytics.adalytics_backend.models.externalDTOs.googleDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleErrorDTO {
    private String message;
    private String type;
}
