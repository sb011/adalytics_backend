package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.requestModels.InviteMemberDTO;
import com.adalytics.adalytics_backend.models.requestModels.OrganizationRequestDTO;

import java.util.List;

public interface IOrganizationService {
    void createOrganization(OrganizationRequestDTO organizationRequestDTO);
    void inviteMembers(List<InviteMemberDTO> inviteMemberDTOList);
}
