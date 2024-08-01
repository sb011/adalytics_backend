package com.adalytics.adalytics_backend.constants;

import com.adalytics.adalytics_backend.enums.Flow;
import com.adalytics.adalytics_backend.enums.Platform;

import java.util.HashMap;
import java.util.Map;

public class CommonConstants {
    public static final String DEFAULT_USERNAME = "guest";
    public static final int JWT_TOKEN_EXPIRY = 1000 * 60 * 60 * 24 * 7;
    public static final int EMAIL_TOKEN_EXPIRY = 1000 * 60 * 15;
    public static final String ROLE = "ROLE";
    public static final String UNDERSCORE = "_";
    public static final String EMPTY_STRING = "";
    public static long oneWeekInMillis = 7 * 24 * 60 * 60 * 1000L;

    public static final Map<String, Flow> platformToFlowMap = new HashMap<String, Flow>() {{
        put(EMPTY_STRING, Flow.DEFAULT);
        put(Platform.FACEBOOK.name(), Flow.FACEBOOK);
        put(Platform.GOOGLE.name(), Flow.GOOGLE);
    }};
}
