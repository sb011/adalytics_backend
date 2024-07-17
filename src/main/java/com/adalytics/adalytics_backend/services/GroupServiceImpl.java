package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.exceptions.NotFoundException;
import com.adalytics.adalytics_backend.models.entities.Group;
import com.adalytics.adalytics_backend.models.entities.Metric;
import com.adalytics.adalytics_backend.models.requestModels.GroupRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.GroupResponseDTO;
import com.adalytics.adalytics_backend.repositories.interfaces.IGroupRepository;
import com.adalytics.adalytics_backend.repositories.interfaces.IMetricRepository;
import com.adalytics.adalytics_backend.services.interfaces.IGroupService;
import com.adalytics.adalytics_backend.transformers.GroupTransformer;
import com.adalytics.adalytics_backend.transformers.MetricTransformer;
import com.adalytics.adalytics_backend.utils.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class GroupServiceImpl implements IGroupService {
    @Autowired
    private IGroupRepository groupRepository;
    @Autowired
    private IMetricRepository metricRepository;
    @Autowired
    private GroupTransformer groupTransformer;
    @Autowired
    private MetricTransformer metricTransformer;

    @Override
    public void createGroup(GroupRequestDTO groupRequestDTO) {
        if (isNull(groupRequestDTO)) {
            throw new BadRequestException("Invalid Request", ErrorCodes.Invalid_Request_Body.getErrorCode());
        }
        validateCreateGroupRequest(groupRequestDTO);
        Group group = null;
        if (isNull(groupRequestDTO.getId())) {
            group = Group.builder()
                    .name(groupRequestDTO.getName())
                    .organizationId(ContextUtil.getCurrentOrgId())
                    .build();
        } else {
            group = groupRepository.findById(groupRequestDTO.getId())
                    .orElseThrow(() -> new NotFoundException("Group not found", ErrorCodes.Group_Not_Found.getErrorCode()));
            group.setName(groupRequestDTO.getName());
        }

        groupRepository.save(group);
    }

    public List<GroupResponseDTO> getGroups() {
        List<Group> groups = groupRepository.findAllByOrganizationId(ContextUtil.getCurrentOrgId());
        List<Metric> metrics = metricRepository.findAllByOrganizationId(ContextUtil.getCurrentOrgId());
        List<GroupResponseDTO> groupResponseDTOsWithoutMetric = groupTransformer.convertToGroupResponseDTOs(groups);

        return groupResponseDTOsWithoutMetric.stream().peek(groupResponseDTO -> {
            List<Metric> metricList = metrics.stream().filter(metric -> metric.getGroupId().equals(groupResponseDTO.getId())).collect(Collectors.toList());
            groupResponseDTO.setMetrics(metricTransformer.convertToMetricResponseDTOs(metricList));
        }).toList();
    }

    public void deleteGroup(String groupId) {
        List<Metric> metrics = metricRepository.findAllByGroupId(groupId);
        metricRepository.deleteAll(metrics);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found", ErrorCodes.Group_Not_Found.getErrorCode()));
        groupRepository.delete(group);
    }

    private void validateCreateGroupRequest(GroupRequestDTO groupRequestDTO) {
        if (isNull(groupRequestDTO.getName())) {
            throw new BadRequestException("Invalid Request", ErrorCodes.Invalid_Request_Body.getErrorCode());
        }
    }
}
