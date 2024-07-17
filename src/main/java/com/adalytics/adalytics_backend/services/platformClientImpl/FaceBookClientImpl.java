package com.adalytics.adalytics_backend.services.platformClientImpl;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.external.ApiService;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.externalDTOs.facebookDTOs.FacebookLongLiveTokenDTO;
import com.adalytics.adalytics_backend.models.externalDTOs.facebookDTOs.FacebookUserInfoDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IConnectorRepository;
import com.adalytics.adalytics_backend.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class FaceBookClientImpl{

    @Value("${graph.api.version}")
    private String graphApiVersion;
    @Value("${app-id}")
    private String appId;
    @Value("${app-secret}")
    private String appSecret;

    @Autowired
    private ApiService apiService;

    @Autowired
    private IConnectorRepository connectorRepository;

    @Async
    public void getLongLiveAccessToken(Connector connector) {
        String url = String.format("https://graph.facebook.com/%s/oauth/access_token?grant_type=fb_exchange_token&client_id=%s&client_secret=%s&fb_exchange_token=%s",
                    graphApiVersion, appId, appSecret, connector.getToken());
        String response = apiService.callExternalApi(url, "GET", null, null);
        FacebookLongLiveTokenDTO facebookLongLiveTokenDTO = JsonUtil.getObjectFromJsonString(response, FacebookLongLiveTokenDTO.class);
        if (nonNull(facebookLongLiveTokenDTO.getError())) {
            throw new BadRequestException(facebookLongLiveTokenDTO.getError().getMessage(), ErrorCodes.Platform_Invalid.getErrorCode());
        }
        long expiresAt = System.currentTimeMillis() + (facebookLongLiveTokenDTO.getExpires_in() * 1000L);
        connector.setToken(facebookLongLiveTokenDTO.getAccess_token());
        connector.setExpirationTime(expiresAt);
        connectorRepository.save(connector);
        log.info("===========================Done========================");
    }

    public Connector populateUserInfo(Connector connector) {
        String url = String.format("https://graph.facebook.com/me?fields=id,email&access_token=%s", connector.getToken());
        String response = apiService.callExternalApi(url, "GET", null, null);
        FacebookUserInfoDTO facebookUserInfoDTO = JsonUtil.getObjectFromJsonString(response, FacebookUserInfoDTO.class);
        if(nonNull(facebookUserInfoDTO.getError())) {
            throw new BadRequestException(facebookUserInfoDTO.getError().getMessage(), ErrorCodes.Platform_Error.getErrorCode());
        }
        connector.setPlatformUserId(facebookUserInfoDTO.getId());
        connector.setEmail(facebookUserInfoDTO.getEmail());
        return connector;
    }
}