package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.Role;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.models.entities.Organization;
import com.adalytics.adalytics_backend.models.requestModels.OrganizationRequestDTO;
import com.adalytics.adalytics_backend.models.requestModels.SignupRequestDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IOrganizationRepository;
import com.adalytics.adalytics_backend.services.interfaces.IAuthService;
import com.adalytics.adalytics_backend.services.interfaces.IOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationServiceImpl implements IOrganizationService {

    @Autowired
    private IOrganizationRepository organizationRepository;
    @Autowired
    private IAuthService authService;

    @Override
    public void createOrganization(OrganizationRequestDTO organizationRequestDTO) {
        if (organizationRequestDTO.getOrganizationName().isBlank()) {
            throw new BadRequestException("Organization Name is empty.", ErrorCodes.Organization_Name_Invalid.getErrorCode());
        }
        Organization organization = Organization.builder().name(organizationRequestDTO.getOrganizationName()).build();
        organizationRepository.save(organization);
        SignupRequestDTO signupRequestDTO = SignupRequestDTO.builder()
                .email(organizationRequestDTO.getEmail())
                .password(organizationRequestDTO.getPassword())
                .role(Role.ADMIN.name())
                .build();
        authService.signUp(signupRequestDTO, organization.getId());
    }
}
