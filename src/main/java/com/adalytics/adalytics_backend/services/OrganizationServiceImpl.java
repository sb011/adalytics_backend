package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.Role;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.models.entities.Organization;
import com.adalytics.adalytics_backend.models.requestModels.InviteMemberDTO;
import com.adalytics.adalytics_backend.models.requestModels.OrganizationRequestDTO;
import com.adalytics.adalytics_backend.models.requestModels.SignupRequestModel;
import com.adalytics.adalytics_backend.repositories.interfaces.IOrganizationRepository;
import com.adalytics.adalytics_backend.services.interfaces.IAuthService;
import com.adalytics.adalytics_backend.services.interfaces.IOrganizationService;
import com.adalytics.adalytics_backend.utils.ContextUtil;
import com.adalytics.adalytics_backend.utils.EmailHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class OrganizationServiceImpl implements IOrganizationService {

    @Autowired
    private IOrganizationRepository organizationRepository;
    @Autowired
    private IAuthService authService;
    @Autowired
    private EmailHelper emailHelper;

    @Override
    public void createOrganization(OrganizationRequestDTO organizationRequestDTO) {
        if (organizationRequestDTO.getOrganizationName().isBlank()) {
            throw new BadRequestException("Organization Name is empty.", ErrorCodes.Organization_Name_Invalid.getErrorCode());
        }
        Organization organization = Organization.builder().name(organizationRequestDTO.getOrganizationName()).build();
        organizationRepository.save(organization);
        SignupRequestModel signupRequestModel = SignupRequestModel.builder()
                .email(organizationRequestDTO.getEmail())
                .password(organizationRequestDTO.getPassword())
                .role(Role.ADMIN.name())
                .build();
        authService.signUp(signupRequestModel, organization.getId());
        emailHelper.createTokenAndSendVerificationMail(signupRequestModel.getEmail());
    }

    @Override
    public void inviteMembers(List<InviteMemberDTO> inviteMemberDTOList) {
        if(isEmpty(inviteMemberDTOList)) {
            return;
        }
        inviteMemberDTOList.forEach(member -> {
            SignupRequestModel signupRequestModel = SignupRequestModel.builder()
                    .email(member.getEmail())
                    .password(generateRandomPassword())
                    .role(Role.USER.name())
                    .build();
            authService.signUp(signupRequestModel, ContextUtil.getCurrentOrgId());
            emailHelper.sendInvitationMail(member.getEmail(), signupRequestModel.getPassword());
        });
    }

    private String generateRandomPassword() {
        //TODO : will write code to generate random strong password
        return "Abcd@1234";
    }
}
