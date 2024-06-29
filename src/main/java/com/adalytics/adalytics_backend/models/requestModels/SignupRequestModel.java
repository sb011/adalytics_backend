package com.adalytics.adalytics_backend.models.requestModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestModel {
    private String email;
    private String password;
}
