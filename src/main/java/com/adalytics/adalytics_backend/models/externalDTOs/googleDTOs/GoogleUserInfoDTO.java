package com.adalytics.adalytics_backend.models.externalDTOs.googleDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUserInfoDTO {
    private String sub;
    private String email;

    //for error
    private GoogleErrorDTO error;
}
