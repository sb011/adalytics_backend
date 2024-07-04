package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.Role;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.models.entities.Organization;
import com.adalytics.adalytics_backend.models.requestModels.InviteMemberDTO;
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
import com.adalytics.adalytics_backend.utils.EmailHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
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
    @Autowired
    private EmailHelper emailHelper;

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
            emailHelper.createTokenAndSendVerificationMail(signupRequestDTO.getEmail());
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

    @Override
    public void inviteMembers(List<InviteMemberDTO> inviteMemberDTOList) {
        if(isEmpty(inviteMemberDTOList)) {
            return;
        }
        inviteMemberDTOList.forEach(member -> {
            SignupRequestDTO signupRequestDTO = SignupRequestDTO.builder()
                    .email(member.getEmail())
                    .password(generateRandomPassword())
                    .role(Role.USER.name())
                    .build();
            try {
                authService.signUp(signupRequestDTO, ContextUtil.getCurrentOrgId());
                String organizationName = organizationRepository.findById(ContextUtil.getCurrentOrgId()).map(Organization::getName).orElse("");
                emailHelper.sendInvitationMail(organizationName, member.getEmail(), signupRequestDTO.getPassword());
            } catch (Exception ex) {
                log.error("Failed to invite member : {} with an exception :", member.getEmail(), ex);
            }
        });
    }

    private String generateRandomPassword() {
        //TODO : will write code to generate random strong password
        return "Abcd@1234";
    }
}
