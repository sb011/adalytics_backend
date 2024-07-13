package com.adalytics.adalytics_backend.models.responseModels;

import lombok.Data;

@Data
public class MetricResponseDTO {
    private String id;
    private String name;
    private String metricType;
    private String verticalAxisProperty;
    private String groupId;
}
