package com.adalytics.adalytics_backend.models.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.annotation.Documented;

@Data
@Document("users")
public class Users  {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
}
