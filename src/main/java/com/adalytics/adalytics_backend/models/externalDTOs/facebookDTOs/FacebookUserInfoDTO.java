package com.adalytics.adalytics_backend.models.externalDTOs.facebookDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookUserInfoDTO {
    private String id;
    private String email;

    //for error
    private FaceBookErrorDTO error;
}