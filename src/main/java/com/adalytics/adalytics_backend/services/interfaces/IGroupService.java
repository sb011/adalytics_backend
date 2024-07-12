package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.requestModels.GroupRequestDTO;

public interface IGroupService {
    void createGroup(GroupRequestDTO groupRequestDTO);
}
