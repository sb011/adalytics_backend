package com.adalytics.adalytics_backend.models.responseModels;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class OAuthTokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
}