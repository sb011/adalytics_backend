package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.models.requestModels.InviteMemberDTO;
import com.adalytics.adalytics_backend.models.requestModels.OrganizationRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.UserResponseDTO;
import com.adalytics.adalytics_backend.services.interfaces.IOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organization")
public class OrganizationController {

    @Autowired
    private IOrganizationService organizationService;

    @PostMapping("/")
    public ResponseEntity<Void> createOrganization(@RequestBody OrganizationRequestDTO organizationRequestDTO) {
        organizationService.createOrganization(organizationRequestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getOrganizationUsers() {
        return new ResponseEntity<>(organizationService.getOrganizationUsers(), HttpStatus.OK);
    }

    @PostMapping("/invite")
    public ResponseEntity<Void> inviteMembers(@RequestBody List<InviteMemberDTO> inviteMemberDTOList) {
        organizationService.inviteMembers(inviteMemberDTOList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        organizationService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
