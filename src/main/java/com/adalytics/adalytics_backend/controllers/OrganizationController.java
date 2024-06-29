package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.models.requestModels.OrganizationRequestDTO;
import com.adalytics.adalytics_backend.services.interfaces.IOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organization")
public class OrganizationController {

    @Autowired
    private IOrganizationService organizationService;

    @PostMapping("/create")
    public ResponseEntity<Void> createOrganization(@RequestBody OrganizationRequestDTO organizationRequestDTO) {
        organizationService.createOrganization(organizationRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
