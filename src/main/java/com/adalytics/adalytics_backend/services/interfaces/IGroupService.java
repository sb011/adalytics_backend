package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.requestModels.GroupRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.GroupResponseDTO;

import java.util.List;

public interface IGroupService {
    GroupResponseDTO createGroup(GroupRequestDTO groupRequestDTO);
    List<GroupResponseDTO> getGroups();
    void deleteGroup(String groupId);
}
