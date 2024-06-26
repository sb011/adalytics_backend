package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.models.entities.Connector;
import com.adalytics.adalytics_backend.models.requestModels.ConnectorRequestDTO;
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

    @GetMapping("/{userId}")
    public ResponseEntity<List<Connector>> getAllConnectors(@PathVariable("userId") String userId) {
        return new ResponseEntity<>(connectorService.fetchAllConnectors(userId), HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<Connector> removeConnector(@RequestBody ConnectorRequestDTO connectorRequestDTO) {
        connectorService.removeConnector(connectorRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
