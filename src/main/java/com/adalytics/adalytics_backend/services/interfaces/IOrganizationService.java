package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.requestModels.OrganizationRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.UserResponseDTO;

import java.util.List;

public interface IOrganizationService {
    void createOrganization(OrganizationRequestDTO organizationRequestDTO);
    List<UserResponseDTO> getOrganizationUsers();

    void deleteUser(String userId);
}
