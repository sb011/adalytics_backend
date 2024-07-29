package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.requestModels.MetricRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.MetricResponseDTO;

public interface IMetricService {
    MetricResponseDTO createMetric(MetricRequestDTO metricRequestDTO);
    void deleteMetric(String metricId);
    MetricResponseDTO getMetricById(String metricId);
}
