package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.models.entities.Campaign;
import com.adalytics.adalytics_backend.services.interfaces.ICampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    @Autowired
    private ICampaignService campaignService;

    @GetMapping
    public ResponseEntity<List<Campaign>> getAllCampaigns(){
        return new ResponseEntity<>(campaignService.getAllCampaigns(), HttpStatus.OK);
    }
}
