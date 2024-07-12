package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.exceptions.NotFoundException;
import com.adalytics.adalytics_backend.models.entities.Group;
import com.adalytics.adalytics_backend.models.requestModels.GroupRequestDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IGroupRepository;
import com.adalytics.adalytics_backend.services.interfaces.IGroupService;
import com.adalytics.adalytics_backend.utils.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
public class GroupServiceImpl implements IGroupService {
    @Autowired
    private IGroupRepository groupRepository;

    @Override
    public void createGroup(GroupRequestDTO groupRequestDTO) {
        if (isNull(groupRequestDTO)) {
            throw new BadRequestException("Invalid Request", ErrorCodes.Invalid_Request_Body.getErrorCode());
        }
        validateCreateGroupRequest(groupRequestDTO);
        Group group = null;
        if (isNull(groupRequestDTO.getId())) {
            group = Group.builder()
                    .name(groupRequestDTO.getName())
                    .organizationId(ContextUtil.getCurrentOrgId())
                    .build();
        } else {
            group = groupRepository.findById(groupRequestDTO.getId())
                    .orElseThrow(() -> new NotFoundException("Group not found", ErrorCodes.Group_Not_Found.getErrorCode()));
            group.setName(groupRequestDTO.getName());
        }

        groupRepository.save(group);
    }

    private void validateCreateGroupRequest(GroupRequestDTO groupRequestDTO) {
        if (isNull(groupRequestDTO.getName())) {
            throw new BadRequestException("Invalid Request", ErrorCodes.Invalid_Request_Body.getErrorCode());
        }
    }
}
