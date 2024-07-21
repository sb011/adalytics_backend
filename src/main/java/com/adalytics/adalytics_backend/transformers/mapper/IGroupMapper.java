package com.adalytics.adalytics_backend.transformers.mapper;

import com.adalytics.adalytics_backend.models.entities.Group;
import com.adalytics.adalytics_backend.models.responseModels.GroupResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IGroupMapper {
    List<GroupResponseDTO> convertToGroupResponseDTOs(List<Group> groupList);
    GroupResponseDTO convertToGroupResponseDTO(Group group);
}
