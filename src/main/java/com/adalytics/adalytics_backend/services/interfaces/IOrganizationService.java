package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.requestModels.OrganizationRequestDTO;

public interface IOrganizationService {
    void createOrganization(OrganizationRequestDTO organizationRequestDTO);
}