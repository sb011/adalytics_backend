package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.models.entities.Organization;
import com.adalytics.adalytics_backend.models.requestModels.OrganizationRequestDTO;
import com.adalytics.adalytics_backend.models.requestModels.SignupRequestModel;
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
        Organization organization = Organization.builder().name(organizationRequestDTO.getOrganizationName()).build();
        organizationRepository.save(organization);
        SignupRequestModel signupRequestModel = SignupRequestModel.builder()
                .email(organizationRequestDTO.getEmail())
                .password(organizationRequestDTO.getPassword())
                .build();
        authService.signUp(signupRequestModel, organization.getId());
    }
}
