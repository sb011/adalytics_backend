package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.models.requestModels.MetricRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.MetricResponseDTO;
import com.adalytics.adalytics_backend.services.interfaces.IMetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/metrics")
public class MetricController {
    @Autowired
    private IMetricService metricService;

    @PostMapping("/")
    public ResponseEntity<Void> createMetric(@RequestBody MetricRequestDTO metricRequestDTO) {
        metricService.createMetric(metricRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{metricId}")
    public ResponseEntity<Void> deleteMetric(@PathVariable String metricId) {
        metricService.deleteMetric(metricId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{metricId}")
    public ResponseEntity<MetricResponseDTO> getMetricById(@PathVariable String metricId) {
        return new ResponseEntity<>(metricService.getMetricById(metricId), HttpStatus.OK);
    }
}
