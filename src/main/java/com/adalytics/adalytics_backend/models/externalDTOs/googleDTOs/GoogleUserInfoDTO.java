package com.adalytics.adalytics_backend.models.externalDTOs.googleDTOs;

import lombok.Data;

@Data
public class GoogleUserInfoDTO {
    private String sub;
    private String email;

    //for error
    private GoogleErrorDTO error;
}
