package com.adalytics.adalytics_backend.models.requestModels;

import lombok.Data;

@Data
public class MetricRequestDTO {
    private String id;
    private String name;
    private String metricType;
    private String verticalAxisProperty;
    private String groupId;
    private String organizationId;
}
