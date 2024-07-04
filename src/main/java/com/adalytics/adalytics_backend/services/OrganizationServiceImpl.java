package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.Role;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.models.entities.Organization;
import com.adalytics.adalytics_backend.models.entities.User;
import com.adalytics.adalytics_backend.models.requestModels.OrganizationRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.UserResponseDTO;
import com.adalytics.adalytics_backend.models.requestModels.SignupRequestDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IOrganizationRepository;
import com.adalytics.adalytics_backend.repositories.interfaces.IUserRepository;
import com.adalytics.adalytics_backend.services.interfaces.IAuthService;
import com.adalytics.adalytics_backend.services.interfaces.IOrganizationService;
import com.adalytics.adalytics_backend.transformers.ConnectorTransformer;
import com.adalytics.adalytics_backend.utils.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceImpl implements IOrganizationService {

    @Autowired
    private IOrganizationRepository organizationRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IAuthService authService;
    @Autowired
    private ConnectorTransformer connectorTransformer;

    @Override
    public void createOrganization(OrganizationRequestDTO organizationRequestDTO) {
        if (organizationRequestDTO.getOrganizationName().isBlank()) {
            throw new BadRequestException("Organization Name is empty.", ErrorCodes.Organization_Name_Invalid.getErrorCode());
        }
        Organization organization = Organization.builder().name(organizationRequestDTO.getOrganizationName()).build();
        organizationRepository.save(organization);
        try {
            SignupRequestDTO signupRequestDTO = SignupRequestDTO.builder()
                    .email(organizationRequestDTO.getEmail())
                    .password(organizationRequestDTO.getPassword())
                    .role(Role.ADMIN.name())
                    .build();
            authService.signUp(signupRequestDTO, organization.getId());
        } catch (BadRequestException exception) {
            organizationRepository.deleteById(organization.getId());
            throw new BadRequestException(exception.getErrorMessage(), exception.getErrorCode());
        } catch (Exception exception) {
            organizationRepository.deleteById(organization.getId());
            throw exception;
        }

    }

    @Override
    public List<UserResponseDTO> getOrganizationUsers() {
        String orgId = ContextUtil.getCurrentOrgId();
        List<User> users = userRepository.findByOrganizationId(orgId);

        return connectorTransformer.convertToUserResponseDTOs(users);
    }
}
