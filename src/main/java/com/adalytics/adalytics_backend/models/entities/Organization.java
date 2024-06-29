package com.adalytics.adalytics_backend.models.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("organizations")
@Builder
public class Organization {
    @Id
    private String id;
    private String name;
}
