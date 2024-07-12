package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.MetricType;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.exceptions.NotFoundException;
import com.adalytics.adalytics_backend.models.entities.Group;
import com.adalytics.adalytics_backend.models.entities.Metric;
import com.adalytics.adalytics_backend.models.requestModels.MetricRequestDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IGroupRepository;
import com.adalytics.adalytics_backend.repositories.interfaces.IMetricRepository;
import com.adalytics.adalytics_backend.services.interfaces.IMetricService;
import com.adalytics.adalytics_backend.transformers.ConnectorTransformer;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
public class MetricServiceImpl implements IMetricService {
    @Autowired
    private IMetricRepository metricRepository;
    @Autowired
    private IGroupRepository groupRepository;
    @Autowired
    private ConnectorTransformer connectorTransformer;

    @Override
    public void createMetric(MetricRequestDTO metricRequestDTO) {
        System.out.println(metricRequestDTO);
        if (isNull(metricRequestDTO)) {
            throw new BadRequestException("Invalid Request", ErrorCodes.Invalid_Request_Body.getErrorCode());
        }
        validateCreateMetricRequest(metricRequestDTO);

        Group group = null;
        if (!isNull(metricRequestDTO.getGroupId())) {
            group = groupRepository.findById(metricRequestDTO.getGroupId())
                    .orElseThrow(() -> new NotFoundException("Group not found", ErrorCodes.Group_Not_Found.getErrorCode()));
        }

        Metric metric = null;
        if (isNull(metricRequestDTO.getId())) {
            metric = connectorTransformer.convertToMetric(metricRequestDTO);
        } else {
            metric = metricRepository.findById(metricRequestDTO.getId())
                    .orElseThrow(() -> new NotFoundException("Metric not found", ErrorCodes.Metric_Not_Found.getErrorCode()));
            metric.setName(metricRequestDTO.getName());
            metric.setMetricType(metricRequestDTO.getMetricType());
            metric.setVerticalAxisProperty(metricRequestDTO.getVerticalAxisProperty());
            metric.setGroupId(metricRequestDTO.getGroupId());
        }
        metricRepository.save(metric);
    }

    private void validateCreateMetricRequest(MetricRequestDTO metricRequestDTO) {
        if (isNull(metricRequestDTO.getName())) {
            throw new BadRequestException("Name is required", ErrorCodes.Invalid_Metric_Name.getErrorCode());
        }

        if (isNull(metricRequestDTO.getMetricType()) || !EnumUtils.isValidEnum(MetricType.class, metricRequestDTO.getMetricType())) {
            throw new BadRequestException("MetricType is required", ErrorCodes.Invalid_Metric_Type.getErrorCode());
        }

        if (isNull(metricRequestDTO.getVerticalAxisProperty())) {
            throw new BadRequestException("YAxisProperty is required", ErrorCodes.Invalid_Metric_YAxisProperty.getErrorCode());
        }
    }
}
