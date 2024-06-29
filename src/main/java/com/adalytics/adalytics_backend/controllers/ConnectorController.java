package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.ConnectorResponseDTO;
import com.adalytics.adalytics_backend.services.interfaces.IConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/connectors")
public class ConnectorController {

    @Autowired
    private IConnectorService connectorService;

    @PostMapping("/")
    public ResponseEntity<Void> addConnector(@RequestBody ConnectorRequestDTO connectorRequestDTO) {
        connectorService.addConnector(connectorRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<ConnectorResponseDTO>> getAllConnectors() {
        return new ResponseEntity<>(connectorService.fetchAllConnectors(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeConnector(@PathVariable("id") String id) {
        connectorService.removeConnector(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
