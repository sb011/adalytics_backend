package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.models.entities.Campaign;
import com.adalytics.adalytics_backend.models.requestModels.CampaignRangeDTO;
import com.adalytics.adalytics_backend.services.interfaces.ICampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    @Autowired
    private ICampaignService campaignService;

    @PostMapping
    public ResponseEntity<List<Campaign>> getAllCampaigns(@RequestBody(required = false) CampaignRangeDTO campaignRangeDTO){
        return new ResponseEntity<>(campaignService.getAllCampaigns(campaignRangeDTO), HttpStatus.OK);
    }
}
