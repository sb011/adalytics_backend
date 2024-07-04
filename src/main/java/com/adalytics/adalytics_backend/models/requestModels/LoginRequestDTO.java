package com.adalytics.adalytics_backend.models.requestModels;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}
