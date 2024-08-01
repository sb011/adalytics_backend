package com.adalytics.adalytics_backend.models.requestModels;

import lombok.Builder;
import lombok.Data;

@Data
public class CampaignRangeDTO {
    private Long startTime;
    private Long endTime;
}
