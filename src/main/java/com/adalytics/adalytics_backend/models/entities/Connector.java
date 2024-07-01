package com.adalytics.adalytics_backend.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("connectors")
public class Connector {
    @Id
    private String id;
    private String organizationId;
    /**
     * {@link com.adalytics.adalytics_backend.enums.Platform}
     */
    private String platform;
    private String platformUserId;
    private String token;
    private Long expirationTime;
}
