package com.adalytics.adalytics_backend.models.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("tokens")
public class Token {
    private String id;
    private String email;
    private String token;
    private Long expirationTime;

    public boolean isExpired(Long currentTime) {
        return this.expirationTime.compareTo(currentTime) < 0;
    }
}