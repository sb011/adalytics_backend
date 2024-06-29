package com.adalytics.adalytics_backend.transformers.mapper;

import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.ConnectorResponseDTO;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IConnectorMapper {

    List<ConnectorResponseDTO> convertToConnectorResponseDTOs(List<Connector> connectorList);

    Connector convertToConnector(ConnectorRequestDTO connectorRequestDTO);
}