package com.adalytics.adalytics_backend.models.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("metrics")
public class Metric {
    @Id
    private String id;
    private String name;
    private String metricType;
    private String verticalAxisProperty;
    private String groupId;
}