package com.adalytics.adalytics_backend.models.responseModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseModel {
    private String token;
}
