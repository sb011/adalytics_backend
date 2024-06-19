package com.adalytics.adalytics_backend.models.requestModels;

import lombok.Data;

@Data
public class SignupRequestModel {
    private String email;
    private String password;
}
