package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.ConnectorResponseDTO;
import com.adalytics.adalytics_backend.services.interfaces.IConnectorService;
import com.adalytics.adalytics_backend.services.invokers.ConnectorServiceInvoker;
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
    private ConnectorServiceInvoker connectorServiceInvoker;

    @PostMapping("/")
    public ResponseEntity<Void> addConnector(@RequestParam(value = "platform", required = false, defaultValue = "") String platform,@RequestBody ConnectorRequestDTO connectorRequestDTO) throws Exception {
        IConnectorService connectorService = connectorServiceInvoker.invoke(platform);
        connectorService.addConnector(connectorRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/")
    public ResponseEntity<List<ConnectorResponseDTO>> getAllConnectors(@RequestParam(value = "platform", required = false, defaultValue = "") String platform) {
        IConnectorService connectorService = connectorServiceInvoker.invoke(platform);
        return new ResponseEntity<>(connectorService.fetchAllConnectors(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeConnector(@RequestParam(value = "platform", required = false, defaultValue = "") String platform, @PathVariable("id") String id) {
        IConnectorService connectorService = connectorServiceInvoker.invoke(platform);
        connectorService.removeConnector(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/refresh/{id}")
    public ResponseEntity<Void> refreshAccessToken(@RequestParam(value = "platform", required = false, defaultValue = "") String platform, @PathVariable("id") String id) {
        IConnectorService connectorService = connectorServiceInvoker.invoke(platform);
        connectorService.refreshAccessToken(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
