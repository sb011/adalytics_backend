package com.adalytics.adalytics_backend.models.requestModels;

import lombok.Data;

@Data
public class LoginRequestModel {
    private String email;
    private String password;
}
