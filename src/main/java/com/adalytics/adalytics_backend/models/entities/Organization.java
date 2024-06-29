package com.adalytics.adalytics_backend.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("organizations")
@NoArgsConstructor
public class Organization {
    @Id
    private String id;
    private String name;

    public Organization(String name) {
        this.name = name;
    }
}
