package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.entities.Campaign;

import java.util.List;

public interface ICampaignService {
    List<Campaign> getAllCampaigns();
}
