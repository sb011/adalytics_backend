package com.adalytics.adalytics_backend.transformers;

import com.adalytics.adalytics_backend.models.entities.Group;
import com.adalytics.adalytics_backend.models.responseModels.GroupResponseDTO;
import com.adalytics.adalytics_backend.transformers.mapper.IGroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupTransformer {
    private final IGroupMapper groupMapper;

    public List<GroupResponseDTO> convertToGroupResponseDTOs(List<Group> groupList) {
        return groupMapper.convertToGroupResponseDTOs(groupList);
    }

    public GroupResponseDTO convertToGroupResponseDTO(Group group) {
        return groupMapper.convertToGroupResponseDTO(group);
    }
}
