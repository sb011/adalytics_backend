package com.adalytics.adalytics_backend.models.externalDTOs.facebookDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookLongLiveTokenDTO{
    private String access_token;
    private String token_type;
    private Long expires_in;

    //for error
    private FaceBookErrorDTO error;
}