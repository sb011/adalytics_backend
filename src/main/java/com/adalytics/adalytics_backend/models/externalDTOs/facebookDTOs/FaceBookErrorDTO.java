package com.adalytics.adalytics_backend.models.externalDTOs.facebookDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaceBookErrorDTO {
    private String message;
    private String type;
}