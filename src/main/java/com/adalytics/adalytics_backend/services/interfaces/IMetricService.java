package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.requestModels.MetricRequestDTO;

public interface IMetricService {
    void createMetric(MetricRequestDTO metricRequestDTO);
}
