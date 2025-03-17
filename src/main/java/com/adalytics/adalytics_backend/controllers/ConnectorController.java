package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.ConnectorResponseDTO;
import com.adalytics.adalytics_backend.services.interfaces.IConnectorService;
import com.adalytics.adalytics_backend.transformers.ConnectorTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@RequestMapping("/api/v1/connectors")
public class ConnectorController {

    @Autowired
    private IConnectorService connectorService;

    @Autowired
    private ConnectorTransformer connectorTransformer;

    @PostMapping("/")
    public ResponseEntity<ConnectorResponseDTO> addConnector(@RequestParam(value = "platform", required = false, defaultValue = "") String platform,@RequestBody ConnectorRequestDTO connectorRequestDTO) throws Exception {
        Connector connector = connectorService.addConnector(connectorRequestDTO);
        ConnectorResponseDTO connectorResponseDTO = connectorTransformer.convertToConnectorResponseDTO(connector);
        return new ResponseEntity<>(connectorResponseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/")
    public ResponseEntity<List<ConnectorResponseDTO>> getAllConnectors(@RequestParam(value = "platform", required = false, defaultValue = "") String platform) {
        return new ResponseEntity<>(connectorService.fetchAllConnectors(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeConnector(@RequestParam(value = "platform", required = false, defaultValue = "") String platform, @PathVariable("id") String id) {
        connectorService.removeConnector(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
