package com.adalytics.adalytics_backend.transformers;

import com.adalytics.adalytics_backend.models.entities.Metric;
import com.adalytics.adalytics_backend.models.requestModels.MetricRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.MetricResponseDTO;
import com.adalytics.adalytics_backend.transformers.mapper.IMetricMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MetricTransformer {
    private final IMetricMapper metricMapper;
    public Metric convertToMetric(MetricRequestDTO metricRequestDTO) {
        return metricMapper.convertToMetric(metricRequestDTO);
    }

    public List<MetricResponseDTO> convertToMetricResponseDTOs(List<Metric> metricList) {
        return metricMapper.convertToMetricResponseDTOs(metricList);
    }
}
