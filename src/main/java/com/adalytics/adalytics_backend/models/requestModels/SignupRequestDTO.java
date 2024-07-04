package com.adalytics.adalytics_backend.models.requestModels;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupRequestDTO {
    private String email;
    private String password;
    private String role;
}
