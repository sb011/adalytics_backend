package com.adalytics.adalytics_backend.models.externalDTOs.facebookDTOs;

import lombok.Data;

@Data
public class FacebookLongLiveTokenDTO{
    private String access_token;
    private String token_type;
    private Long expires_in;

    //for error
    private FaceBookErrorDTO error;
}