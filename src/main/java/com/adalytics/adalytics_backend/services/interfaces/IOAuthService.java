package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.responseModels.OAuthTokenResponseDTO;

public interface IOAuthService {
    String getAuthorizationUrl();

    OAuthTokenResponseDTO exchangeAuthorizationCode(String authorizationCode);
}
