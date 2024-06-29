package com.adalytics.adalytics_backend.models.requestModels;

import lombok.Data;

@Data
public class OrganizationRequestDTO {
    private String organizationName;
    private String email;
    private String password;
}
