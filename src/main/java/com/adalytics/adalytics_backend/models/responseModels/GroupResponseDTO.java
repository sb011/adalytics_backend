package com.adalytics.adalytics_backend.models.responseModels;

import lombok.Data;

import java.util.List;

@Data
public class GroupResponseDTO {
    private String id;
    private String name;
    private List<MetricResponseDTO> metrics;
}
