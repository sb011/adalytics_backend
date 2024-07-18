package com.adalytics.adalytics_backend.transformers;

import com.adalytics.adalytics_backend.models.entities.User;
import com.adalytics.adalytics_backend.models.responseModels.UserResponseDTO;
import com.adalytics.adalytics_backend.transformers.mapper.IUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserTransformer {
    private final IUserMapper userMapper;
    public List<UserResponseDTO> convertToUserResponseDTOs(List<User> userList) {
        return userMapper.convertToUserResponseDTOs(userList);
    }
}
