package com.adalytics.adalytics_backend.utils;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JsonUtil {

    public static <T> T getObjectFromJsonString(String jasonString, Class<T> responseType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jasonString, responseType);
        } catch (Exception ex) {
            log.info("error processing json string : ", ex);
            throw new BadRequestException("Error while processing Json string", ErrorCodes.Json_Processing_error.getErrorCode());
        }
    }
}