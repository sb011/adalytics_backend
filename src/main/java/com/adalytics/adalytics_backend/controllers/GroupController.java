package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.models.requestModels.GroupRequestDTO;
import com.adalytics.adalytics_backend.services.interfaces.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {
    @Autowired
    private IGroupService groupService;

    @PostMapping("/")
    public ResponseEntity<Void> createGroup(@RequestBody GroupRequestDTO groupRequestDTO) {
        groupService.createGroup(groupRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
