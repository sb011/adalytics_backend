package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.services.interfaces.ICampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    @Autowired
    private ICampaignService campaignService;

    @GetMapping
    public void getAllCampaigns(){
        campaignService.getAllCampaigns();
    }
}
