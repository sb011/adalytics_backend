package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.models.entities.Campaign;
import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.requestModels.CampaignRangeDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IConnectorRepository;
import com.adalytics.adalytics_backend.services.interfaces.ICampaignService;
import com.adalytics.adalytics_backend.services.platformClientImpl.GoogleClientImpl;
import com.adalytics.adalytics_backend.utils.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.adalytics.adalytics_backend.constants.CommonConstants.oneWeekInMillis;
import static com.adalytics.adalytics_backend.enums.Platform.GOOGLE;
import static java.util.Objects.isNull;

@Service
public class CampaignServiceImpl implements ICampaignService {

    @Autowired
    IConnectorRepository connectorRepository;
    @Autowired
    private GoogleClientImpl googleClient;
    @Override
    public List<Campaign> getAllCampaigns(CampaignRangeDTO campaignRangeDTO) {
        populateDefaultTimeRangeIfNull(campaignRangeDTO);
        List<Campaign> campaignList = new ArrayList<>();
        List<Connector> connectorList = connectorRepository.findByOrganizationId(ContextUtil.getCurrentOrgId());
        List<Connector> googleConnectors = connectorList.stream().filter(connector -> GOOGLE.name().equals(connector.getPlatform())).toList();
        googleConnectors.forEach(connector -> {
            campaignList.addAll(googleClient.fetchCampaigns(connector, campaignRangeDTO));
        });
        return campaignList;
    }

    private void populateDefaultTimeRangeIfNull(CampaignRangeDTO campaignRangeDTO) {
        if(isNull(campaignRangeDTO)) {
            return;
        }
        if(isNull(campaignRangeDTO.getStartTime()) || isNull(campaignRangeDTO.getEndTime())) {
            long endTime = System.currentTimeMillis();
            long startTime = endTime - oneWeekInMillis;
            campaignRangeDTO.setStartTime(startTime);
            campaignRangeDTO.setEndTime(endTime);
        }
    }
}
