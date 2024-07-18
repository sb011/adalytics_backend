package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.repositories.interfaces.IConnectorRepository;
import com.adalytics.adalytics_backend.services.interfaces.ICampaignService;
import com.adalytics.adalytics_backend.services.platformClientImpl.GoogleClientImpl;
import com.adalytics.adalytics_backend.utils.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.adalytics.adalytics_backend.enums.Platform.GOOGLE;

@Service
public class CampaignServiceImpl implements ICampaignService {

    @Autowired
    IConnectorRepository connectorRepository;
    @Autowired
    private GoogleClientImpl googleClient;
    @Override
    public void getAllCampaigns() {
        List<Connector> connectorList = connectorRepository.findByOrganizationId(ContextUtil.getCurrentOrgId());
        List<Connector> googleConnectors = connectorList.stream().filter(connector -> GOOGLE.name().equals(connector.getPlatform())).toList();
        googleConnectors.forEach(connector -> {
            googleClient.fetchCampaigns(connector);
        });
    }
}
