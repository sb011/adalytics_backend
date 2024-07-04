package com.adalytics.adalytics_backend.models.responseModels;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LongLivedTokenResponseDTO {
    String accessToken;
    Long expiresIn;
}
