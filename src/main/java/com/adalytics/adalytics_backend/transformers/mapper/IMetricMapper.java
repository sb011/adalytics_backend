package com.adalytics.adalytics_backend.transformers.mapper;

import com.adalytics.adalytics_backend.models.entities.Metric;
import com.adalytics.adalytics_backend.models.requestModels.MetricRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.MetricResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IMetricMapper {
    Metric convertToMetric(MetricRequestDTO metricRequestDTO);

    List<MetricResponseDTO> convertToMetricResponseDTOs(List<Metric> metricList);
}
