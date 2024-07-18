package com.adalytics.adalytics_backend.transformers.mapper;

import com.adalytics.adalytics_backend.models.entities.User;
import com.adalytics.adalytics_backend.models.responseModels.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IUserMapper {
    List<UserResponseDTO> convertToUserResponseDTOs(List<User> userList);
}
