package com.adalytics.adalytics_backend.models.externalDTOs.facebookDTOs;

import lombok.Data;

@Data
public class FacebookUserInfoDTO {
    private String id;
    private String email;

    //for error
    private FaceBookErrorDTO error;
}